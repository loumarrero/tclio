package scripting;


import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class FamilyRule
{
    private String value;
    private String shellPrompt;
    private String loginPrompt;
    private String passwordPrompt;
    private Set<MorePrompt> morePrompts;
        
    private Map<String, CommandProperty> commandProperties;
    private VendorRules parent;
    private String saveConfigCommand;

    public FamilyRule()
    {
        commandProperties = new LinkedHashMap<String, CommandProperty> ();
    }

    public void addCommandProperty( CommandProperty  commandProperty ) {
        if (commandProperty.getCommand() != null)
        {
            commandProperties.put(commandProperty.getCommand(), commandProperty);
        }
    }

    /**
     * @return Returns the value.
     */
    public String getValue()
    {
        return (this.value);
    }

    /**
     * @param value The value to set.
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * @return Returns the vendorRules.
     */
    public VendorRules getParent()
    {
        return (this.parent);
    }

    /**
     * @param vendorRules The vendorRules to set.
     */
    public void setParent(VendorRules vendorRules)
    {
        this.parent = vendorRules;
    }

    /**
     * @return Returns the commandProperties.
     */
    public Map<String, CommandProperty> getCommandProperties()
    {
        return (this.commandProperties);
    }


    public CommandProperty getCommandProperty(String command)
    {
        CommandProperty prop = null;
        for (Map.Entry<String, CommandProperty> item : commandProperties.entrySet())
        {
            if (command.matches(item.getKey()))
            {
                prop =  (item.getValue());
                break;
            }
        }
        return (prop);
    }

    /**
     * @return Returns the saveConfigCommand.
     */
    public String getSaveConfigCommand()
    {
        return (this.saveConfigCommand);
    }

    /**
     * @param saveConfigCommand The saveConfigCommand to set.
     */
    public void setSaveConfigCommand(String saveConfigCommand)
    {
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

    public void addMorePrompts(MorePrompt prompt)
    {
        if (prompt != null)
        {
            if (morePrompts == null)
            {
                morePrompts = new HashSet<MorePrompt>();
            }
            morePrompts.add(prompt);
        }
    }

    /**
     * @return Returns the defaultPrompts.
     */
    public Set<MorePrompt> getMorePrompts()
    {
        return (this.morePrompts);
    }
    
    
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Name: ");
        builder.append(getValue());
        builder.append("\n");

        builder.append("Save: ");
        builder.append(getSaveConfigCommand());
        builder.append("\n");

        builder.append("ShellPrompt: ");
        builder.append(getShellPrompt());
        builder.append("\n");

        builder.append("LoginPrompt: ");
        builder.append(getLoginPrompt());
        builder.append("\n");

        builder.append("MorePrompt(s):");
        for(MorePrompt morePrompt: morePrompts){
            builder.append("\n\tPrompt: ");
            builder.append(morePrompt.getPrompt());
            builder.append(" response: ");
            builder.append(morePrompt.getResponse());
            builder.append("\n");    
        }
        
        return builder.toString();
    }
}

