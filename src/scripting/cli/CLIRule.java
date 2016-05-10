package scripting.cli;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;


@XmlRootElement(name = "Rule")
public class CLIRule {
    private String name;
    private String shellPrompt;
    private String loginPrompt;
    private String passwordPrompt;

    private Set<MorePrompt> morePrompts;

    private Map<String, CommandProperty> commandProperties;

    private List<CommandProperty> myProps;

    private String saveConfigCommand;

    public CLIRule() {
        commandProperties = new LinkedHashMap<String, CommandProperty>();
    }

    public void addCommandProperty(CommandProperty commandProperty) {
        if (commandProperty.getCommand() != null) {
            commandProperties.put(commandProperty.getCommand(), commandProperty);
        }
    }

    /**
     * @return Returns the value.
     */
    @XmlAttribute(name = "name")
    public String getName() {
        return (this.name);
    }

    /**
     * @param name The value to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "morePrompt")
    public Set<MorePrompt> getMorePrompts() {
        return morePrompts;
    }

    public void setMorePrompts(Set<MorePrompt> morePrompts) {
        this.morePrompts = morePrompts;
    }

    @XmlElement(name = "CommandProperty")
    public List<CommandProperty> getMyProps() {
        return myProps;
    }

    public void setMyProps(List<CommandProperty> props) {
        this.myProps = props;
    }

    public CommandProperty getCommandPropertyTest(String command) {
        CommandProperty prop = null;
        for (Map.Entry<String, CommandProperty> item : commandProperties.entrySet()) {
            if (command.matches(item.getKey())) {
                prop = (item.getValue());
                break;
            }
        }
        return (prop);
    }


    /**
     * @return Returns the saveConfigCommand.
     */
    public String getSaveConfigCommand() {
        return (this.saveConfigCommand);
    }

    /**
     * @param saveConfigCommand The saveConfigCommand to set.
     */
    public void setSaveConfigCommand(String saveConfigCommand) {
        this.saveConfigCommand = saveConfigCommand;
    }

    public String getShellPrompt() {
        return shellPrompt;
    }

    public void setShellPrompt(String shellPrompt) {
        this.shellPrompt = shellPrompt;
    }

    public String getLoginPrompt() {
        return loginPrompt;
    }

    public void setLoginPrompt(String loginPrompt) {
        this.loginPrompt = loginPrompt;
    }

    public String getPasswordPrompt() {
        return passwordPrompt;
    }

    public void setPasswordPrompt(String passwordPrompt) {
        this.passwordPrompt = passwordPrompt;
    }

    public void addMorePrompts(MorePrompt prompt) {
        if (prompt != null) {
            if (morePrompts == null) {
                morePrompts = new HashSet<MorePrompt>();
            }
            morePrompts.add(prompt);
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Name: ");
        builder.append(getName());
        builder.append("\n");

        builder.append("LoginPrompt: ");
        builder.append(getLoginPrompt());
        builder.append("\n");

        builder.append("PasswordPrompt: ");
        builder.append(getPasswordPrompt());
        builder.append("\n");

        builder.append("Save: ");
        builder.append(getSaveConfigCommand());
        builder.append("\n");

        builder.append("ShellPrompt: ");
        builder.append(getShellPrompt());
        builder.append("\n");

        builder.append("MorePrompt(s):");
        if (morePrompts != null)
            for (MorePrompt morePrompt : morePrompts) {
                builder.append("\n\tPrompt: ");
                builder.append(morePrompt.getPrompt());
                builder.append(" response: ");
                builder.append(morePrompt.getResponse());
                builder.append("\n");
            }

        builder.append("Command Prompt(s):");
        if (myProps != null)
            for (CommandProperty morePrompt : myProps) {
                builder.append("\t" + morePrompt.toString());
            }


        return builder.toString();
    }
}
