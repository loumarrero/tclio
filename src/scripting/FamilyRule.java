package scripting;


import java.util.LinkedHashMap;
import java.util.Map;



public class FamilyRule
{
    private String value;
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
}

