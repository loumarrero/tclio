package scripting.commands;


import scripting.ScriptEngine;
import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclObject;

import java.io.BufferedWriter;


public class EchoCommand extends BaseTclCommand
{
    public EchoCommand(ScriptEngine engine)
    {
        super(engine);
    }


    /**
     * @see tcl.lang.Command#cmdProc(tcl.lang.Interp, tcl.lang.TclObject[])
     */
    @Override
    public void cmdProc(Interp interp, TclObject[] args) throws TclException
    {
        super.cmdProc(interp, args);
        // String filename =
        // (String)context.get(CommunicatorKeyNames.RESPONSE_FILE_ABS_PATH_KEY);
        // interp.eval("set epic_responseFileId [open " +
        // "\"" +
        // filename +
        // "\" a]");
        // interp.eval("puts $epic_responseFileId \"" + getCommandArgs() +
        // "\"");
        // interp.eval("flush $epic_responseFileId");
        // interp.eval("close $epic_responseFileId");
//        try
//        {
//            writer.write("\n");
//            writer.flush();
//        }
//        catch (IOException ex)
//        {
//            throw new TclException(interp, ex.getMessage());
//        }
        
       // System.out.println("CLI ECHO - "+getCommandArgs());
        logMessage("CLI ECHO - "+getCommandArgs());

    }

   
}

