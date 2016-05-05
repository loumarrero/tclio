package scripting;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandProperty
{
    // public String vendor;
    private String versionRange = "-:+";

    private String command;

    private Boolean isPasswordCommand = Boolean.FALSE;

    private List<String> errorPatterns;

    private FamilyRule parent;

    private Integer timeout = -1; // default

    private String shellPrompt;

    private Set<DefaultPrompt> defaultPrompts;


    public CommandProperty()
    {
        errorPatterns = new ArrayList<String>();
    }


    /**
     * @return Returns the command.
     */
    public String getCommand()
    {
        return (this.command);
    }


    /**
     * @param command
     *            The command to set.
     */
    public void setCommand(String command)
    {
        this.command = command;
    }


    /**
     * @return Returns the version.
     */
    public String getVersionRange()
    {
        return (this.versionRange);
    }


    /**
     * @param version
     *            The version to set.
     */
    public void setVersionRange(String versionRange)
    {
        this.versionRange = versionRange;
    }


    public String toString()
    {
        StringBuilder mappingStr = new StringBuilder();
        mappingStr.append("command = ").append(this.command).append("\n")
                .append("version range = ").append(this.versionRange)
                .append("\n").append("error patterns = ").append(
                errorPatterns).append("\n").append(
                "timeout = ").append(timeout).append(
                "\n").append("isPasswordCommand = ")
                .append(isPasswordCommand).append("\n").append(
                "default prompts = ").append(
                defaultPrompts).append("\n");
        return (mappingStr.toString());
    }


    /**
     * @return Returns the responses.
     */
    public List<String> getResponses()
    {
        return (this.errorPatterns);
    }


    /**
     * @param response
     *            Add response to set.
     */
    public void addErrorPattern(String errorPattern)
    {
        errorPatterns.add(errorPattern);
    }


  

    /**
     * @return Returns the parent.
     */
    public FamilyRule getParent()
    {
        return (this.parent);
    }


    /**
     * @param parent
     *            The parent to set.
     */
    public void setParent(FamilyRule parent)
    {
        this.parent = parent;
    }


    public String getErrorPattersStr()
    {
        StringBuffer errorPattern = null;
        for (String error : errorPatterns)
        {
            if (errorPattern != null)
            {
                errorPattern.append('|').append(error);
            }
            else
            {
                errorPattern = new StringBuffer("");
                errorPattern.append(error);
            }
        }

        return ((errorPattern != null) ? errorPattern.toString().toLowerCase()
                : null);

    }


    /**
     * @return Returns the isPasswordCommand.
     */
    public Boolean isPasswordCommand()
    {
        return (this.isPasswordCommand);
    }


    /**
     * @param isPasswordCommand
     *            The isPasswordCommand to set.
     */
    public void setPasswordCommand(String isPasswordCommand)
    {
        this.isPasswordCommand = Boolean.valueOf(isPasswordCommand);
    }


    /**
     * @return Returns the timeout.
     */
    public Integer getTimeout()
    {
        return (this.timeout);
    }


    /**
     * @param timeout
     *            The timeout to set.
     */
    public void setTimeout(Integer timeout)
    {
        this.timeout = timeout;
    }


    /**
     * @return Returns the shellPrompt.
     */
    public String getShellPrompt()
    {
        return (this.shellPrompt);
    }


    /**
     * @param shellPrompt
     *            The shellPrompt to set.
     */
    public void setShellPrompt(String shellPrompt)
    {
        this.shellPrompt = shellPrompt;
    }


    public void addDefaultPrompts(DefaultPrompt prompt)
    {
        if (prompt != null)
        {
            if (defaultPrompts == null)
            {
                defaultPrompts = new HashSet<DefaultPrompt>();
            }
            defaultPrompts.add(prompt);
        }
    }


    /**
     * @return Returns the defaultPrompts.
     */
    public Set<DefaultPrompt> getDefaultPrompts()
    {
        return (this.defaultPrompts);
    }
}