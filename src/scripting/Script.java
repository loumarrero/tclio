package scripting;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines EPICenter script entity.
 */
public interface Script
{
    public static final String TYPE = "scripting/scripting.Script";

    public static final String ATTRIBUTE_NAME = "name";

    public static final String ATTRIBUTE_COMMENTS = "comments";

    public static final String ATTRIBUTE_CONTENT = "content";

    public static final String ATTRIBUTE_SAVE_CONFIG_ON_EXIT = "saveConfigOnExit";

    //public static final String ATTRIBUTE_AUDIT_LOG_ENABLED = "auditLogEnabled";

    public static final String ATTRIBUTE_EXECUTION_CONTEXTS = "executionContexts";

    public static final String ATTRIBUTE_SUPPORTED_GROUPS = "supportedGroups";

    public static final String ATTRIBUTE_SUPPORTED_ROLES = "supportedRoles";

    public static final String ATTRIBUTE_CATEGORY = "category";

    public static final String ATTRIBUTE_DEFAULT_CATEGORY = "defaultCategory";

    public static final String ATTRIBUTE_XMLDATA = "miscXMLData";

    public static final String ATTRIBUTE_MENUSCOPE = "menuScope";

    public static final String RELATION_USERROLE = "scriptRoles";

    public static final String ATTRIBUTE_IS_ALARM_ACTION = "isAlarmAction";

    public static final String ATTRIBUTE_SCRIPT_TIMEOUT = "scriptTimeout";

    public static final String ATTRIBUTE_ROLLBACK_SCRIPT = "rollbackScript";

    public static final String ATTRIBUTE_POSTPROCESS_SCRIPT = "postprocessScript";

    public static final String ATTRIBUTE_LOCKED = "locked";

    public static final String ATTRIBUTE_SCRIPT_OWNER = "scriptOwner";

    public static final String ATTRIBUTE_CREATOR = "creator";
    public static final String ATTRIBUTE_CREATTION_DATE = "creationDate";
    public static final String ATTRIBUTE_LAST_UPDATED_BY = "lastUpdatedBy";
    public static final String ATTRIBUTE_UPDATE_DATE = "lastUpdateDate";

    public static final String[] ATTRIBUTE_NAMES = {ATTRIBUTE_NAME,
            ATTRIBUTE_COMMENTS, ATTRIBUTE_CONTENT, ATTRIBUTE_CREATOR,
            ATTRIBUTE_CREATTION_DATE, ATTRIBUTE_UPDATE_DATE,
            ATTRIBUTE_LAST_UPDATED_BY, ATTRIBUTE_SAVE_CONFIG_ON_EXIT,
            //ATTRIBUTE_AUDIT_LOG_ENABLED,
            ATTRIBUTE_EXECUTION_CONTEXTS,
            ATTRIBUTE_SUPPORTED_GROUPS, ATTRIBUTE_SUPPORTED_ROLES,
            ATTRIBUTE_CATEGORY, ATTRIBUTE_MENUSCOPE,
            ATTRIBUTE_DEFAULT_CATEGORY, ATTRIBUTE_IS_ALARM_ACTION,
            ATTRIBUTE_XMLDATA, ATTRIBUTE_LOCKED,
            ATTRIBUTE_ROLLBACK_SCRIPT,
            ATTRIBUTE_POSTPROCESS_SCRIPT,
            ATTRIBUTE_SCRIPT_OWNER
    };

    public static final String[] EXPORT_OMITTED_ATTRIBUTE_NAMES = {
            ATTRIBUTE_UPDATE_DATE, ATTRIBUTE_LAST_UPDATED_BY,
            ATTRIBUTE_IS_ALARM_ACTION, ATTRIBUTE_LOCKED, "scriptTasks" };

    static public final StringArray NAME_ATTRIBUTE_KEY = new StringArray(
            new String[] {Script.TYPE, Script.ATTRIBUTE_NAME });

    static public final StringArray COMMENTS_ATTRIBUTE_KEY = new StringArray(
            new String[] {Script.TYPE, Script.ATTRIBUTE_COMMENTS });

    static public final StringArray AUTHOR_ATTRIBUTE_KEY = new StringArray(
            new String[] {Script.TYPE, Script.ATTRIBUTE_CREATOR });

    static public final StringArray LAST_UPDATE_TIME_ATTRIBUTE_KEY = new StringArray(
            new String[] {Script.TYPE, Script.ATTRIBUTE_UPDATE_DATE });

    static public final StringArray CONTENT_ATTRIBUTE_KEY = new StringArray(
            new String[] {Script.TYPE, Script.ATTRIBUTE_CONTENT });

    static public final StringArray SAVE_CONFIG_ATTRIBUTE_KEY = new StringArray(
            new String[] {Script.TYPE,
                    Script.ATTRIBUTE_SAVE_CONFIG_ON_EXIT });

//    static public final scripting.StringArray AUDIT_LOG_ATTRIBUTE_KEY = new scripting.StringArray(
//                    new String[] {scripting.Script.TYPE,
//                            scripting.Script.ATTRIBUTE_AUDIT_LOG_ENABLED });


    public enum SERVER_PROPERTY implements EnumTextValue
    {
        SERVER_VERSION("SERVER_VERSION", "serverVersion"),
        HOSTNAME("HOSTNAME", "hostname"),
        IP_ADDRESS("IP_ADDRESS", "ipAddress"),
        CLIENT_CONNECT_ADDRESS("CLIENT_CONNECT_ADDRESS", "clientConnectAddress"),
        HTTP_PORT("HTTP_PORT", "httpPort"),
        TELNET_PORT("TELNET_PORT", "telnetPort"),
        SSH_PORT("SSH_PORT"),
        TFTP_CONFIG_DIR("TFTP_CONFIG_DIR"),
        TFTP_ROOT("TFTP_ROOT"),
        CONFIG_TIMEOUT("CONFIG_TIMEOUT"),
        // returns server hostname for multiple interface server. ip address for single interface
        SERVER_ADDRESS("SERVER_ADDRESS"),
        VMM_FTP_USER("VMM_FTP_USER"),
        VMM_FTP_PASSWD("VMM_FTP_PASSWD"),
        SNMP_CONNECT_SERVER_ADDRESS("SNMP_CONNECT_SERVER_ADDRESS"),
        SYSTEM_USER_NAME("SYSTEM_USER_NAME"),
        SNMP_TRAP_RECEIVER_PORT("SNMP_TRAP_RECEIVER_PORT"),
        NON_INTRUSIVE_MODE("NON_INTRUSIVE_MODE","nonIntrusiveMode"),
        DO_NOT_REGISTER_TRAPS("DO_NOT_REGISTER_TRAPS","doNotRegisterTrapReceiver"),
        //CLI_SSH_LICENSE("CLI_SSH_LICENSE",PROPERTY_TYPE.NAMEVALUE),
        LICENSES("LICENSES"),
        AUTOMATIC_CONFIGURATION_SAVE("AUTOMATIC_CONFIGURATION_SAVE")
        ;


        private final String legacyPropertyKey;
        private String name;
       

        private static List<SERVER_PROPERTY> properties = new ArrayList<SERVER_PROPERTY>();

        static
        {
            properties.add(HOSTNAME);
            properties.add(HTTP_PORT);
            properties.add(TELNET_PORT);
            properties.add(SSH_PORT);
            properties.add(TFTP_CONFIG_DIR);
            properties.add(SERVER_ADDRESS);
        }

        SERVER_PROPERTY(String name)
        {
            this(name, name);
        }

        SERVER_PROPERTY(String name, String legacyKey)
        {
            this.name = name;
            this.legacyPropertyKey = legacyKey;
        }

        @Override
        public String getDisplayString()
        {
            return name;
        }

        @Override
        public String getVerboseDisplayString()
        {
            return name;
        }

        @Override
        public String toString()
        {
            return getDisplayString();
        }

        public static List<SERVER_PROPERTY> getAllPropertiesList()
        {
            return (properties);
        }

     
    }

    public enum SYSTEM_VARIABLES implements EnumTextValue
    {
        DEVICE_NAME("deviceName", "Dns name of selected device."),
        DEVICE_IP("deviceIP", "IP address of the selected device."),
        DEVICE_LOGIN("deviceLogin", "Login user for the selected device."),
        DEVICE_SOFTWARE_VERSION("deviceSoftwareVer","Software image version nunmber on the device."),
        DEVICE_TYPE("deviceType", "Device type of the selected device."),
        PORT("port", "Selected ports, represented in one string."),
        SERVER_NAME("serverName","Host machine name of the server."),
        SERVER_IP("serverIP", "Server IP Address."),
        SERVER_PORT("serverPort", "Server web port."),
        TIME("time","Current time at server (HH:mm:ss z)."),
        DATE("date","Current date at server (yyyy-MM-dd)."),
        STATUS("STATUS", "Last CLI command execution status. 0: success, non-zero: not successful."),
        ABORT_ON_ERROR("abort_on_error","0: abort on error flag is set; 1: otherwise"),
        //EPICENTER_USER("epicenterUser", "User running script."), 
        NETSIGHT_USER("netsightUser", "User running script."),
        CLI_SESSION_TYPE("CLI.SESSION_TYPE","current session type (telnet / ssh)"),
        IS_EXOS("isExos", "true / false. if this device is XOS device?"),
        VENDOR("vendor","Extreme, if it is Extreme device. Vendor name, otherwise."),
        CLIOUT("CLI.OUT", "Output of the last CLI command."),
        NMS_SERVER_ADDRESS("nmsServerAddress","Server host name if multiple interfaces enabled, ip address if one interface."),
        ;

        private String displayString;

        private String verboseDisplayString;


        private SYSTEM_VARIABLES(String displayString,
                                 String verboseDisplayString)
        {
            this.displayString = displayString;
            this.verboseDisplayString = verboseDisplayString;
        }


        /**
         * @see com.extremenetworks.epicenter.common.interfaces.EnumTextValue#getDisplayString()
         */
        @Override
        public String getDisplayString()
        {
            return (displayString);
        }


        /**
         * @param name
         *            The name to set.
         */
        public void setDisplayString(String displayString)
        {
            this.displayString = displayString;
        }


        /**
         * @see com.extremenetworks.epicenter.common.interfaces.EnumTextValue#getVerboseDisplayString()
         */
        @Override
        public String getVerboseDisplayString()
        {
            return verboseDisplayString;
        }


        @Override
        public String toString()
        {
            return (displayString);
        }
    }


    public enum MENU_SCOPE implements EnumTextValue
    {
        SERVICES("Services"), TOOLS("Tools"), NONE("None");

        private final String displayString;

        private final String verboseDisplayString;


        MENU_SCOPE(String displayString)
        {
            this.displayString = displayString;
            this.verboseDisplayString = displayString;
        }


        public static int toInt(MENU_SCOPE scope)
        {
            int ret = 2; // none
            switch (scope)
            {
                case SERVICES:
                    ret = 0;
                    break;
                case TOOLS:
                    ret = 1;
                    break;
            }
            return (ret);
        }

        public static MENU_SCOPE getMenuScope(int scope)
        {
            MENU_SCOPE ret = MENU_SCOPE.NONE;//2; // none
            switch (scope)
            {
                case 0:
                    ret = MENU_SCOPE.SERVICES;
                    break;
                case 1:
                    ret = MENU_SCOPE.TOOLS;
                    break;
            }
            return (ret);
        }


        @Override
        public String getDisplayString()
        {
            return displayString;
        }


        @Override
        public String getVerboseDisplayString()
        {
            return verboseDisplayString;
        }


        @Override
        public String toString()
        {
            return getDisplayString();
        }
    }


    public enum ExecutionContext implements EnumTextValue
    {
        DEVICE("Device"), DEVICEGROUP("DeviceGroup"), PORT("Port"), PortGroup(
            "PortGroup");

        private final String displayString;


        ExecutionContext(String displayString)
        {
            this.displayString = displayString;
        }


        @Override
        public String getDisplayString()
        {
            return displayString;
        }


        @Override
        public String getVerboseDisplayString()
        {
            return displayString;
        }

    }


    public String getName();


    public void setName(String name);


    public String getComments();


    public void setComments(String comments);


    public String getContent();


    public void setContent(String content);


    public String getExecutionContexts();


    public void setExecutionContexts(String executionContexts);


    public boolean getSaveConfigOnExit();


    public void setSaveConfigOnExit(boolean saveOnExit);


    //public boolean getAuditLogEnabled();


    //public void setAuditLogEnabled(boolean auditLogEnabled);


    public String getSupportedGroups();


    public void setSupportedGroups(String supportedGroups);


    public String getSupportedRoles();


    public void setSupportedRoles(String supportedRoles);


    public String getCategory();


    public void setCategory(String category);


    public String getDefaultCategory();


    public void setDefaultCategory(String defaultCategory);


    public String getMiscXMLData();


    public void setMiscXMLData(String miscXMLData);


    public MENU_SCOPE GetMenuScope();


    public void SetMenuScope(MENU_SCOPE menuScope);


//    public boolean getLocked();
//
//
//    public void setLocked(boolean locked);

    public String getRollbackScript();
    public void setRollbackScript(String rollbackScript);

    public String getPostprocessScript();
    public void setPostprocessScript(String postprocessScript);

    public String getScriptOwner();
    public void setScriptOwner(String scriptOwner);

    public String getCreator();
    public void setCreator(String creator);

    public Long getCreationDate();
    public void setCreationDate(Long creationDate);

    public String getLastUpdatedBy();
    public void setLastUpdatedBy(String lastUpdatedBy);

    public Long getLastUpdateDate();
    public void setLastUpdateDate(Long lastUpdateDate);
    
    String CONFIG_SCRIPT_CATEGORY ="Config";
}
