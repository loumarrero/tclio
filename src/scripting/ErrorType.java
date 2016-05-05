package scripting;

/**
 * scripting.ErrorType.java : scripting.ErrorType
 *
 * @author ptripathi (ptripathi@extremenetworks.com)
 */
public enum ErrorType implements EnumTextValue
{

    APPLICATION ("Application"),
    DEVICE ("Device"),
    PROTOCOL ("Protocol"),
    ATHENTICATION("Authentication"),
    INPUT("Input"),
    ;

    private String displayString;
    ErrorType(String displayString)
    {
        this.displayString = displayString;
    }

    /**
     * @see com.extremenetworks.epicenter.common.interfaces.EnumTextValue#getDisplayString()
     */
    @Override
    public String getDisplayString()
    {
        return displayString;
    }

    /**
     * @see com.extremenetworks.epicenter.common.interfaces.EnumTextValue#getVerboseDisplayString()
     */
    @Override
    public String getVerboseDisplayString()
    {
        return displayString;
    }
}
