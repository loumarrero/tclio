package scripting;

import org.xml.sax.SAXException;
import scripting.commands.*;
import tcl.lang.Interp;
import tcl.lang.TCL;
import tcl.lang.TclException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


enum  Scripts {
    INSTANCE;

    Map<String, ScriptEngine.ScriptEntry> scriptMap;
    File updateDeltafile = null;
    File processedDeltafile = null;
    TclConvertor convertor;
    
    
   private Scripts() {        
        try {
            loadScripts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadScripts() {
        scriptMap = new HashMap<>();

        //        ScriptXMLParser<ScriptImpl> scriptsParser = new ScriptXMLParser<ScriptImpl>(ScriptImpl.class,"script","name");
//        scriptsParser.loadBeans("scripts",(d,f)->f.endsWith(".xml"));
//        scriptMap = scriptsParser.getBeans().stream().collect(Collectors.toMap(ScriptImpl::getName,c->new ScriptEntry(c)));
//        scriptMap.keySet().stream().forEach(System.out::println);

        String fileSeperator = System.getProperty("file.separator");
        String relativePath  = "scripting"+fileSeperator+"bundled_scripts";
        List<String> lines = new ArrayList<String>();
        BufferedReader in = null;
        updateDeltafile = new File(".",  relativePath + fileSeperator + "updated_scripts.list");
        processedDeltafile = new File (".",  relativePath + fileSeperator + "backup_updated_scripts.list");
        File scriptFolder = new File("scripting");
        try{

            ScriptXMLParser rulesParser = new ScriptXMLParser<SourceMapping>(SourceMapping.class,"SourceMapping","id");
            
            rulesParser.loadBeans(scriptFolder,(d,n)->n.equals("SyntaxConversionRules.xml"));
            convertor = new TclConvertor(rulesParser.getBeans());

            if(!updateDeltafile.exists()){
                System.out.println("Script index is missing: "+updateDeltafile.getAbsolutePath());
                return;
            }

            in = new BufferedReader(new FileReader(updateDeltafile));

            String inputLine = null;
            while ((inputLine = in.readLine()) != null)
            {
                lines.add(inputLine);
            }
            in.close();
        }
        catch (Exception e)
        {
            System.out.println("Error reading updated_scripts.list: "+e);
        }
        finally
        {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {

                }
        }

        for(String line: lines){
            StringBuilder stringBuilder = new StringBuilder(relativePath);
            stringBuilder.append(fileSeperator).append("xml").append(fileSeperator).append(line);
            File scriptXmlFile = new File (".", stringBuilder.toString());
            if(scriptXmlFile.exists()){
                ScriptXMLParser<ScriptBean> scriptsParser = new ScriptXMLParser<ScriptBean>(ScriptBean.class,"script","name");
                try {
                    scriptsParser.loadBeans(scriptXmlFile);
                    Map<String, ScriptEngine.ScriptEntry> map = scriptsParser.getBeans().stream().collect(Collectors.toMap(ScriptBean::getName, c->new ScriptEngine.ScriptEntry(c)));
                    //map.keySet().stream().forEach(System.out::println);
                    scriptMap.putAll(map);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }

        //scriptMap.keySet().stream().forEach(System.out::println);
    }
}


public class ScriptEngine {
    private Interp interp;
    private String scriptID;
    private String host;
    private StringBuilder responseBuilder;
    
    public ScriptEngine(String scriptID){
        this.scriptID = scriptID;
        responseBuilder = new StringBuilder();
    }
    
   
    public void eval(Map<String,?> localVars){
     
        if(interp==null){
            interp = getInterp();
        }
        
        try {
            
            // compile original tcl script 
            String script = compile(localVars);
            
            setGlobalVariables(localVars);
            
            interp.eval(script);

        } catch (TclException e) {
            switch (e.getCompletionCode())
            {
                case TCL.ERROR:
                {
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
                    System.out.println("ERROR: "+e);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally
        {
        }
    }
    
    public void append(String line){
        responseBuilder.append(line);
    }
    
    
    public String getResponse() {
        return responseBuilder.toString();
    }
    
    public void close() {
        if(interp!=null){
            interp.dispose();
        }
    }

    protected String compile(Map<String,?> localVars) throws Exception{
        String source=null;
        
        ScriptEngine.ScriptEntry scriptEntry = Scripts.INSTANCE.scriptMap.get(scriptID);
        if(scriptEntry ==null){
            System.out.println("Script not found: "+scriptID);
            return null;
        }
        
        ScriptBean scriptImpl = scriptEntry.scriptImpl;
        if(scriptEntry.tclSource==null){
            System.out.println("Compiling: "+scriptID);
            scriptEntry.tclSource = Scripts.INSTANCE.convertor.convertScriptToTcl(interp,scriptImpl.getContent());
        }

        source = scriptEntry.tclSource.getSourceCode();
        if(source == null){
            source = scriptEntry.tclSource.getOriginalSourceCode();
        }
        
        source = setLocalVariableValues(source,localVars);
        
        return source;
    }

    protected String setLocalVariableValues(String origScript, Map<String, ?> varMap)
    {
        String[] linesArray = origScript.split(System.getProperty("line.separator"));
        StringBuilder updatedScript = new StringBuilder("");

        int count = 0;
        for (String line : linesArray)
        {

            if ((line = line.trim()).length() == 0)
            {
                updatedScript.append(line).append((count < linesArray.length - 1) ? "\n" : "");
                count++;
                continue; // ignore blank lines
            }
            if (line.charAt(0) != '#' && !(line.toLowerCase().trim().startsWith("set")))
            {
                updatedScript.append(line).append((count < linesArray.length - 1) ? "\n" : "");
                count++;
                continue;
            }

            for (Map.Entry<String, ?> varValue : varMap.entrySet())
            {
                // skip port (system) variable
                if (varValue.getKey() != null && varValue.getKey().equalsIgnoreCase(Script.SYSTEM_VARIABLES.PORT.toString()))
                {
                    continue;
                }
                String regEx =  "(?i)" + "(set" + "[\\s]*" + (varValue.getKey().startsWith("$") ? "\\" + varValue.getKey() : varValue.getKey()) + ")"
                        + "([\\s]*)" + "([.a-zA-Z0-9_-]*[\\s])*";

                Matcher matcher = Pattern.compile(regEx).matcher(line);
                if (line.charAt(0) != '#' && matcher.find())
                {
                    String value = varValue.getValue().toString();
                    if (value.length() == 0)
                    {
                        value = "\"\"";
                    }
                    if (value.trim().indexOf(" ") != -1 && value.trim().charAt(0) != '"')
                    {
                        value = "\"" + value + "\"";
                    }

                    if (matcher.group(2) != null && matcher.group(2).length() > 0)
                    {
                        line = matcher.group(1) + matcher.group(2) + value;
                    }
                    else if (matcher.group(3) == null || matcher.group(3).length() == 0)
                    {
                        line = matcher.group(1) + " " + value;
                    }
                }
            }
            updatedScript.append(line).append((count < linesArray.length - 1) ? "\n" : "").append(System.getProperty("line.separator"));
            count++;
        }
        return updatedScript.toString();
    }


    private void setGlobalVariables(Map<String, ?> deviceProps)throws TclException{

        Map<String,String> scriptVars = new HashMap<>();

        Map<String,String> keyMapping = new HashMap<>();
        keyMapping.put("name",Script.SYSTEM_VARIABLES.DEVICE_NAME.getDisplayString());
        keyMapping.put("ipStr",Script.SYSTEM_VARIABLES.DEVICE_IP.getDisplayString());
        keyMapping.put("CLI_LOGIN",Script.SYSTEM_VARIABLES.DEVICE_LOGIN.getDisplayString());
        keyMapping.put("DEVICE_TYPE",Script.SYSTEM_VARIABLES.DEVICE_TYPE.getDisplayString());
        keyMapping.put("softwareVersion",Script.SYSTEM_VARIABLES.DEVICE_SOFTWARE_VERSION.getDisplayString());
        keyMapping.put("vendor",Script.SYSTEM_VARIABLES.VENDOR.getDisplayString());
        keyMapping.put("HOSTNAME",Script.SYSTEM_VARIABLES.SERVER_NAME.getDisplayString());
        keyMapping.put("CLI_IS_SSH",Script.SYSTEM_VARIABLES.CLI_SESSION_TYPE.getDisplayString());
        keyMapping.put("XOS_CAPABLE",Script.SYSTEM_VARIABLES.IS_EXOS.getDisplayString());
        keyMapping.put(Script.SYSTEM_VARIABLES.NMS_SERVER_ADDRESS.getDisplayString(),"SERVER_ADDRESS");
        keyMapping.put(Script.SERVER_PROPERTY.IP_ADDRESS.getDisplayString(),Script.SYSTEM_VARIABLES.SERVER_IP.getDisplayString());
        keyMapping.put(Script.SERVER_PROPERTY.HTTP_PORT.getDisplayString(),Script.SYSTEM_VARIABLES.SERVER_PORT.getDisplayString());


        for(Map.Entry<String,?> entry: deviceProps.entrySet()){
            String key = entry.getKey();
            if(keyMapping.containsKey(key)){
                key = keyMapping.get(key);
            }
            scriptVars.put(key,entry.getValue().toString());
        }

        scriptVars.put(Script.SYSTEM_VARIABLES.DATE.getDisplayString(), (new SimpleDateFormat("yyyy-MM-dd").format(new Date())).toString());
        scriptVars.put(Script.SYSTEM_VARIABLES.TIME.getDisplayString(), (new SimpleDateFormat("HH:mm:ss z").format(new Date())).toString());
        scriptVars.put(Script.SYSTEM_VARIABLES.STATUS.getDisplayString(), "1");
        scriptVars.put(Script.SYSTEM_VARIABLES.CLIOUT.getDisplayString(), " ");
        scriptVars.put("ridgeline.scripting.abort_on_error",  "yes"); //??

        if(deviceProps.containsKey("CLI_IS_SSH")){
            scriptVars.put(Script.SYSTEM_VARIABLES.CLI_SESSION_TYPE.getDisplayString(),
                    ((Boolean)deviceProps.get("CLI_IS_SSH")).booleanValue() ? "ssh" : "telnet"); //??
        }

        if(deviceProps.containsKey("XOS_CAPABLE")){
            scriptVars.put(Script.SYSTEM_VARIABLES.IS_EXOS.getDisplayString(),
                    ((Boolean)deviceProps.get("XOS_CAPABLE")).toString()); //??
        }

        // needs the request context
        scriptVars.put(Script.SYSTEM_VARIABLES.NETSIGHT_USER.getDisplayString(),
                "TODO get Username from request context"); //??

        scriptVars.put(Script.SYSTEM_VARIABLES.PORT.getDisplayString(),
                "TODO get port from request context"); //??

        setInterpVars(scriptVars);
    }

    private void setInterpVars(Map<String,String> map){

        for(Map.Entry<String,String> entry: map.entrySet()){
            try {
                interp.setVar(entry.getKey(), null, entry.getValue(), TCL.GLOBAL_ONLY);
            } catch (TclException e) {
                e.printStackTrace();
            }
        }

    }

    private Map<String, Object> getGlobalVariables() {
        Map<String,Object> vars = new HashMap<>();

//        DEVICE_NAME("deviceName", "Dns name of selected device."),
//                DEVICE_IP("deviceIP", "IP address of the selected device."),
//                DEVICE_LOGIN("deviceLogin", "Login user for the selected device."),
//                DEVICE_SOFTWARE_VERSION("deviceSoftwareVer","Software image version nunmber on the device."),
//                DEVICE_TYPE("deviceType", "Device type of the selected device."),
//                PORT("port", "Selected ports, represented in one string."),
//                SERVER_NAME("serverName","Host machine name of the server."),
//                SERVER_IP("serverIP", "Server IP Address."),
//                SERVER_PORT("serverPort", "Server web port."),
//                TIME("time","Current time at server (HH:mm:ss z)."),
//                DATE("date","Current date at server (yyyy-MM-dd)."),
//                STATUS("STATUS", "Last CLI command execution status. 0: success, non-zero: not successful."),
//                ABORT_ON_ERROR("abort_on_error","0: abort on error flag is set; 1: otherwise"),
//                //EPICENTER_USER("epicenterUser", "User running script."), 
//                NETSIGHT_USER("netsightUser", "User running script."),
//                CLI_SESSION_TYPE("CLI.SESSION_TYPE","current session type (telnet / ssh)"),
//                IS_EXOS("isExos", "true / false. if this device is XOS device?"),
//                VENDOR("vendor","Extreme, if it is Extreme device. Vendor name, otherwise."),
//                CLIOUT("CLI.OUT", "Output of the last CLI command."),
//                NMS_SERVER_ADDRESS("nmsServerAddress","Server host name if multiple interfaces enabled, ip address if one interface."),
//        
//        
        return vars;
    }


    protected Interp getInterp() {
        Interp interp = null;

        try
        {
            File scriptFile = TclInterpUtils.getDataFile("Scripting", "bundled_procs.tcl");
            if (scriptFile == null)
            {
                //   LOG.error("Could not load data file bundled_procs.tcl. File missing. Procedures inside this cannot be used.");
            }
            interp = new TclInterpUtils().getTclInterpreter();

            interp.createCommand(TclInterpUtils.CUSTOM_TCL_COMMAND.CLI.getDisplayString(), new CLICommand(this));
            interp.createCommand(TclInterpUtils.CUSTOM_TCL_COMMAND.CLINoWait.getDisplayString(), new CLINoWaitCommand(this));
            interp.createCommand(TclInterpUtils.CUSTOM_TCL_COMMAND.SYNC.getDisplayString(), new PerformSyncCommand(this));
            interp.createCommand(TclInterpUtils.CUSTOM_TCL_COMMAND.ECHO.getDisplayString(), new EchoCommand(this));
            interp.createCommand(TclInterpUtils.CUSTOM_TCL_COMMAND.GetATLValue.getDisplayString(), new GetATLCommand(this));

            interp.evalFile(scriptFile.getCanonicalPath());
        }catch (TclException e) {
            e.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            //  LOG.error("Could not load data file bundled_procs.tcl. Procedures inside this cannot be used.", ex);
        }
        return interp;
    }


    static class ScriptEntry {
        ScriptBean scriptImpl;
        TclSource tclSource;

        public ScriptEntry(ScriptBean script){
            this.scriptImpl = script;
        }
    }
    
    
    static void testFuture(){
        //Future<ScriptResponse> f = svc.submit(request);
//        svc.submit(request, new ScriptListener() {
//            @Override
//            public boolean beforeScriptRun(ScriptRequest request) {
//                System.out.println("BEFORE: "+request.getScriptName());
//                return true;
//            }
//
//            @Override
//            public void afterScriptRun(ScriptRequest request, ScriptResponse response) {
//                System.out.println("AFTER: "+request.getScriptName());
//            }
//        });
//        try {
//            svc.showOutstanding();
//            Thread.sleep(1000);
//            svc.cancel(request.getRequestID());
//            svc.showOutstanding();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        
           /*
        try {
            ScriptResponse response = f.get(2, TimeUnit.SECONDS);
            System.out.println("COOOL!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        */
    }
    
    public static void main(String[] args){
//        String scriptID = "Hello world";
//        ScriptEngine engine = new ScriptEngine(scriptID);
//        Map<String,Object> globalVars = new HashMap<>();
//        globalVars.put("userName","WOWOWOWOW");
//        engine.eval(globalVars);
//
//
//        globalVars.put("userName","Cool");
//        engine.eval(globalVars);

        ScriptingClient svc = ScriptingClient.INSTANCE;
        ScriptRequest request = new ScriptRequest("Hello world");
        
        System.out.println("Submitting request: "+request.getScriptName());
        ScriptResponse response = svc.submitAndWait(request,10);
        System.out.println("Got response: "+response.getResponse());
        
        svc.shutdown();
        //engine.setGlobalVariables(Collections.emptyMap());
        
        //Future f  = engine.evalScript("test",Collections.emptyMap());
    }
}
