package scripting.commands;


import scripting.ScriptEngine;
import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclObject;


public class GetATLCommand extends BaseTclCommand {

    public GetATLCommand(ScriptEngine engine) {
        super(engine);
    }


    /**
     * @see tcl.lang.Command#cmdProc(Interp, TclObject[])
     */
    @Override
    public void cmdProc(Interp interp, TclObject[] args) throws TclException {
        super.cmdProc(interp, args);

        setCommandResponse("false");
    }


}

