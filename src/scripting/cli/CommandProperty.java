package scripting.cli;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@XmlRootElement(name = "CommandProperty")
public class CommandProperty {
    // public String vendor;
    private String versionRange = "-:+";

    private String command;

    private Boolean isPasswordCommand = Boolean.FALSE;

    private List<String> errorPatterns;

    private Integer timeout = -1; // default

    private String shellPrompt;

    private Set<DefaultPrompt> defaultPrompts;


    public CommandProperty() {
        errorPatterns = new ArrayList<String>();
    }


    /**
     * @return Returns the command.
     */
    @XmlAttribute
    public String getCommand() {
        return (this.command);
    }


    /**
     * @param command The command to set.
     */
    public void setCommand(String command) {
        this.command = command;
    }


    /**
     * @return Returns the version.
     */
    public String getVersionRange() {
        return (this.versionRange);
    }


    /**
     * @param versionRange The version to set.
     */
    public void setVersionRange(String versionRange) {
        this.versionRange = versionRange;
    }


    @XmlElementWrapper(name = "errors")
    @XmlElement(name = "messagePattern")
    public List<String> getErrorPatterns() {
        return errorPatterns;
    }

    public void setErrorPatterns(List<String> errorPatterns) {
        this.errorPatterns = errorPatterns;
    }

    public String toString() {
        StringBuilder mappingStr = new StringBuilder();
        mappingStr.append("\n\tcommand = ").append(this.command).append("\n")
                .append("\tversion range = ").append(this.versionRange)
                .append("\n").append("\terror patterns = ").append(
                errorPatterns).append("\n").append(
                "\ttimeout = ").append(timeout).append(
                "\n").append("\tisPasswordCommand = ")
                .append(isPasswordCommand).append("\n").append(
                "\tdefault prompts = ").append(
                defaultPrompts).append("\n");
        return (mappingStr.toString());
    }

    /**
     * @return Returns the responses.
     */
    public List<String> getResponses() {
        return (this.errorPatterns);
    }

    /**
     * @param errorPattern Add response to set.
     */
    public void addErrorPattern(String errorPattern) {
        errorPatterns.add(errorPattern);
    }

    public String getErrorPattersStr() {
        StringBuffer errorPattern = null;
        for (String error : errorPatterns) {
            if (errorPattern != null) {
                errorPattern.append('|').append(error);
            } else {
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
    public Boolean isPasswordCommand() {
        return (this.isPasswordCommand);
    }

    /**
     * @param isPasswordCommand The isPasswordCommand to set.
     */
    public void setPasswordCommand(String isPasswordCommand) {
        this.isPasswordCommand = Boolean.valueOf(isPasswordCommand);
    }

    /**
     * @return Returns the timeout.
     */
    public Integer getTimeout() {
        return (this.timeout);
    }

    /**
     * @param timeout The timeout to set.
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * @return Returns the shellPrompt.
     */
    public String getShellPrompt() {
        return (this.shellPrompt);
    }

    /**
     * @param shellPrompt The shellPrompt to set.
     */
    public void setShellPrompt(String shellPrompt) {
        this.shellPrompt = shellPrompt;
    }

    public void addDefaultPrompts(DefaultPrompt prompt) {
        if (prompt != null) {
            if (defaultPrompts == null) {
                defaultPrompts = new HashSet<DefaultPrompt>();
            }
            defaultPrompts.add(prompt);
        }
    }

    /**
     * @return Returns the defaultPrompts.
     */
    @XmlElement(name = "defaultPrompt")
    public Set<DefaultPrompt> getDefaultPrompts() {
        return (this.defaultPrompts);
    }

    public void setDefaultPrompts(Set<DefaultPrompt> defaultPrompts) {
        this.defaultPrompts = defaultPrompts;
    }

}