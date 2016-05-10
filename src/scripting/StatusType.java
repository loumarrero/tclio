package scripting;

import java.io.Serializable;

public enum StatusType implements Serializable, EnumTextValue {
    /**
     * IN_PROGRESS, NO_ERROR, etc values are set to empty string for demo purpose.  This hardcoding should be removed. Revert this file to prev version.
     */
    //common 00-19 for infos, 20-39 for warnings, 40-99 for errors
    INFORMATIONAL("Info", "0000", Severity.INFO),
    NO_ERROR("", "0001", Severity.INFO),
    OFFLINE_DEVICE("Device is offline", "0002", Severity.WARNING),
    IN_PROGRESS("", "0003", Severity.INFO),
    SKIP_POLL("The system is not initialized for poll", "0004", Severity.INFO),
    ENTITY_NOT_FOUND("Entity not found in system", "0005", Severity.ERROR),
    DEVICE_NOT_READY_ERROR("Device is not ready. I may be offline or unavailable at the moment.", "0006", Severity.WARNING),
    UNKNOWN_ERROR("An Unknown error occured", "0007", Severity.ERROR),
    DEVICE_NOT_MACRO_READY("Unable to reach device. This may be because the username/password is incorrect, or the device is down.", "0008", Severity.WARNING),
    COMPLETED("Completed", "0009", Severity.INFO),
    IMPORTING_DATA("Importing data in to database", "0010", Severity.INFO),

    WARNING("Warning", "0020", Severity.WARNING),
    STALE_OBJECT("The entity's state is stale", "0021", Severity.WARNING),

    ERROR("Error", "0030", Severity.ERROR), // Deprecated. Use UNSUCCESSFUL
    UNSUCCESSFUL("", "0036", Severity.ERROR), // Use this instead of ERROR
    PORT_STRING_FORMAT_ERROR("The port string has a wrong format", "0031", Severity.ERROR),
    PORT_STRING_NOT_FOUND_ERROR("The port string has non-existing ports", "0032", Severity.ERROR),
    TIMEOUT_ERROR("Failed due to timeout", "0033", Severity.ERROR),
    DEVICE_NOT_EXIST("Device doesn't exist", "0034", Severity.ERROR),
    REQUEST_INVALID_ERROR("Request is invalid.", "0035", Severity.ERROR),
    INTERNAL_ERROR("Internal error occurred. Please try after some time.", "0036", Severity.ERROR),


    IN_USE("Request requires a resource that is already in use.", "0040", Severity.ERROR),
    INVALID_VALUE("The request specifies an unacceptable value for one or more parameters.", "0041", Severity.ERROR),
    OPERATION_NOT_SUPPORTED("The request could not be completed since the operation is " +
            "not supported by the implementation.", "0042", Severity.ERROR),


    SERVER_EXCEPTION("Exception occurred", "0199", Severity.ERROR),


    // Scripting Use. Error range 601 - 700
    SCRIPTING_INTERNAL_ERROR_RUN("An internal error orrcured running script.", "602", Severity.ERROR),
    SCRIPTING_INTERNAL_ERROR_SENDRESPONS("An internal error occured sending response to client.", "603", Severity.ERROR),
    SCRIPTING_VALIDATION_ERROR("scripting.Script validation failed.", "604", Severity.ERROR),
    SCRIPTING_TRANSLATION_ERROR("scripting.Script translation failed.", "605", Severity.ERROR),
    SCRIPTING_SCRIPT_NOT_FOUND_ERROR("scripting.Script not found at server.", "606", Severity.ERROR),
    SCRIPTING_DEVICE_COMMUNICATION_ERROR("Error communicating to the device. Verify that device username and password is correct.", "607", Severity.ERROR),
    SCRIPTING_RUNSCRIPT_STOPPED_INFO("scripting.Script execution for this device was stopped.", "608", Severity.INFO),
    STOPPED("Stopped", "609", Severity.INFO),
    NOT_STARTED("", "610", Severity.INFO),
    SCRIPTING_INVALID_RUNREQUEST_ERROR("scripting.Script run request not valid. Please check that run timeout is correct.", "611", Severity.ERROR),
    SCRIPTING_TIME_OUT("Timeout", "612", Severity.INFO),
    SCRIPTING_RUNSCRIPT_COMMAND_TIMEOUT("Error: Command execution on server timed out. " +
            "The command may still be running on device. Refresh results to see execution log.\n" +
            "If this is valid command and you would like to re-run, increase your request timeout.", "613", Severity.ERROR),

    PROVISIONING_COMPLEMENTARY_SCRIPT_NOT_FOUND("scripting.Script not found.", "0706", Severity.ERROR),
    PROVISIONING_DEVICE_NOT_FOUND("One or more devices not found.", "0709", Severity.ERROR),
    PROVISIONING_ROLLEDBACK("Unsuccessful and Rolledback", "0718", Severity.ERROR),

    // Provisioning info (0750 - 0799)
    PROVISIONING_VALIDATE_DEVICE_LOGIN("Validating connectivity to switches...", "0750", Severity.INFO),
    PROVISIONING_VALIDATE_DEVICE_NOT_FOUND("Provisioning request without device", "0751", Severity.ERROR),
    PROVISIONING_COMPLEMENTARY_SCRIPT_NOT_SET("Complementary script not set in the original script definition.", "0752", Severity.INFO),
    PROVISIONING_DEVICE_CLI_ACCESS("Device credentials could not be validated", "0753", Severity.ERROR),

    //VLAN provisioning Error messages. Error range 900 - 1100
    PROVISIONING_VLAN_NAME_INVALID_LENGTH("Name should not be more than 32 characters", "900", Severity.ERROR),
    PROVISIONING_VLAN_NAME_INVALID_CHARACTERS("Name should be alphanumeric characters", "901", Severity.ERROR),
    PROVISIONING_VLAN_ID_INVALID("Tag range should be between 1-4095", "902", Severity.ERROR),
    PROVISIONING_VLAN_NAME_SHOULD_NOT_EXIST_AS_CTRL_VLAN("Name should not be present on selected device list as Control VLAN name", "903", Severity.ERROR),
    PROVISIONING_NETWORK_VLAN_NOT_SET("Network service should be set as input", "904", Severity.ERROR),
    PROVISIONING_NETWORK_VLAN_DOES_NOT_EXIST("Selected network service should be present in the system", "905", Severity.ERROR),
    PROVISIONING_NETWORK_VLAN_EAPS_CTRL_VLAN_VALIDATION("EAPS domain control VLAN validation", "906", Severity.ERROR),
    PROVISIONING_NETWORK_VLAN_EAPS_PROT_VLAN_VALIDATION("EAPS domain protection validation", "907", Severity.ERROR),
    PROVISIONING_SERVICE_VLAN("Selected network service should not be used as a transport service in an E-Line/E-LAN Service", "908", Severity.ERROR),
    PROVISIONING_VLAN_TAG_SHOULD_NOT_EXIST_AS_CTRL_VLAN("Tag should not be present on selected device as Control VLAN tag", "909", Severity.ERROR),
    PROVISIONING_VLAN_UNTAGGED_PORT_ERROR("Protocol conflict should not be there when adding untagged port", "910", Severity.ERROR),
    PROVISIONING_VLAN_NAME_SHOULD_NOT_EXIST_ON_DEVICE("Name should not be present on selected device", "912", Severity.ERROR),
    PROVISIONING_VLAN_TAG_SHOULD_NOT_EXIST_ON_DEVICE("Tag should not be present on selected device", "913", Severity.ERROR),
    PROVISIONING_EXOS_VERSION_CHECK("Switch software compatibility check", "914", Severity.ERROR),
    PROVISIONING_VLAN_DAFULT_VLAN_ERROR("Default vlan can not be used to create a E-Line/E-LAN service", "915", Severity.ERROR),
    PROVISIONING_UNTAGGED_VLAN_PORT_STS_ERROR("Port should not be added as a tagged port to an untagged VLAN", "916", Severity.ERROR),
    PROVISIONING_NO_VLANS_IN_NTWK_VLAN("Selected network service is not present on any of the devices in the network", "917", Severity.ERROR),
    PROVISIONING_VLAN_UNI_PORT_INVALID("Port tag value should not be invalid", "918", Severity.ERROR),
    PROVISIONING_BVLAN_PORT_STS_ERROR("Port can not be added as a untagged port to BVLAN", "919", Severity.ERROR),
    PROVISIONING_TAGGED_CVLAN_UNI_PORT_INVALID("A tagged port in CVLAN can not be added to BVLAN", "920", Severity.ERROR),
    PROVISIONING_UNTAGGED_CVLAN_UNI_PORT_INVALID("An untagged port in CVLAN can not be added to BVLAN", "921", Severity.ERROR),
    PROVISIONING_TAGGED_SVLAN_UNI_PORT_INVALID("A tagged Port in SVLAN can not be added to BVLAN", "922", Severity.ERROR),
    PROVISIONING_UNTAGGED_SVLAN_UNI_PORT_INVALID("An untagged Port in SVLAN can not be added to BVLAN", "923", Severity.ERROR),
    PROVISIONING_UNTAGGED_VLAN_ERROR("Untagged Network VLAN can not be used to create a E-Line/E-LAN service", "924", Severity.ERROR),
    PROVISIONING_EAPS_RING_PORT_ERROR("Ring ports have to be configured on all the selected EAPS domains", "925", Severity.ERROR),
    PROVISIONING_UNTAGGED_VMAN_ERROR("Untagged Network VMAN can not be used to create a E-Line/E-LAN service", "926", Severity.ERROR),
    PROVISIONING_UNTAGGED_BVLAN_ERROR("Untagged Network BVLAN can not be used to create a E-Line/E-LAN service", "927", Severity.ERROR),
    PROVISIONING_VLAN_NAME_SHOULD_NOT_EXIST_AS_PROT_VLAN("Name should not be present on selected device list as Protected VLAN name", "928", Severity.ERROR),
    PROVISIONING_VLAN_TAG_SHOULD_NOT_EXIST_AS_PROT_VLAN("Tag should not be present on selected device list as Protected VLAN tag", "929", Severity.ERROR),

    //EAPS provisioning Error messages. Error range 1200 - 1500
    EAPS_CONTROLLER_NODE_SHARED_NODE_LIMIT("Shared port instance limit on Controller Node", "1200", Severity.ERROR),
    EAPS_CONTROLLER_NODE_LIMIT("Node's Controller max capacity", "1201", Severity.ERROR),
    EAPS_PARTNER_NODE_SHARED_NODE_LIMIT("Shared port instance limit on Partner Node", "1202", Severity.ERROR),
    EAPS_LINKID_ALREADY_EXISTS("Link Id Unique", "1203", Severity.ERROR),
    EAPS_LINKID_MODIFY_NOT_ALLOWED("Link Id Modification", "1204", Severity.ERROR),
    EAPS_LINKID_RANGE("Link ID Range", "1205", Severity.ERROR),
    EAPS_SEGMENT_TIMER_CHECK("Segment Timer Validity", "1206", Severity.ERROR),
    EAPS_COMMONPATH_TIMER_CHECK("Common path timers Validity", "1207", Severity.ERROR),
    EAPS_SHARING_DOMAINS("Sharing Domains", "1208", Severity.ERROR),
    EAPS_EXISTING_SHAREDLINK("Target EapsShared link exists", "1209", Severity.ERROR),
    EAPS_CONTROLLERPORT_ALREADY_EXISTS_AS_SHAREDPORT("Controller port should not be an Existing Shared-port", "1210", Severity.ERROR),
    EAPS_PARTNERPORT_ALREADY_EXISTS_AS_SHAREDPORT("Partner port should not be an Existing Shared-port", "1211", Severity.ERROR),
    EAPS_COMMONPATH_HEALTHINTERVAL_RANGE("Common path Health Interval Range", "1212", Severity.ERROR),
    EAPS_COMMONPATH_TIMEOUT_RANGE("Common path Timeout Range", "1213", Severity.ERROR),
    EAPS_SEGMENT_HEALTHINTERVAL_RANGE("Segment Health Interval Range", "1214", Severity.ERROR),
    EAPS_SEGMENT_TIMEOUT_RANGE("Segment Timeout Range", "1215", Severity.ERROR),


    EAPS_DOMAINNAME_LENGTH("Domain name length", "1300", Severity.ERROR),
    EAPS_DOMAINNAME_CHARSET("Domain name characters", "1301", Severity.ERROR),
    EAPS_DOMAINNAME_UNIQUE("Domain name uniquness", "1302", Severity.ERROR),

    EAPS_CLOSED_LOOP("Closed loop", "1303", Severity.ERROR),
    EAPS_MULTIPLE_CLOSED_LOOP("Multiple Closed loop", "1304", Severity.ERROR),
    EAPS_MASTER_NODE_EXIST_IN_RING("Master Node must be existing node in ring", "1305", Severity.ERROR),
    EAPS_MASTER_DOMAINPORT_EXISTS_IN_RING("Master's domain port must represent a link in ring", "1306", Severity.ERROR),
    EAPS_MASTER_SECPORT_NOT_SHARED("Master's secondary port must not be shared", "1307", Severity.ERROR),
    EAPS_CTRLVLAN_ALREADY_EXIST("Control VLAN must not be an existing Control VLAN", "1308", Severity.ERROR),
    EAPS_CTRLVLAN_ALREADY_PROTECTEDVLAN("Control VLAN must not be an existing Protected VLAN", "1309", Severity.ERROR),
    EAPS_CTRLVLAN_ALREADY_DATAVLAN("Control VLAN must not be an existing Data VLAN", "1310", Severity.ERROR),
    EAPS_DOMAIN_PROTECTS_VLAN("Domain protects Vlan", "1311", Severity.ERROR),
    EAPS_OVERLAPDOMAIN_PRESENT("Overlap Domains present on same Physical Ring", "1312", Severity.ERROR),
    EAPS_OVERLAPDOMAIN_LIMIT("Overlap Domain limit on same Physical Ring", "1313", Severity.ERROR),
    EAPS_OVERLAPDOMAIN_MASTER("Master Node conflict in Overlapped domain", "1314", Severity.ERROR),
    EAPS_HEALTH_TIMER("Domain Health Timer", "1315", Severity.ERROR),
    EAPS_COMMON_NODE_HAS_COMMON_PHYLINK("Common Nodes must have Common Physical Link",
            "1316", Severity.ERROR),
    EXISTING_SHAREDLINK_IN_COMMONNODES("Common Physical link between Common Nodes must be shared", "1317", Severity.ERROR),
    EAPS_ADJACENT_DOMAIN_COMMONNODE_LIMIT("Common node limit with adjacent domain", "1318", Severity.ERROR),
    COMMON_LINKS_OVERLAPS_ON_EXISTING_PHYRING("Common links limit on an existing physical ring", "1319", Severity.ERROR),
    COMMON_LINK_SHARED("Common link with another Domain (different Physical Ring) must be a Shared link", "1320", Severity.ERROR),
    EAPS_DOMAIN_EXISTS("Target Eaps Domain exists", "1321", Severity.ERROR),
    EAPS_CTRLVLAN_NAME_LENGTH("Control VLAN name length", "1322", Severity.ERROR),
    EAPS_CTRLVLAN_NAME_CHARACTERS("Control VLAN name characterss", "1323", Severity.ERROR),
    EAPS_CTRLVLAN_ID_RANGE("Control VLAN ID Range", "1324", Severity.ERROR),
    EAPS_SWVER_CHECK("EAPS software compatibility check", "1325", Severity.ERROR),
    EAPS_SHAREDLINK_SWVER_CHECK("EAPS Shared-Link Switch software compatibility check", "1326", Severity.ERROR),
    EAPS_SHAREDLNK_TIMER_SWITCH_SWVER_CHECK("Switch software compatibility check", "1327", Severity.ERROR),
    EAPS_DOMAINPORT_MLAGENABLED("Domain Ports must not be MLAG enabled ports", "1328", Severity.ERROR),
    EAPS_MASTER_NODE_NOT_CHANGED("Master Node has not changed", "1329", Severity.ERROR),

    PARTIAL_ERROR("", "1800", Severity.ERROR),
    SKIPPED("Skipped", "1801", Severity.ERROR),
    NOT_EXECUTED("Not Executed", "1802", Severity.INFO),

    RL_ADMIN_PROPERTY_NOT_PRESENT("The property with the provided name is not present", "4005", Severity.ERROR),
    RL_ADMIN_INIT_PROPERTIES_METADATA("Failed to initialize the properties-metadata configuration file", "4011", Severity.ERROR),
    REST_ROW_LIMIT("Number of return rows exceeds the configured limit", "5000", Severity.ERROR),
    REQUIRED("Required", "8000", Severity.INFO),
    NOT_REQUIRED("Not Required", "8001", Severity.INFO),
    REQUEST_CANCELLED("Request execution cancelled", "8002", Severity.INFO),;


    private final String displayString;
    private final String verboseDisplayString;
    private final String statusCode;
    private final Severity severity;
    private final ErrorType errorType;

    StatusType(String displayString, String statusCode, Severity severity) {
        this(displayString, statusCode, severity, ErrorType.APPLICATION);
    }

    StatusType(String displayString, String statusCode, Severity severity, ErrorType errorType) {
        this.displayString = displayString;
        this.verboseDisplayString = displayString;
        this.statusCode = statusCode;
        this.severity = severity;
        this.errorType = errorType;
    }

    public static StatusType getStatusType(StatusCode status) {
        switch (status) {
            case SUCCESSFUL:
                return NO_ERROR;

            case PARTIAL:
                return PARTIAL_ERROR;

            case UNSUCCESSFULL:
                return UNSUCCESSFUL;
            default:
                return NO_ERROR;
        }
    }

    public String getStatusCode() {
        return statusCode;
    }

    public Severity getSeverity() {
        return severity;
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

    /**
     * @return Returns the errorType.
     */
    public ErrorType getErrorType() {
        return this.errorType;
    }
}
