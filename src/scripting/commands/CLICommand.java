package scripting.commands;


import scripting.CLIManager;
import scripting.CLISession;
import scripting.ScriptEngine;
import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclObject;

import java.io.BufferedWriter;
import java.io.IOException;


public class CLICommand extends BaseTclCommand
{
    
    public CLICommand(ScriptEngine engine) {
        super(engine);
    }


    /**
     * @see tcl.lang.Command#cmdProc(tcl.lang.Interp, tcl.lang.TclObject[])
     */
    @Override
    public void cmdProc(Interp interp, TclObject[] args) throws TclException
    {
            super.cmdProc(interp, args);
            
            logMessage("CLI - "+getCommandArgs());
        
            resetStatusFlags();
    
            // For show commands update CLI.OUT
            if (args.length >= 3 && args[1].toString().toLowerCase().startsWith("sh") && args[2].toString().toLowerCase().startsWith("va")) {
                String varValue = null;
                if (args.length == 3) // SHOW VAR NOT SUPPORTED. SHOW VAR <varname> is supported
                {
                    varValue = "Error: Command incomplete. Usage: SHOW VAR <variable>" + lineSeperator;
                }
                else
                {
                    TclObject tclObject = interp.getVar(args[3].toString(), 0);
    
                    if (tclObject != null)
                    {
                        varValue = tclObject.toString();
                    }
                    else
                    {
                        varValue = "Variable " + args[3].toString() + " not found." + lineSeperator;
                    }
                }
                logAndSetInterpResponse(varValue);
            }
            else {
               
            }

        }

    public void cliCommand(String cmd){
        String host = "";
        String userName = "";
        String passwd = "";
        CLIManager cliManager = CLIManager.INSTANCE;
        System.out.println(cliManager.cli(host,cmd, new CLISession.QuestionPrompt("to quit:"," ")));
        // the results go into CLI.OUT
    }
    

}

