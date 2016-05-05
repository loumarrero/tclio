package scripting.commands;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scripting.Script;
import scripting.ScriptEngine;
import tcl.lang.*;

/**
 * Common functionality for all tcl command implementations.
 *
 */
public class BaseTclCommand implements Command
{
    private static final String RIDGELINE_DEVICECOMM_SCALE_MAX_CLI_OUT_CHARACTERS = "ridgeline.devicecomm.scale.maxCliOutCharacters";

    public final static String ERROR_PATTERN_STR = "Downloading to MSM-AError:|Failed to download image - Error:"
            + "|^Error:|Incomplete|%% Unrecognized command:|^-Error:|syntax error"
            + "|^ERROR:|^Invalid|Invalid (.*) detected|Invalid input detected|tftpPeerSet failed|Incomplete"
            + "|BootROM is not able to load this type of image|Error: Source image bad|a save or download is in progress - please try again later to save config:"
            + "|Failed to save config: a save or download is in progress - please try again later"
            + "|Error: Failed to install image";

    public final static Pattern ERROR_PATTERN = Pattern.compile(
            ERROR_PATTERN_STR, Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE);

    public final static Pattern ERROR_PATTERN_XOS = Pattern
            .compile(
                    "Downloading to MSM-AError:|Failed to download image - Error:|^Error:|Incomplete|^-Error:|Error: Failed to install image",
                    Pattern.CASE_INSENSITIVE
                            | Pattern.MULTILINE);

    public static final String CLI_TIMEOUT_PROPERTY_NAME =  "scripting.cli.timeoutSeconds";

    public static final int CLI_TIMEOUT_DEFAULT = 120;

    private static final Logger Log = Logger
            .getLogger(BaseTclCommand.class.getName());

    public final String lineSeperator = System.getProperty("line.separator");

    private static boolean propertiesLoaded = false;
   // private static ServerPropertiesUtil serverProps;

    protected ScriptEngine engine;

 //   protected CommandProperty commandProperty;

    private long commandTimeoutMillis = CLI_TIMEOUT_DEFAULT * 1000;

    private String cmdArgs;

    private Pattern errorPattern;

    private String shellPrompt;

   // private Set<ResponseInfo> promptsResponses;

    private Interp interp;

    private String command;


    private int maxCliOut = 10000;
    
    private Object scriptRequest;

    /**
     *
     * @param context
     *            request context for this command.
     * @param writer
     *            to output response / error messages during execution.
     */
    public BaseTclCommand(ScriptEngine engine)
    {
        this.engine = engine;

        //If we loaded the properties, stop asking for them over and over again
        if (!propertiesLoaded){
           // serverProps = new ServerPropertiesUtil(LookupConstants.DEVICE_COMMUNICATOR_MANAGER);
            Set<String> names = new HashSet<String>();
            names.add(CLI_TIMEOUT_PROPERTY_NAME);
            names.add(RIDGELINE_DEVICECOMM_SCALE_MAX_CLI_OUT_CHARACTERS);
           // serverProps.load(names);

        }

//        this.maxCliOut = serverProps.getInteger(RIDGELINE_DEVICECOMM_SCALE_MAX_CLI_OUT_CHARACTERS, 10000);
//        this.commandTimeoutMillis = serverProps.getInteger(CLI_TIMEOUT_PROPERTY_NAME,CLI_TIMEOUT_DEFAULT) * 1000;
    }


    /**
     * An Exception should be shown to STOP script execution.
     *
     * @see tcl.lang.Command#cmdProc(tcl.lang.Interp, tcl.lang.TclObject[])
     */
    @Override
    public void cmdProc(Interp interp, TclObject[] args) throws TclException
    {
        this.interp = interp;
//        if (ContextUtil.isStopped(context))
//        {
//            // stop requested
//            return;
//        }
        if (args.length < 1)
        {
            throw new TclNumArgsException(interp, 1, args, "command missing");
        }
        cmdArgs = cmdarrayToString(args, " ", 1);
        command = cmdarrayToString(args, " ", 0);
        errorPattern = ERROR_PATTERN;
       
            if (Log.isLoggable(Level.FINE)) Log.log(Level.FINE,"timeout for cli [" + cmdarrayToString(args, " ", 0)
                    + "] " + getCommandTimeoutMillis() / 1000 + " seconds.");

    }

    


    /**
     * Returns single concatenated string from the command arguments.
     *
     * @param a
     * @param separator
     * @param beginIndex
     *            from this index all subsequent args are taken.
     * @return
     */
    protected String cmdarrayToString(TclObject[] a, String separator,
                                      int beginIndex)
    {
        StringBuffer result = new StringBuffer();
        if (a.length > beginIndex)
        {
            result.append(a[beginIndex].toString());
            for (int i = beginIndex + 1; i < a.length; i++)
            {
                result.append(separator);
                result.append(a[i].toString());
            }
        }
        return result.toString();
    }


    /**
     * Sets <code>STATUS</code> system variable into interpreter if given
     * response matches specified error pattern.
     *
     * @param interp
     * @param response
     * @param errorPattern
     * @throws TclException
     * @see {@link #isResponseInError(String, Pattern)}
     */
    protected void setStatus(Interp interp, String response,
                             Pattern errorPattern) throws TclException
    {
        if (response != null
                && getResponseErrorIndex(response, errorPattern) > 0)
        {
            interp.setVar(Script.SYSTEM_VARIABLES.STATUS.toString(), null, -1, 0);

            if (Integer.valueOf(interp.getVar(
                    Script.SYSTEM_VARIABLES.ABORT_ON_ERROR.toString(), 0)
                    .toString()) == 1)
            {
//                try
//                {
//                    writer
//                            .write(lineSeperator
//                                    + "** Aborting execution of script because cli-mode is set to abort-on-error **"
//                                    + lineSeperator);
//                    writer.flush();
//                }
//                catch (IOException ex)
//                {
//                    throw new TclException(interp, ex.getMessage());
//                }

                throw new TclException(interp, response);
            }
            // send async erros as alarm
            sendErrorAsAlarmEvent(response);
        }
        else
        {
            interp.setVar(Script.SYSTEM_VARIABLES.STATUS.toString(), null, 0, 0);
        }
    }


    /**
     * Checks if given response matches error pattern.
     *
     * @param response
     * @param pErrorPattern
     * @return
     */
    protected int getResponseErrorIndex(String response, Pattern pErrorPattern)
    {
        int ret = -1;
        Matcher matcher = pErrorPattern.matcher(response);
        if (matcher.find())
        {
            ret = response.indexOf(command) + command.length();
        }
        return (ret);

    }
    

    /**
     * @return Returns the deviceCommand.
     */
    public String getCommandArgs()
    {
        return (this.cmdArgs);
    }


    protected void logAndSetInterpResponse(String response) throws TclException
    {
        logAndSetInterpResponse(true, response);
    }


    protected void logAndSetInterpResponse(String header, String response) throws TclException
    {
        if (header != null)
        {
            logMessage(header + lineSeperator);
        }

        Integer maxCliOut = getMaxCliOut();;
        String truncatedResponse = response;
        if (response != null && response.length() > maxCliOut)
        {
            StringBuilder str = new StringBuilder(response.substring(0,
                    maxCliOut));
            str.append("\n **** command response too big. truncated to ")
                    .append(maxCliOut)
                    .append(" characters **** \n");
            truncatedResponse = str.toString();
        }
        logMessage(response);
        setCommandResponse(truncatedResponse);
    }


    protected void setCommandResponse(String commandResponse)
            throws TclException
    {
        interp.setVar(Script.SYSTEM_VARIABLES.CLIOUT.getDisplayString(), null,
                commandResponse, 0);
        interp.setResult(commandResponse);
        setStatus(interp, commandResponse, errorPattern);
    }


    protected void logMessage(String message) throws TclException
    {
        engine.append(message + lineSeperator);
    }


    /**
     * Prints command arguments and then prints response.
     *
     * @param printCommand
     * @param response
     * @throws TclException
     */
    protected void logAndSetInterpResponse(boolean printCommand, String response)
            throws TclException
    {
        String header = null;
        if (printCommand)
        {
            header = getCommandArgs();
        }
        logAndSetInterpResponse(header, response);
    }


    protected void resetStatusFlags() throws TclException
    {
        interp.setVar(Script.SYSTEM_VARIABLES.CLIOUT.getDisplayString(), null, "", 0);
        interp.setVar(Script.SYSTEM_VARIABLES.STATUS.toString(), null, 0, 0); // 0 : no
        // error
    }


    protected void handleException(Exception ex) throws TclException
    {
        String errorMsg = getDetailedErrorMessage(ex);
        sendErrorAsAlarmEvent(errorMsg);
        throw new TclException(interp, errorMsg);
    }


    /**
     * @return Returns the errorPattern.
     */
    public Pattern getErrorPattern()
    {
        return (this.errorPattern);
    }


    /**
     * @return Returns the shellPrompt.
     */
    public String getShellPrompt()
    {
        return (this.shellPrompt);
    }


    /**
     * @return Returns the promptsResponses.
     */
//    public Set<ResponseInfo> getPromptsResponses()
//    {
//        return (this.promptsResponses);
//    }


    protected String getDetailedErrorMessage(Exception ex)
    {
        String errorStr = null;
        if (ex != null)
        {
            errorStr = ex.getMessage();

            if (!(ex instanceof TclException))
            {
                if (ex.getMessage() != null && ex.getMessage().indexOf("Telnet connection problem") != -1)
                {
                    if (Log.isLoggable(Level.FINE)) Log.log(Level.FINE,"Error excecuting command: " + getCommand() + ex.getMessage());
                }
                else
                {
                    if (Log.isLoggable(Level.FINE)) Log.log(Level.FINE,"Error excecuting command: " + getCommand(), ex);
                }
            }

            // UNCOMMENT TO SEE WHOLE STACK TRACE ON THE CLIENT
            // else
            // {
            // StringBuffer tempBuffer = new
            // StringBuffer(" -------- StackTrace --------\n");
            // StringWriter sw = new StringWriter();
            // PrintWriter pw = new PrintWriter(sw);
            // ex.printStackTrace(pw);
            // tempBuffer.append(sw.toString());
            // tempBuffer.append("\n").append(" ----------------------------");
            //
            // errorStr = tempBuffer.toString();
            // }
        }
        return (errorStr);
    }


    /**
     * Sends async requests' error as alarm.
     *
     * @param errorMessage
     */
    protected void sendErrorAsAlarmEvent(String errorMessage)
    {
// We dont have a way to raise alarms from scripting yet.
    }


 

    /**
     * @return Returns the command.
     */
    public String getCommand()
    {
        return (this.command);
    }


    /**
     * @return
     */
    private int getMaxCliOut()
    {
        return this.maxCliOut;
    }

    /**
     * @return Returns the commandTimeoutMillis.
     */
    protected long getCommandTimeoutMillis()
    {
        return(commandTimeoutMillis);
    }


    /**
     * @param commandTimeoutMillis The commandTimeoutMillis to set.
     */
    protected void setCommandTimeoutMillis(long commandTimeoutMillis)
    {
        this.commandTimeoutMillis = commandTimeoutMillis;
    }
}
