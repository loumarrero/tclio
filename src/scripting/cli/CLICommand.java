package scripting.cli;


import scripting.Script;
import scripting.ScriptEngine;
import scripting.commands.BaseTclCommand;
import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclObject;


public class CLICommand extends BaseTclCommand {
    protected CLISession cliSession;

    public CLICommand(ScriptEngine engine) {
        super(engine);
    }

    public CLICommand(ScriptEngine engine,boolean noWait) {
        super(engine);
    }

    public CLISession getCliSession() {
        return cliSession;
    }

    public void setCliSession(CLISession cliSession) {
        this.cliSession = cliSession;
    }

    /**
     * @see tcl.lang.Command#cmdProc(tcl.lang.Interp, tcl.lang.TclObject[])
     */
    @Override
    public void cmdProc(Interp interp, TclObject[] args) throws TclException {
        super.cmdProc(interp, args);

        logMessage("CLI - " + getCommandArgs());
        if (!isConnected()) {
            //logMessage("CLI - Failed to connect to device: " + cliSession.getHost() + ", reason: " + cliSession.getLastErrorMessage());
            connect();
            return;
        }


        resetStatusFlags();

        // For show commands update CLI.OUT
        if (args.length >= 3 && args[1].toString().toLowerCase().startsWith("sh") && args[2].toString().toLowerCase().startsWith("va")) {
            String varValue = null;
            if (args.length == 3) // SHOW VAR NOT SUPPORTED. SHOW VAR <varname> is supported
            {
                varValue = "Error: Command incomplete. Usage: SHOW VAR <variable>" + lineSeperator;
            } else {
                TclObject tclObject = interp.getVar(args[3].toString(), 0);

                if (tclObject != null) {
                    varValue = tclObject.toString();
                } else {
                    varValue = "Variable " + args[3].toString() + " not found." + lineSeperator;
                }
            }
            logAndSetInterpResponse(varValue);
        } else {
            String output = cliSession.cli(getCommandArgs());
            logAndSetInterpResponse(output);
        }

    }

    protected boolean isConnected() {
        return cliSession != null && cliSession.isConnected();
    }

    protected void connect() {
        // script overrides

        String userName = getInterVar("deviceLogin", "lmarrero");
        String passwd = getInterVar("devicePassword", "foo");
        String sshEnabled = getInterVar(Script.SYSTEM_VARIABLES.CLI_SESSION_TYPE.getDisplayString(), "telnet");
        String portStr = getInterVar(Script.SYSTEM_VARIABLES.PORT.getDisplayString(), "23");
        String ipStr = getInterVar(Script.SYSTEM_VARIABLES.DEVICE_IP.getDisplayString(), "10.1.1.1");


        String savecmd = getInterVar(Script.SYSTEM_VARIABLES.CLI_CMD_SAVE.getDisplayString(), null);
        String cmd = getInterVar(Script.SYSTEM_VARIABLES.CLI_PROMPT_CMD.getDisplayString(), null);
        String cmdReply = getInterVar(Script.SYSTEM_VARIABLES.CLI_PROMPT_CMD_REPLY.getDisplayString(), null);
        String shellP = getInterVar(Script.SYSTEM_VARIABLES.CLI_PROMPT_SHELL.getDisplayString(), null);


    }


    @Override
    public void close() {
        if (cliSession != null)
            cliSession.close();
    }
}

