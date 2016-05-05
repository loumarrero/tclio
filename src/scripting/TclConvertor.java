package scripting;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.xmlrules.FromXmlRulesModule;
import tcl.lang.Interp;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import tcl.lang.Namespace;
import tcl.lang.TCL;

import static org.apache.commons.digester3.binder.DigesterLoader.newLoader;

public class TclConvertor
{
    private static final Logger LOG = Logger.getLogger(TclConvertor.class.getName());
    
    private static final String extremeSwitchesCommandPrefixes =
            "abort?|check|clear?|confi?g?u?r?e?|creat?e?|"
                    + "delet?e?|disab?l?e?|downl?o?a?d?|edit|eject?|" +
                    "enabl?e?|ENDIF?|ENDWH?I?L?E?|exit|histo?r?y?|" +
                    "IF|insta?l?l?|load|logou?t?|ls|" +
                    "mpls|mrinf?o?|mtrac?e?|mv|nsloo?k?u?p?|" +
                    "ping|quit|reboo?t?|refre?s?h?|reset?|" +
                    "resta?r?t?|rm|rtloo?k?u?p?|run|save|" +
                    "scp2|set|show|telne?t?|termi?n?a?t?e?|" +
                    "tftp|top|trace?r?o?u?t?e?|uncon?f?i?g?u?r?e?|unins?t?a?l?l?|" +
                    "uploa?d?|use|virtual-router|vlan|vplstrace|" +
                    "while|xping";

    private Properties properties = new Properties();
    private List<SourceMapping> mappings = Collections.EMPTY_LIST;
    
    public TclConvertor(List<SourceMapping> mappings){
        this.mappings = mappings;
    }
    
   

    public TclSource convertScriptToTcl(Interp interp,String script) throws Exception
    {
        
        if (script == null)
        {
            LOG.log(Level.SEVERE,"Error converting script to tcl. scripting.Script is null.");
            throw new IllegalArgumentException("Source EPICenter script cannot be null");
        }

        List<String> procsList = new ArrayList<String>();
        Writer writer = new StringWriter();

        boolean switchMode = false;
       
        TclSource tclSource = new TclSource(script);
        StringTokenizer st = new StringTokenizer(script, System.getProperty("line.separator"), true);
        while (st.hasMoreTokens())
        {
            String line = st.nextToken();
            if (line.charAt(0) == '\r')
            {
                continue;
            }
            else if (line.charAt(0) == '\n')
            {
                // tclSource.getSourceMetrics().incrementEmptyLineCount();
                writer.write(System.getProperty("line.separator"));
            }
            else
            {
                tclSource.getSourceMetrics().incrementLineCount();

                line = line.trim();
                if (line.length() == 0)
                {
                    continue;
                }

                if (switchMode)
                {
                    line = TclInterpUtils.CUSTOM_TCL_COMMAND.CLI.getDisplayString() + " " + line;
                    writer.write(line);
                    continue;
                }

                boolean found = false;
                for (SourceMapping sourceMapping : mappings)
                {
                    String exosSyntax = sourceMapping.getXossyntaxexpr();
                    Pattern pattern = Pattern.compile(exosSyntax);
                    Matcher matcher = pattern.matcher(line);
                    matcher.reset();
                    boolean cont = false;
                    while (matcher.find())
                    {
                        String tclStr = line.replaceAll(exosSyntax, sourceMapping.getTclexpr());

                        if (sourceMapping.getHint() != null && sourceMapping.getHint().equals("continue"))
                        {
                            line = tclStr;
                            cont = true;
                        }
                        else
                        {
                            // keep list of all procs defined in the script
                            // this will help seperate proc call from CLI calls later
                            if (sourceMapping.getHint() != null && sourceMapping.getHint().equals("proc"))
                            {
                                if (matcher.groupCount() > 0)
                                {
                                    procsList.add(matcher.group(1));
                                }
                            }
                            writer.write(tclStr);
                            cont = false;
                            found = true;
                        }
                    }
                    if (found && (!cont))
                    {
                        if (sourceMapping.getHint().equals("set"))
                        {
                            Pattern modePattern = Pattern.compile("(?i)^\\s*set\\s+var\\s+runMode\\s+SWITCH\\s*$");
                            Matcher modeMatcher = modePattern.matcher(line);
                            if (modeMatcher.find())
                            {
                                switchMode = true;
                            }
                        }
                        break;
                    }
                }
                // did not match any rule
                if (!found)
                {
                    line = line.trim();
                    String[] command = line.split(" ");

                    if (Namespace.findCommand(interp, command[0], null, TCL.GLOBAL_ONLY) == null)
                    {
                        boolean isCustom = false;
                        for (TclInterpUtils.CUSTOM_TCL_COMMAND cmd : TclInterpUtils.CUSTOM_TCL_COMMAND.values())
                        {
                            if (command[0].equalsIgnoreCase(cmd.getDisplayString()))
                            {
                                int i = line.indexOf(' ');
                                line = cmd.getDisplayString() + (line.substring((i == -1) ? line.length() : i));
                                isCustom = true;
                                break;
                            }
                        }
                        if (!isCustom && !(procsList.contains(command[0])))
                        {

                            if (command[0].matches("(?i)" + extremeSwitchesCommandPrefixes))
                            {
                                // Preserve double quotes (".. ")in switch command by putting "\" before
                                // quotes. otherwise tcl interpreter strips double quotes.
                                // eg. create log entry "this is in quotes"
                                // this will not work since tcl will strip quotes
                                line = line.replace("\"", "\\\"");

                                // escape semi-colon (;) in the CLI commands 
                                // PD4-1258815021
                                line = line.replace(";", "\\;");

                                line = TclInterpUtils.CUSTOM_TCL_COMMAND.CLI.getDisplayString() + " " + line;

                            }
                        }
                    }
                    writer.write(line);
                }
            }
        }
        tclSource.setSourceCode(writer.toString());
        //interp.dispose();
        return (tclSource);
    }


    /**
     * Properties should be picked from ITranslationOptions
     *
     * @param properties
     * @see ITranslationOptions
     */
    public void setConversionOptions(Properties properties)
    {
        this.properties = properties;
    }


    private Interp getInterp() throws Exception
    {
        Interp interp = new TclInterpUtils().getTclInterpreter();
        File scriptFile = TclInterpUtils.getDataFile("Scripting", "bundled_procs.tcl");
        if (scriptFile == null)
        {
            LOG.log(Level.SEVERE,"Could not load data file bundled_procs.tcl. File missing. Procedures inside this cannot be used.");
        }
        else
        {
            try
            {
                interp.evalFile(scriptFile.getCanonicalPath());
            }
            catch (Exception ex)
            {
                LOG.log(Level.SEVERE,"Could not load bundled_procs.tcl. Procedures inside this cannot be used.", ex);
            }
        }
        return (interp);
    }
    
}
