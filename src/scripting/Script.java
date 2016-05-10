package scripting;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines EPICenter script entity.
 */
public interface Script {
    String TYPE = "scripting/Script";

    String ATTRIBUTE_NAME = "name";

    String ATTRIBUTE_COMMENTS = "comments";

    String ATTRIBUTE_CONTENT = "content";

    String ATTRIBUTE_SAVE_CONFIG_ON_EXIT = "saveConfigOnExit";

    //String ATTRIBUTE_AUDIT_LOG_ENABLED = "auditLogEnabled";

    String ATTRIBUTE_EXECUTION_CONTEXTS = "executionContexts";

    String ATTRIBUTE_SUPPORTED_GROUPS = "supportedGroups";

    String ATTRIBUTE_SUPPORTED_ROLES = "supportedRoles";

    String ATTRIBUTE_CATEGORY = "category";

    String ATTRIBUTE_DEFAULT_CATEGORY = "defaultCategory";

    String ATTRIBUTE_XMLDATA = "miscXMLData";

    String ATTRIBUTE_MENUSCOPE = "menuScope";

    String RELATION_USERROLE = "scriptRoles";

    String ATTRIBUTE_IS_ALARM_ACTION = "isAlarmAction";

    String ATTRIBUTE_SCRIPT_TIMEOUT = "scriptTimeout";

    String ATTRIBUTE_ROLLBACK_SCRIPT = "rollbackScript";

    String ATTRIBUTE_POSTPROCESS_SCRIPT = "postprocessScript";

    String ATTRIBUTE_LOCKED = "locked";

    String ATTRIBUTE_SCRIPT_OWNER = "scriptOwner";

    String ATTRIBUTE_CREATOR = "creator";
    String ATTRIBUTE_CREATTION_DATE = "creationDate";
    String ATTRIBUTE_LAST_UPDATED_BY = "lastUpdatedBy";
    String ATTRIBUTE_UPDATE_DATE = "lastUpdateDate";

    String[] ATTRIBUTE_NAMES = {ATTRIBUTE_NAME,
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

    String CONFIG_SCRIPT_CATEGORY = "Config";

    String getName();

    void setName(String name);

    String getComments();

    void setComments(String comments);

    String getContent();

    void setContent(String content);

    String getExecutionContexts();

    void setExecutionContexts(String executionContexts);

    boolean getSaveConfigOnExit();

    void setSaveConfigOnExit(boolean saveOnExit);

    String getSupportedGroups();

    void setSupportedGroups(String supportedGroups);

    String getSupportedRoles();


    //boolean getAuditLogEnabled();


    //void setAuditLogEnabled(boolean auditLogEnabled);

    void setSupportedRoles(String supportedRoles);

    String getCategory();

    void setCategory(String category);

    String getDefaultCategory();

    void setDefaultCategory(String defaultCategory);

    String getMiscXMLData();

    void setMiscXMLData(String miscXMLData);

    MENU_SCOPE GetMenuScope();

    void SetMenuScope(MENU_SCOPE menuScope);

    String getRollbackScript();

    void setRollbackScript(String rollbackScript);

    String getPostprocessScript();


//    boolean getLocked();
//
//
//    void setLocked(boolean locked);

    void setPostprocessScript(String postprocessScript);

    String getScriptOwner();

    void setScriptOwner(String scriptOwner);

    String getCreator();

    void setCreator(String creator);

    Long getCreationDate();

    void setCreationDate(Long creationDate);

    String getLastUpdatedBy();

    void setLastUpdatedBy(String lastUpdatedBy);

    Long getLastUpdateDate();

    void setLastUpdateDate(Long lastUpdateDate);

    enum SERVER_PROPERTY implements EnumTextValue {
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
        NON_INTRUSIVE_MODE("NON_INTRUSIVE_MODE", "nonIntrusiveMode"),
        DO_NOT_REGISTER_TRAPS("DO_NOT_REGISTER_TRAPS", "doNotRegisterTrapReceiver"),
        //CLI_SSH_LICENSE("CLI_SSH_LICENSE",PROPERTY_TYPE.NAMEVALUE),
        LICENSES("LICENSES"),
        AUTOMATIC_CONFIGURATION_SAVE("AUTOMATIC_CONFIGURATION_SAVE");


        private static List<SERVER_PROPERTY> properties = new ArrayList<SERVER_PROPERTY>();

        static {
            properties.add(HOSTNAME);
            properties.add(HTTP_PORT);
            properties.add(TELNET_PORT);
            properties.add(SSH_PORT);
            properties.add(TFTP_CONFIG_DIR);
            properties.add(SERVER_ADDRESS);
        }

        private final String legacyPropertyKey;
        private String name;

        SERVER_PROPERTY(String name) {
            this(name, name);
        }

        SERVER_PROPERTY(String name, String legacyKey) {
            this.name = name;
            this.legacyPropertyKey = legacyKey;
        }

        public static List<SERVER_PROPERTY> getAllPropertiesList() {
            return (properties);
        }

        @Override
        public String getDisplayString() {
            return name;
        }

        @Override
        public String getVerboseDisplayString() {
            return name;
        }

        @Override
        public String toString() {
            return getDisplayString();
        }


    }

    enum SYSTEM_VARIABLES implements EnumTextValue {
        DEVICE_NAME("deviceName", "Dns name of selected device."),
        DEVICE_IP("deviceIP", "IP address of the selected device."),
        DEVICE_LOGIN("deviceLogin", "Login user for the selected device."),
        DEVICE_SOFTWARE_VERSION("deviceSoftwareVer", "Software image version nunmber on the device."),
        DEVICE_TYPE("deviceType", "Device type of the selected device."),
        PORT("port", "Selected ports, represented in one string."),
        SERVER_NAME("serverName", "Host machine name of the server."),
        SERVER_IP("serverIP", "Server IP Address."),
        SERVER_PORT("serverPort", "Server web port."),
        TIME("time", "Current time at server (HH:mm:ss z)."),
        DATE("date", "Current date at server (yyyy-MM-dd)."),
        STATUS("STATUS", "Last CLI command execution status. 0: success, non-zero: not successful."),
        ABORT_ON_ERROR("abort_on_error", "0: abort on error flag is set; 1: otherwise"),
        //EPICENTER_USER("epicenterUser", "User running script."),
        NETSIGHT_USER("netsightUser", "User running script."),
        CLI_SESSION_TYPE("CLI.SESSION_TYPE", "current session type (telnet / ssh)"),
        IS_EXOS("isExos", "true / false. if this device is XOS device?"),
        VENDOR("vendor", "Extreme, if it is Extreme device. Vendor name, otherwise."),


        CLIOUT("CLI.OUT", "Output of the last CLI command."),
        CLI_CMD_SAVE("saveCommand", "Save config command."),
        CLI_PROMPT_CMD("commandPrompt", "Command pompt."),
        CLI_PROMPT_CMD_REPLY("commandReply", "Command prompt reply."),
        CLI_PROMPT_SHELL("shellPrompt", "System shell prompt."),

        //
        AUDITLOG_ENABLED("auditLogEnabled", "true /false if audit log is supported."),
        SCRIPT_TIMEOUT("scriptTimeout", "Max script timeout in secs."),
        SCRIPT_OWNER("scriptOwner", "Script owner."),

        NMS_SERVER_ADDRESS("nmsServerAddress", "Server host name if multiple interfaces enabled, ip address if one interface."),;


        private String displayString;

        private String verboseDisplayString;


        private SYSTEM_VARIABLES(String displayString,
                                 String verboseDisplayString) {
            this.displayString = displayString;
            this.verboseDisplayString = verboseDisplayString;
        }


        @Override
        public String getDisplayString() {
            return (displayString);
        }


        /**
         */
        public void setDisplayString(String displayString) {
            this.displayString = displayString;
        }


        /**
         */
        @Override
        public String getVerboseDisplayString() {
            return verboseDisplayString;
        }


        @Override
        public String toString() {
            return (displayString);
        }
    }

    enum MENU_SCOPE implements EnumTextValue {
        SERVICES("Services"), TOOLS("Tools"), NONE("None");

        private final String displayString;

        private final String verboseDisplayString;


        MENU_SCOPE(String displayString) {
            this.displayString = displayString;
            this.verboseDisplayString = displayString;
        }


        public static int toInt(MENU_SCOPE scope) {
            int ret = 2; // none
            switch (scope) {
                case SERVICES:
                    ret = 0;
                    break;
                case TOOLS:
                    ret = 1;
                    break;
            }
            return (ret);
        }

        public static MENU_SCOPE getMenuScope(int scope) {
            MENU_SCOPE ret = MENU_SCOPE.NONE;//2; // none
            switch (scope) {
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

    enum ExecutionContext implements EnumTextValue {
        DEVICE("Device"), DEVICEGROUP("DeviceGroup"), PORT("Port"), PortGroup(
                "PortGroup");

        private final String displayString;


        ExecutionContext(String displayString) {
            this.displayString = displayString;
        }


        @Override
        public String getDisplayString() {
            return displayString;
        }


        @Override
        public String getVerboseDisplayString() {
            return displayString;
        }

    }
}
