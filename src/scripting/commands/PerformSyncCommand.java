package scripting.commands;


import scripting.ScriptEngine;
import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclObject;

import java.io.BufferedWriter;


public class PerformSyncCommand extends BaseTclCommand
{
    public PerformSyncCommand(ScriptEngine engine)
    {
        super(engine);
    }


    /**
     * @see tcl.lang.Command#cmdProc(Interp, TclObject[])
     */
    @Override
    public void cmdProc(Interp interp, TclObject[] args) throws TclException {
            super.cmdProc(interp, args);
   
    }


}

