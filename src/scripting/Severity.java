package scripting;

public enum Severity implements EnumTextValue {
    INFO("Information", "Informational severity level"),
    WARNING("Warning", "Warning severity level"),
    ERROR("Alert", "Alert severity level");

    private final String displayString;
    private final String verboseDisplayString;


    Severity(String displayString, String verboseDisplayString) {
        this.displayString = displayString;
        this.verboseDisplayString = verboseDisplayString;
    }


    @Override
    public String getDisplayString() {
        return displayString;
    }

    @Override
    public String getVerboseDisplayString() {
        return verboseDisplayString;
    }

    @Override
    public String toString() {
        return getDisplayString();
    }

}
