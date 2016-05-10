package scripting;

import scripting.cli.CLICommand;
import scripting.cli.CLISession;
import scripting.commands.EchoCommand;
import scripting.commands.GetATLCommand;
import scripting.commands.PerformSyncCommand;
import scripting.tcl.TclConvertor;
import scripting.tcl.TclInterpUtils;
import scripting.tcl.TclSource;
import tcl.lang.Interp;
import tcl.lang.TCL;
import tcl.lang.TclException;
import util.JAXBLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ScriptEngine {
    private Interp interp;
    private String scriptID;
    private String host;
    private StringBuilder responseBuilder;

    private TclConvertor tclConvertor;
    private ScriptBean scriptBean;

    private Map<String, String> globalVars;

    private ScriptEngine(ScriptBean bean) {
        scriptID = bean.getName();
        scriptBean = bean;
        globalVars = new HashMap<>();
        responseBuilder = new StringBuilder();
    }

    public static ScriptEngine newInstance(String scriptName) {

        // verify script exists
        if (!JAXBLoader.fileExists(scriptName)) {
            return null;
        }

        // create and initialize the engine
        ScriptEngine engine = null;
        try {
            ScriptBean scriptBean = JAXBLoader.loadScript(scriptName);
            engine = new ScriptEngine(scriptBean);

            List<SourceMapping> settingsList = JAXBLoader.loadScriptSettings();
            if (!engine.init(settingsList)) {
                System.out.println("Failed to init engine!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return engine;
    }


    public String getEngineName() {
        return "Extreme Tcl";
    }

    public String getScriptName() {
        return (scriptBean != null) ? scriptBean.getName() : "";
    }

    public ScriptResponse runAndWait(Map<String, String> localVars) {
        ScriptResponse response = new ScriptResponse();
        ScriptingClient svc = ScriptingClient.INSTANCE;
        ScriptContext request = new ScriptContext(scriptBean);

        request.setScriptEngine(this);
        request.addLocalVars(localVars);

        response = svc.submitAndWait(request, 10);

        return response;
    }

    public Future<ScriptResponse> runAsync(Map<String, String> localVars) {
        ScriptingClient svc = ScriptingClient.INSTANCE;
        ScriptContext request = new ScriptContext(scriptBean);
        request.setScriptEngine(this);
        request.addLocalVars(localVars);

        System.out.println("Submitting request: " + request.getScriptName());
        Future<ScriptResponse> response = svc.submit(request);

        return response;
    }

    public boolean runAsync(Map<String, String> localVars, ScriptListener listener) {
        ScriptingClient svc = ScriptingClient.INSTANCE;
        ScriptContext request = new ScriptContext(scriptBean);
        request.setScriptEngine(this);
        request.addLocalVars(localVars);

        System.out.println("Submitting request: " + request.getScriptName());
        svc.submit(request, listener);

        return true;
    }

    protected boolean init(List<SourceMapping> settings) {
        System.out.println("Compiling: " + scriptID);
        tclConvertor = new TclConvertor(settings);

        if (interp == null) {
            interp = getInterp();
        }
        return interp != null;
    }

    public void eval(Map<String, ?> localVars) {

        try {
            // convertScript original tcl script
            String script = convertScript();

            // update/set local variables to the script
            script = setLocalVariableValues(script, localVars);

            // update/set the global vars in the interp
            addGlobalVariables(globalVars);
            //addGlobalVariables(localVars);

            // interpret tcl script
            interp.eval(script);

        } catch (TclException e) {
            switch (e.getCompletionCode()) {
                case TCL.ERROR: {
                    String sep = System.getProperty("line.separator");
                    StringBuilder strError = new StringBuilder(sep);
                    strError.append("*** Error at line - ").append(interp.getErrorLine()).append(" ***").append(sep).toString();
//                    out.write(strError.toString());
//                    out.write(interp.getResult().toString() + sep);
//                    context.put(CommunicatorKeyNames.ERROR_MESSAGE_KEY, interp.getResult().toString());
                    // ex.getMessage does not return error message. Create a new exception so that ex.getMessage() works
                    TclException newEx = new TclException(interp, interp.getResult().toString());
                    newEx.setCompletionCode(TCL.ERROR);
                    //throw newEx;
                    System.out.println(strError.toString());
                }
                default:
                    System.out.println("ERROR: " + e);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        }
    }

    public void appendResponse(String line) {
        responseBuilder.append(line);
    }

    public String getResponse() {
        return responseBuilder.toString();
    }

    public void close() {
        if (interp != null) {
            interp.dispose();
        }
    }

    protected String convertScript() throws Exception {
        String source = null;

        TclSource tclSource = tclConvertor.convertScriptToTcl(interp, scriptBean.getContent());
        source = tclSource.getSourceCode();
        if (source == null) {
            source = tclSource.getOriginalSourceCode();
        }

        return source;
    }

    protected String setLocalVariableValues(String origScript, Map<String, ?> varMap) {
        String[] linesArray = origScript.split(System.getProperty("line.separator"));
        StringBuilder updatedScript = new StringBuilder("");

        int count = 0;
        for (String line : linesArray) {

            if ((line = line.trim()).length() == 0) {
                updatedScript.append(line).append((count < linesArray.length - 1) ? "\n" : "");
                count++;
                continue; // ignore blank lines
            }
            if (line.charAt(0) != '#' && !(line.toLowerCase().trim().startsWith("set"))) {
                updatedScript.append(line).append((count < linesArray.length - 1) ? "\n" : "");
                count++;
                continue;
            }

            for (Map.Entry<String, ?> varValue : varMap.entrySet()) {
                // skip port (system) variable
                if (varValue.getKey() != null && varValue.getKey().equalsIgnoreCase(Script.SYSTEM_VARIABLES.PORT.toString())) {
                    continue;
                }
                String regEx = "(?i)" + "(set" + "[\\s]*" + (varValue.getKey().startsWith("$") ? "\\" + varValue.getKey() : varValue.getKey()) + ")"
                        + "([\\s]*)" + "([.a-zA-Z0-9_-]*[\\s])*";

                Matcher matcher = Pattern.compile(regEx).matcher(line);
                if (line.charAt(0) != '#' && matcher.find()) {
                    String value = varValue.getValue().toString();
                    if (value.length() == 0) {
                        value = "\"\"";
                    }
                    if (value.trim().indexOf(" ") != -1 && value.trim().charAt(0) != '"') {
                        value = "\"" + value + "\"";
                    }

                    if (matcher.group(2) != null && matcher.group(2).length() > 0) {
                        line = matcher.group(1) + matcher.group(2) + value;
                    } else if (matcher.group(3) == null || matcher.group(3).length() == 0) {
                        line = matcher.group(1) + " " + value;
                    }
                }
            }
            updatedScript.append(line).append((count < linesArray.length - 1) ? "\n" : "").append(System.getProperty("line.separator"));
            count++;
        }
        return updatedScript.toString();
    }

    public String toString() {
        return getEngineName() + " - " + getScriptName();
    }

    private void convertVarNames(Map<String,String> deviceProps,Map<String, String> scriptVars){
        Map<String, String> keyMapping = new HashMap<>();
        keyMapping.put("name", Script.SYSTEM_VARIABLES.DEVICE_NAME.getDisplayString());
        keyMapping.put("ipStr", Script.SYSTEM_VARIABLES.DEVICE_IP.getDisplayString());
        keyMapping.put("CLI_LOGIN", Script.SYSTEM_VARIABLES.DEVICE_LOGIN.getDisplayString());
        keyMapping.put("DEVICE_TYPE", Script.SYSTEM_VARIABLES.DEVICE_TYPE.getDisplayString());
        keyMapping.put("softwareVersion", Script.SYSTEM_VARIABLES.DEVICE_SOFTWARE_VERSION.getDisplayString());
        keyMapping.put("vendor", Script.SYSTEM_VARIABLES.VENDOR.getDisplayString());
        keyMapping.put("HOSTNAME", Script.SYSTEM_VARIABLES.SERVER_NAME.getDisplayString());
        keyMapping.put("CLI_IS_SSH", Script.SYSTEM_VARIABLES.CLI_SESSION_TYPE.getDisplayString());
        keyMapping.put("XOS_CAPABLE", Script.SYSTEM_VARIABLES.IS_EXOS.getDisplayString());
        keyMapping.put(Script.SYSTEM_VARIABLES.NMS_SERVER_ADDRESS.getDisplayString(), "SERVER_ADDRESS");
        keyMapping.put(Script.SERVER_PROPERTY.IP_ADDRESS.getDisplayString(), Script.SYSTEM_VARIABLES.SERVER_IP.getDisplayString());
        keyMapping.put(Script.SERVER_PROPERTY.HTTP_PORT.getDisplayString(), Script.SYSTEM_VARIABLES.SERVER_PORT.getDisplayString());

        if (deviceProps.containsKey("CLI_IS_SSH")) {
            scriptVars.put(Script.SYSTEM_VARIABLES.CLI_SESSION_TYPE.getDisplayString(),
                    (Boolean.parseBoolean(deviceProps.get("CLI_IS_SSH")) ? "ssh" : "telnet")); //??
        }

        if (deviceProps.containsKey("XOS_CAPABLE")) {
            scriptVars.put(Script.SYSTEM_VARIABLES.IS_EXOS.getDisplayString(),
                    deviceProps.get("XOS_CAPABLE"));
        }

        scriptVars.put(Script.SYSTEM_VARIABLES.PORT.getDisplayString(),
                "TODO get port from request context"); //??

        for (Map.Entry<String, ?> entry : deviceProps.entrySet()) {
            String key = entry.getKey();
            if (keyMapping.containsKey(key)) {
                key = keyMapping.get(key);
            }
            scriptVars.put(key, entry.getValue().toString());
        }

    }

    private void addServerVars(Map<String, String> scriptVars){
        scriptVars.put(Script.SYSTEM_VARIABLES.DATE.getDisplayString(), (new SimpleDateFormat("yyyy-MM-dd").format(new Date())).toString());
        scriptVars.put(Script.SYSTEM_VARIABLES.TIME.getDisplayString(), (new SimpleDateFormat("HH:mm:ss z").format(new Date())).toString());
        scriptVars.put(Script.SYSTEM_VARIABLES.STATUS.getDisplayString(), "1");
        scriptVars.put("ridgeline.scripting.abort_on_error", "yes"); //??


        // needs the request context
        scriptVars.put(Script.SYSTEM_VARIABLES.NETSIGHT_USER.getDisplayString(),
                "TODO get Username from request context"); //??


    }

    private void addDefaultGlobalVars(Map<String,String> scriptVars){
        for(Script.SYSTEM_VARIABLES e:Script.SYSTEM_VARIABLES.values()){
            scriptVars.put(e.getDisplayString(),"");
        }

        // add script settings
        scriptVars.put(Script.SYSTEM_VARIABLES.AUDITLOG_ENABLED.getDisplayString(),scriptBean.getAuditLogEnabled());
        scriptVars.put(Script.SYSTEM_VARIABLES.SCRIPT_TIMEOUT.getDisplayString(),scriptBean.getScriptTimeout());
        scriptVars.put(Script.SYSTEM_VARIABLES.SCRIPT_OWNER.getDisplayString(),scriptBean.getScriptOwner());
        scriptVars.put(Script.SYSTEM_VARIABLES.VENDOR.getDisplayString(),scriptBean.getVendor());

        // add cli vars
        scriptVars.put(Script.SYSTEM_VARIABLES.CLIOUT.getDisplayString(), " ");

//        scriptVars.put(Script.SYSTEM_VARIABLES.CLI_CMD_SAVE.getDisplayString(), "");
//        scriptVars.put(Script.SYSTEM_VARIABLES.CLI_PROMPT_CMD.getDisplayString(), "");
//        scriptVars.put(Script.SYSTEM_VARIABLES.CLI_PROMPT_CMD_REPLY.getDisplayString(), "");
//        scriptVars.put(Script.SYSTEM_VARIABLES.CLI_PROMPT_SHELL.getDisplayString(), "");
//        scriptVars.put(Script.SYSTEM_VARIABLES.CLI_PROMPT_MORE.getDisplayString(), "");
//        scriptVars.put(Script.SYSTEM_VARIABLES.CLI_PROMPT_MORE_REPLY.getDisplayString(), "");
    }

    private void addGlobalVariables(Map<String, String> deviceProps) throws TclException {

        Map<String, String> scriptVars = new HashMap<>();

        // load all the default and global vars
        addDefaultGlobalVars(scriptVars);

        addServerVars(scriptVars);

        // convert property names to match script names
        convertVarNames(deviceProps,scriptVars);

        setInterpVars(scriptVars);
    }

    private void setInterpVars(Map<String, String> map) {

        for (Map.Entry<String, String> entry : map.entrySet()) {
            try {
                interp.setVar(entry.getKey(), null, entry.getValue(), TCL.GLOBAL_ONLY);
            } catch (TclException e) {
                e.printStackTrace();
            }
        }

    }

    private Map<String, String> getDefaultGlobalVariables() {
        Map<String, String> vars = new HashMap<>();


        // Device Props
//                DEVICE_NAME("deviceName", "Dns name of selected device."),
//                DEVICE_IP("deviceIP", "IP address of the selected device."),
//                DEVICE_TYPE("deviceType", "Device type of the selected device."),
//                DEVICE_SOFTWARE_VERSION("deviceSoftwareVer","Software image version nunmber on the device."),
//                IS_EXOS("isExos", "true / false. if this device is XOS device?"),
//                VENDOR("vendor","Extreme, if it is Extreme device. Vendor name, otherwise."),

        // CLI Creds
//                DEVICE_LOGIN("deviceLogin", "Login user for the selected device."),
//                PORT("port", "Selected ports, represented in one string."),
//                CLI_SESSION_TYPE("CLI.SESSION_TYPE","current session type (telnet / ssh)"),

        // Server Props
//                NMS_SERVER_ADDRESS("nmsServerAddress","Server host name if multiple interfaces enabled, ip address if one interface."),
//                SERVER_NAME("serverName","Host machine name of the server."),
//                SERVER_IP("serverIP", "Server IP Address."),
//                SERVER_PORT("serverPort", "Server web port."),
//                TIME("time","Current time at server (HH:mm:ss z)."),
//                DATE("date","Current date at server (yyyy-MM-dd)."),

        // Script Props
//                ABORT_ON_ERROR("abort_on_error","0: abort on error flag is set; 1: otherwise"),
//                //EPICENTER_USER("epicenterUser", "User running script."),
//                NETSIGHT_USER("netsightUser", "User running script."),
//                CLIOUT("CLI.OUT", "Output of the last CLI command."),
//                STATUS("STATUS", "Last CLI command execution status. 0: success, non-zero: not successful."),


        return vars;
    }

    private void setGlobalVariables(Map<String, String> deviceProps) {
        globalVars.putAll(deviceProps);
    }

    protected Interp getInterp() {
        Interp interp = null;

        try {
            File scriptFile = new File(Paths.get("scripting", "bundled_procs.tcl").toString());
            if (!scriptFile.exists()) {
                System.out.println("failed to find: " + scriptFile.getCanonicalPath());
                return null;
                //   LOG.error("Could not load data file bundled_procs.tcl. File missing. Procedures inside this cannot be used.");
            }
            interp = new TclInterpUtils().getTclInterpreter();

            String cliRuleName = "";
            CLISession cliSession =  CLISession.newInstance(cliRuleName);
            CLICommand cmd = new CLICommand(this);
            cmd.setCliSession(cliSession);
            interp.createCommand(TclInterpUtils.CUSTOM_TCL_COMMAND.CLI.getDisplayString(), cmd);
            cmd = new CLICommand(this,true);
            cmd.setCliSession(cliSession);
            interp.createCommand(TclInterpUtils.CUSTOM_TCL_COMMAND.CLINoWait.getDisplayString(),cmd);
            interp.createCommand(TclInterpUtils.CUSTOM_TCL_COMMAND.SYNC.getDisplayString(), new PerformSyncCommand(this));
            interp.createCommand(TclInterpUtils.CUSTOM_TCL_COMMAND.ECHO.getDisplayString(), new EchoCommand(this));
            interp.createCommand(TclInterpUtils.CUSTOM_TCL_COMMAND.GetATLValue.getDisplayString(), new GetATLCommand(this));

            interp.evalFile(scriptFile.getCanonicalPath());
        } catch (TclException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
            //  LOG.error("Could not load data file bundled_procs.tcl. Procedures inside this cannot be used.", ex);
        }
        return interp;
    }

    private static void testWait() {
        Map<String, String> globalVars = new HashMap<>();
        globalVars.put("USERNAME", "fish");
        globalVars.put("PASSWORD", "leave");
        globalVars.put("DEVICE_SSH_ENABLED", "true");
        globalVars.put("DEVICE_PORT", "888");
        globalVars.put("DEVICE_IP", "1.1.1.1");

        globalVars.put("userName", "testWait");

        ScriptEngine engine = ScriptEngine.newInstance("Hello world.xml");
        System.out.println(engine);
        //System.out.println("Submitting request: "+engine.getScriptName());
        engine.setGlobalVariables(globalVars);

        ScriptResponse response = engine.runAndWait(globalVars);
        System.out.println("Got response: " + response.getResponse());
    }

    private static void testFuture() {
        Map<String, String> globalVars = new HashMap<>();
        globalVars.put("userName", "testFuture");

        ScriptEngine engine = ScriptEngine.newInstance("Hello world.xml");
        System.out.println(engine);
        Future<ScriptResponse> future = engine.runAsync(globalVars);
        try {
            ScriptResponse response = future.get();
            System.out.println("Got Response: " + response.getResponse());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    private static void testListener() {
        Map<String, String> globalVars = new HashMap<>();
        globalVars.put("userName", "testListener");

        ScriptEngine engine = ScriptEngine.newInstance("Hello world.xml");
        System.out.println(engine);
        engine.runAsync(globalVars, new ScriptListener() {
            @Override
            public boolean beforeScriptRun(ScriptContext context) {
                System.out.println("Before: " + context.getScriptName());
                return true;
            }

            @Override
            public void afterScriptRun(ScriptContext context, ScriptResponse response) {
                System.out.println("After: " + context.getScriptName());
            }
        });

    }

    public static void main(String[] args) {
        testWait();

    }

}
