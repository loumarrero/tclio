package scripting.tcl;

import scripting.EnumTextValue;
import tcl.lang.*;

import java.io.File;

/**
 * scripting.tcl.TclInterpUtils.java : {TODO}
 *
 * @author ptripathi (ptripathi@extremenetworks.com)
 */
public class TclInterpUtils {

    // file, open commons to be included?
    private static final String[] unsafeCmds = {
            "encoding", "exit", "load", "cd", "fconfigure",
            "glob", "pwd", "socket",
            "beep", "ls", "resource", "exec", "source"
    };

    /**
     * Returns given data file for a feature. Relative path should be relative to
     * <code>everest.home</code> system property.
     *
     * @param feature
     * @param fileName name of the file. do not specify relative path.
     * @return file reference to the data file or <code>null</code> if file not present or error processing request.
     */
    static public File getDataFile(String feature, String fileName) {
        File file = null;
        String fileSeperator = System.getProperty("file.separator");
        String appdata = "C:\\Users\\lmarrero\\Dev\\test\\MyTcl";
        StringBuffer relativePath = new StringBuffer(fileSeperator).append(feature.toLowerCase())
                .append(fileSeperator).append(fileName);
        file = new File(appdata, relativePath.toString());
        return (file.exists() ? file : null);
    }

    public Interp getTclInterpreter()
            throws TclException {
        Interp interp = new Interp();
        hideUnsafeCommands(interp);
        interp.resetResult();
        return (interp);
    }

    /**
     * @param interp
     * @throws TclException if error occurs
     */
    private void hideUnsafeCommands(Interp interp) throws TclException {
        for (int ix = 0; ix < unsafeCmds.length; ix++) {
            try {
                WrappedCommand cmd = Namespace.findCommand(interp, unsafeCmds[ix], null,
                              /*flags*/ TCL.LEAVE_ERR_MSG | TCL.GLOBAL_ONLY);

                if (cmd.table.containsKey(cmd.hashKey)) {
                    cmd.table.remove(cmd.hashKey);
                    incrEpoch(cmd);
                }

            } catch (TclException e) {
                if (!e.getMessage().startsWith("unknown command")) {
                    throw e;
                }
            }
        }
    }

    private void incrEpoch(WrappedCommand cmd) {
        cmd.cmdEpoch++;
        if (cmd.cmdEpoch == Integer.MIN_VALUE) {
            // Integer overflow, really unlikely but possible.
            cmd.cmdEpoch = 1;
        }
    }


    public enum CUSTOM_TCL_COMMAND implements EnumTextValue {
        CLI("CLI"),
        CLINoWait("CLINoWait"),
        SYNC("PerformSync"),//LRW - Limited functionality EAPS Domain and VPLS
        ECHO("ECHO"),
        SendEvent("SendEvent"),//LRW - Disabled, returns without action
        // FOPEN("fopen"),
        GetATLValue("GetATLValue"),
        SourceScript("SourceScript"),//LRW - Disabled, returns without action
        ;

        private String name;

        private CUSTOM_TCL_COMMAND(String name) {
            this.name = name;
        }

        /**
         * @see com.extremenetworks.epicenter.common.interfaces.EnumTextValue#getDisplayString()
         */
        @Override
        public String getDisplayString() {
            return name;
        }

        /**
         * @see com.extremenetworks.epicenter.common.interfaces.EnumTextValue#getVerboseDisplayString()
         */
        @Override
        public String getVerboseDisplayString() {
            return name;
        }
    }
}
