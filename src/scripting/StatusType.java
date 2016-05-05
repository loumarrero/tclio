package scripting;

import java.io.Serializable;

public enum StatusType implements Serializable, EnumTextValue
{
    /**
     * IN_PROGRESS, NO_ERROR, etc values are set to empty string for demo purpose.  This hardcoding should be removed. Revert this file to prev version.
     */
    //common 00-19 for infos, 20-39 for warnings, 40-99 for errors
    INFORMATIONAL ("Info", "0000", Severity.INFO),
    NO_ERROR ("", "0001", Severity.INFO),
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


    // EAPS use
//    NO_MASTER_NODE ("No Master Node", "0100", scripting.Severity.ERROR),
//    MULTIPLE_MASTER_NODES ("Multiple Master Nodes", "0101", scripting.Severity.ERROR),
//    DISABLED_EAPS_NODE ("Disabled EAPS Node", "0102", scripting.Severity.ERROR),
//    MISSING_CONTROL_VLAN ("Missing Control VLAN", "0103", scripting.Severity.ERROR),
//    MISSING_PRI_DOMAIN_PORT ("Missing Primary domain port", "0104", scripting.Severity.ERROR),
//    MISSING_SEC_DOMAIN_PORT ("Missing secondary domain port", "0105", scripting.Severity.ERROR),
//    MIS_MATCHED_DOMAIN_PORTS ("Mismatched Domain Ports", "0106", scripting.Severity.ERROR),
//    INCOMPLETE_VLAN_PROTECTION ("Incomplete VLAN Protection", "0107", scripting.Severity.ERROR),
//    INCONSISTENT_CONTROL_VLAN_NAMING ("Inconsistent Control VLAN Naming", "0108", scripting.Severity.ERROR),
//    INCONSISTENT_EAPS_NODE_NAMING ("Inconsistent EAPS Node Naming", "0109", scripting.Severity.ERROR),
//    CONTROL_VLAN_NOT_IN_QP8 ("Control VLAN not in QP8", "0110", scripting.Severity.ERROR),
//    UNPROTECTED_SHARED_LINK ("Unprotected Shared Link", "0111", scripting.Severity.ERROR),
//    DUPLICATE_LINKID ("Duplicate Link ID", "0112", scripting.Severity.ERROR),
//    MISSING_LINKID ("Missing Link ID", "0113", scripting.Severity.ERROR),
//    MIS_MATCHED_LINKID ("Mismatched Link ID", "0114", scripting.Severity.ERROR),
//    MIS_CONFIGURED_SHARED_PORT_MODE ("Misconfigured Shared Port Mode", "0114", scripting.Severity.ERROR),
//    SHARED_PORT_NOT_CREATED ("Shared Port not Created", "0115", scripting.Severity.ERROR),
//    SHARED_PORT_NOT_CONFIGURED ("Shared Port not Configured", "0116", scripting.Severity.ERROR),
//    NO_PHYSICAL_LINK ("No Physical Link", "0117", scripting.Severity.ERROR),
//    DOMAIN_LIST_MISMATCH ("Domain List Mismatch", "0118", scripting.Severity.ERROR),
//    LINKID_NOT_CONFIGURED ("Link ID not Configured", "0119", scripting.Severity.ERROR),
//    CONTROL_VLAN_MIS_CONFIG ("Control VLAN Misconfigured", "0120", scripting.Severity.ERROR),
//    PROTECTED_VLAN_MIS_CONFIG ("Protected VLAN Misconfigured", "0121", scripting.Severity.ERROR),
//    SHARED_PORT_MIS_CONFIG ("Shared Port Misconfigured ", "0122", scripting.Severity.ERROR),
//    CONTROLLER_MISCONFIG ("Controller Misconfigured", "0123", scripting.Severity.ERROR),
//    UNTAGGED_PORTS ("UnTagged Domain ports", "0124", scripting.Severity.ERROR),



    SERVER_EXCEPTION ("Exception occurred", "0199", Severity.ERROR),

    // UPM use.  Error code range: 0200 - 0250
//    MANAGED_PROFILE_NAME_ALREADY_EXISTED("Profile name with this version is not unique", "0201", scripting.Severity.ERROR),
//    PROFILE_ASSOCIATION_REQUEST_FAILED("Profile association request failed", "0200", scripting.Severity.ERROR),
//    PROFILE_ASSOCIATION_REQUEST_PARTIALLY_FAILED("Profile association request partially failed", "0202", scripting.Severity.ERROR),
//    GET_PROFILE_DETAILS_FAILED("Get Profile details failed", "0203", scripting.Severity.ERROR),
//    SYNC_INVENTORY_ERROR("Sync with inventory failed", "0204", scripting.Severity.ERROR),
//    DELETE_PROFILE_FAILED("Could not delete profile", "0205", scripting.Severity.ERROR),
//    BIND_PROFILE_FAILED("Could not bind profile", "0206", scripting.Severity.ERROR),
//    CREATE_PROFILE_FAILED("Could not create profile", "0207", scripting.Severity.ERROR),
//    UNKNOWN_UPM_PROFILE("Unknown UPM Profile", "0208", scripting.Severity.ERROR),
//    UNBIND_PROFILE_FAILED("Could not Unbind profile", "0209", scripting.Severity.ERROR),
//    MODIFY_PROFILE_FAILED("Could not modify profile", "0210", scripting.Severity.ERROR),
//    OVER_WRITE_PROFILE_FAILED("Could not over write profile", "0211", scripting.Severity.ERROR),
//    SWITCH_PROFILE_NOTFOUND("Switch profile not found in server", "0212",scripting.Severity.WARNING),
//    GET_ALL_PROFILE_FAILED("Get profiles from device failed", "0213", scripting.Severity.ERROR),
//    PROFILE_EXISTED("Profile name exists on the switch", "0214", scripting.Severity.WARNING),
//    EVENT_PORT_BINDING_CONFLICT("Cannot bind more than one profile to a port for this event", "0215", scripting.Severity.ERROR),
//    PROFILE_EXECUTION_FAILED("Failed to execute profile", "0216", scripting.Severity.ERROR),
//    GET_PROFILE_EXECUTION_RESULT_FAILED("Failed to get profile execution result", "0217", scripting.Severity.ERROR),
//    PROFILE_EXECUTION_RESULT_NOTFOUND("Could not find the profile execution log on the device", "0218", scripting.Severity.INFO),
//    FAILED_TO_SAVE_CONFIG("Could not save configuration on the device", "219", scripting.Severity.ERROR),
//    FAILED_TO_MODIFY_PROFILE_STATUS("Could not modify the profile status on the device", "220", scripting.Severity.ERROR),
//    FAILED_TO_DELETE_TIMER("Could not delete timer on the device", "221", scripting.Severity.ERROR),
//    PROFILE_EXECUTION_LOG_NOTCREATED("Could not retreive the profile execution log because it was not created properly", "0222", scripting.Severity.INFO),
//    PROFILE_SIZE_EXCEEDS_LIMIT("Profile size exceeds the limit. Maximum size supported is 5000 characters in device.","0223",scripting.Severity.INFO),
//    FAILED_TO_MODIFY_PROFILE_ACTIONS("Could not connect to the SMTP server. Provided SMTP credentials are not valid.", "0224", scripting.Severity.ERROR),
//
//    //Grouping Use. Error Code range 00400 - 0500
//    SYSTEM_GROUP_CREATE_MODIFY__DELETE_FAILED("Can not create/modify/Delete System Groups", "0400", scripting.Severity.ERROR),
//    PARENT_FDN_NOT_SET("Parent FDN is not set to add/delete members", "0401", scripting.Severity.ERROR),
//    GROUP_NOT_FOUND("Group not found", "0402", scripting.Severity.ERROR),
//    FILTER_NOT_SET_IN_FILTERREQUEST("Filter/MemberType is not set in the Request", "0403", scripting.Severity.ERROR),
//    MODIFY_GROUP_FAILED("Failed to modify the group", "0404", scripting.Severity.ERROR),
//    PARENT_GROUP_NOT_FOUND("Could not find the parent group", "0405", scripting.Severity.WARNING),
//    GROUP_ADDMEMBERSHIP_FAILED("Failed to add a member to the group", "406", scripting.Severity.ERROR),
//    GROUP_DELETEMEMBERSHIP_FAILED("Failed to delete a member to the group", "407", scripting.Severity.ERROR),
//    UNSUPPORTED_MEMBER("Selected member can not be added to the group", "408", scripting.Severity.ERROR),
//    FAILED_ADD_UDE_TO_GROUP("Failed to add Cloud/Node/Text to the group", "409", scripting.Severity.ERROR),
//    ENTITY_ALREADY_EXIST("Entity already in one of groups contained by the first level group", "410", scripting.Severity.ERROR),
//    LEGACY_GROUP_OPERATION_FAILED("Internal error occurred. Failed to create/modify/delete group. See server log for details.", "411", scripting.Severity.ERROR),
//
//    //Topology Use. Error Code range 00500 - 0600
//    GLOBAL_MAP_CREATE_DELETE_FAILED("Can not create/delete Global map", "500", scripting.Severity.ERROR),
//    DEFAULT_MAP_CREATE_DELETE_MODIFY_FAILED("Can not create/modify/delete Default map", "501", scripting.Severity.ERROR),
//    CREATE_LINK_FAILED("Failed to create link", "502", scripting.Severity.ERROR),
//    INVALID_ENDPOINTS("Failed to create User created link because the endpoints used to create links are invalid", "503", scripting.Severity.ERROR),


    // Scripting Use. Error range 601 - 700
    SCRIPTING_INTERNAL_ERROR_RUN("An internal error orrcured running script.", "602", Severity.ERROR),
    SCRIPTING_INTERNAL_ERROR_SENDRESPONS("An internal error occured sending response to client.", "603", Severity.ERROR),
    SCRIPTING_VALIDATION_ERROR("scripting.Script validation failed.", "604", Severity.ERROR),
    SCRIPTING_TRANSLATION_ERROR("scripting.Script translation failed.", "605", Severity.ERROR),
    SCRIPTING_SCRIPT_NOT_FOUND_ERROR("scripting.Script not found at server.", "606", Severity.ERROR),
    SCRIPTING_DEVICE_COMMUNICATION_ERROR("Error communicating to the device. Verify that device username and password is correct.", "607", Severity.ERROR),
    SCRIPTING_RUNSCRIPT_STOPPED_INFO("scripting.Script execution for this device was stopped.", "608", Severity.INFO),
    STOPPED ("Stopped", "609", Severity.INFO),
    NOT_STARTED("","610",Severity.INFO),
    SCRIPTING_INVALID_RUNREQUEST_ERROR("scripting.Script run request not valid. Please check that run timeout is correct.", "611", Severity.ERROR),
    SCRIPTING_TIME_OUT ("Timeout", "612", Severity.INFO),
    SCRIPTING_RUNSCRIPT_COMMAND_TIMEOUT("Error: Command execution on server timed out. " +
            "The command may still be running on device. Refresh results to see execution log.\n" +
            "If this is valid command and you would like to re-run, increase your request timeout.", "613", Severity.ERROR),

    //    // Sybase Error code.
//    SYBASE_INDEX_ERROR("SQL Anywhere Error -189", "2727", scripting.Severity.ERROR),
//
//
//    // Provisioning error. Error range 701 - 800
//    PROVISIONING_REQUEST_NOT_SUPPORTED ("Error: This request is not supported by server.", "0701", scripting.Severity.ERROR),
//    PROVISIONING_ROLLBACK_FAILED ("Error: Rollback failed for some reason,", "0702", scripting.Severity.ERROR),
//    PROVISIONING_DEVICE_LOGIN_FAILED ("Error: Could not login into the device.", "0703", scripting.Severity.ERROR),
//    PROVISIONING_DEVICE_LOGIN_SUCCESS ("Success: connectivity and login to target devices successful.", "0704", scripting.Severity.ERROR),
//    PROVISIONING_SCRIPT_NOT_FOUND ("Error : Provisioning script not found.", "0705", scripting.Severity.ERROR),
    PROVISIONING_COMPLEMENTARY_SCRIPT_NOT_FOUND ("scripting.Script not found.", "0706", Severity.ERROR),
    //    PROVISIONING_GLOBAL_PARAMETER_NOT_SET("Global parameters are not set properly.", "0707", scripting.Severity.ERROR),
//    PROVISIONING_DETAIL_PARAMETERS_NOT_SET("One or more parameters are not set properly.", "0708", scripting.Severity.ERROR),
    PROVISIONING_DEVICE_NOT_FOUND("One or more devices not found.", "0709", Severity.ERROR),
    //    PROVISIONING_EAPS_NOT_FOUND("One or more EAPS not found.", "0710", scripting.Severity.ERROR),
//    PROVISIONING_SHAREDLINK_MODIFY_PARAMS_NOT_SET("Error: Shared link modify parameters not set", "0711", scripting.Severity.ERROR),
//    PROVISIONING_MASTER_DEVICE_NOT_SET("Error: Eaps Master device not set", "0712", scripting.Severity.ERROR),
//    PROVISIONING_MODIFY_MASTER_PORT_NOT_SET("Error: Master domain port modify  parameters not set", "0713", scripting.Severity.ERROR),
//    PROVISIONING_INVALID_TARGET_ENTITY_TYPE("Error: Target Entity type is invalid", "0714", scripting.Severity.ERROR),
//    PROVISIONING_EAPSDOMAIN_MODIFY_PARAMS_NOT_SET("Error: Eaps Domain modify parameters not set", "0715", scripting.Severity.ERROR),
//    PROVISIONING_DEVICE_PARAMETERS_NOT_SET("One or more device parameters are not set properly.", "0716", scripting.Severity.ERROR),
//    PROVISIONING_LINK_PARAMETERS_NOT_SET("One or more link parameters are not set properly.", "0717", scripting.Severity.ERROR),
    PROVISIONING_ROLLEDBACK("Unsuccessful and Rolledback", "0718", Severity.ERROR),
//    PROVISIONING_TYPE_NOT_SUPPORTED("Provisioning Type not Supported.","0719", scripting.Severity.ERROR),

    // Provisioning info (0750 - 0799)
    PROVISIONING_VALIDATE_DEVICE_LOGIN ("Validating connectivity to switches...", "0750", Severity.INFO),
    PROVISIONING_VALIDATE_DEVICE_NOT_FOUND ("Provisioning request without device", "0751", Severity.ERROR),
    PROVISIONING_COMPLEMENTARY_SCRIPT_NOT_SET ("Complementary script not set in the original script definition.", "0752", Severity.INFO),
    PROVISIONING_DEVICE_CLI_ACCESS ("Device credentials could not be validated", "0753", Severity.ERROR),

    //VLAN provisioning Error messages. Error range 900 - 1100
    PROVISIONING_VLAN_NAME_INVALID_LENGTH ("Name should not be more than 32 characters", "900", Severity.ERROR),
    PROVISIONING_VLAN_NAME_INVALID_CHARACTERS ("Name should be alphanumeric characters", "901", Severity.ERROR),
    PROVISIONING_VLAN_ID_INVALID ("Tag range should be between 1-4095", "902", Severity.ERROR),
    PROVISIONING_VLAN_NAME_SHOULD_NOT_EXIST_AS_CTRL_VLAN ("Name should not be present on selected device list as Control VLAN name", "903", Severity.ERROR),
    PROVISIONING_NETWORK_VLAN_NOT_SET ("Network service should be set as input", "904", Severity.ERROR),
    PROVISIONING_NETWORK_VLAN_DOES_NOT_EXIST ("Selected network service should be present in the system", "905", Severity.ERROR),
    PROVISIONING_NETWORK_VLAN_EAPS_CTRL_VLAN_VALIDATION("EAPS domain control VLAN validation", "906", Severity.ERROR),
    PROVISIONING_NETWORK_VLAN_EAPS_PROT_VLAN_VALIDATION("EAPS domain protection validation", "907", Severity.ERROR),
    PROVISIONING_SERVICE_VLAN("Selected network service should not be used as a transport service in an E-Line/E-LAN Service", "908", Severity.ERROR),
    PROVISIONING_VLAN_TAG_SHOULD_NOT_EXIST_AS_CTRL_VLAN ("Tag should not be present on selected device as Control VLAN tag", "909", Severity.ERROR),
    PROVISIONING_VLAN_UNTAGGED_PORT_ERROR("Protocol conflict should not be there when adding untagged port", "910", Severity.ERROR),
    PROVISIONING_VLAN_NAME_SHOULD_NOT_EXIST_ON_DEVICE ("Name should not be present on selected device", "912", Severity.ERROR),
    PROVISIONING_VLAN_TAG_SHOULD_NOT_EXIST_ON_DEVICE ("Tag should not be present on selected device", "913", Severity.ERROR),
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
    PROVISIONING_VLAN_NAME_SHOULD_NOT_EXIST_AS_PROT_VLAN ("Name should not be present on selected device list as Protected VLAN name", "928", Severity.ERROR),
    PROVISIONING_VLAN_TAG_SHOULD_NOT_EXIST_AS_PROT_VLAN ("Tag should not be present on selected device list as Protected VLAN tag", "929", Severity.ERROR),

    //EAPS provisioning Error messages. Error range 1200 - 1500
    EAPS_CONTROLLER_NODE_SHARED_NODE_LIMIT ("Shared port instance limit on Controller Node", "1200", Severity.ERROR),
    EAPS_CONTROLLER_NODE_LIMIT ("Node's Controller max capacity", "1201", Severity.ERROR),
    EAPS_PARTNER_NODE_SHARED_NODE_LIMIT ("Shared port instance limit on Partner Node", "1202", Severity.ERROR),
    EAPS_LINKID_ALREADY_EXISTS ("Link Id Unique", "1203", Severity.ERROR),
    EAPS_LINKID_MODIFY_NOT_ALLOWED ("Link Id Modification", "1204", Severity.ERROR),
    EAPS_LINKID_RANGE ("Link ID Range", "1205", Severity.ERROR),
    EAPS_SEGMENT_TIMER_CHECK("Segment Timer Validity", "1206", Severity.ERROR),
    EAPS_COMMONPATH_TIMER_CHECK ("Common path timers Validity", "1207", Severity.ERROR),
    EAPS_SHARING_DOMAINS ("Sharing Domains", "1208", Severity.ERROR),
    EAPS_EXISTING_SHAREDLINK ("Target EapsShared link exists", "1209", Severity.ERROR),
    EAPS_CONTROLLERPORT_ALREADY_EXISTS_AS_SHAREDPORT ("Controller port should not be an Existing Shared-port", "1210", Severity.ERROR),
    EAPS_PARTNERPORT_ALREADY_EXISTS_AS_SHAREDPORT ("Partner port should not be an Existing Shared-port", "1211", Severity.ERROR),
    EAPS_COMMONPATH_HEALTHINTERVAL_RANGE ("Common path Health Interval Range", "1212", Severity.ERROR),
    EAPS_COMMONPATH_TIMEOUT_RANGE ("Common path Timeout Range", "1213", Severity.ERROR),
    EAPS_SEGMENT_HEALTHINTERVAL_RANGE ("Segment Health Interval Range", "1214", Severity.ERROR),
    EAPS_SEGMENT_TIMEOUT_RANGE ("Segment Timeout Range", "1215", Severity.ERROR),


    EAPS_DOMAINNAME_LENGTH ("Domain name length", "1300", Severity.ERROR),
    EAPS_DOMAINNAME_CHARSET ("Domain name characters", "1301", Severity.ERROR),
    EAPS_DOMAINNAME_UNIQUE ("Domain name uniquness", "1302", Severity.ERROR),

    EAPS_CLOSED_LOOP ("Closed loop", "1303", Severity.ERROR),
    EAPS_MULTIPLE_CLOSED_LOOP ("Multiple Closed loop", "1304", Severity.ERROR),
    EAPS_MASTER_NODE_EXIST_IN_RING ("Master Node must be existing node in ring", "1305", Severity.ERROR),
    EAPS_MASTER_DOMAINPORT_EXISTS_IN_RING ("Master's domain port must represent a link in ring", "1306", Severity.ERROR),
    EAPS_MASTER_SECPORT_NOT_SHARED ("Master's secondary port must not be shared", "1307", Severity.ERROR),
    EAPS_CTRLVLAN_ALREADY_EXIST ("Control VLAN must not be an existing Control VLAN", "1308", Severity.ERROR),
    EAPS_CTRLVLAN_ALREADY_PROTECTEDVLAN ("Control VLAN must not be an existing Protected VLAN", "1309", Severity.ERROR),
    EAPS_CTRLVLAN_ALREADY_DATAVLAN ("Control VLAN must not be an existing Data VLAN", "1310", Severity.ERROR),
    EAPS_DOMAIN_PROTECTS_VLAN ("Domain protects Vlan", "1311", Severity.ERROR),
    EAPS_OVERLAPDOMAIN_PRESENT ("Overlap Domains present on same Physical Ring", "1312", Severity.ERROR),
    EAPS_OVERLAPDOMAIN_LIMIT ("Overlap Domain limit on same Physical Ring", "1313", Severity.ERROR),
    EAPS_OVERLAPDOMAIN_MASTER ("Master Node conflict in Overlapped domain", "1314", Severity.ERROR),
    EAPS_HEALTH_TIMER ("Domain Health Timer", "1315", Severity.ERROR),
    EAPS_COMMON_NODE_HAS_COMMON_PHYLINK ("Common Nodes must have Common Physical Link",
            "1316", Severity.ERROR),
    EXISTING_SHAREDLINK_IN_COMMONNODES ("Common Physical link between Common Nodes must be shared", "1317", Severity.ERROR),
    EAPS_ADJACENT_DOMAIN_COMMONNODE_LIMIT ("Common node limit with adjacent domain", "1318", Severity.ERROR),
    COMMON_LINKS_OVERLAPS_ON_EXISTING_PHYRING ("Common links limit on an existing physical ring", "1319", Severity.ERROR),
    COMMON_LINK_SHARED ("Common link with another Domain (different Physical Ring) must be a Shared link", "1320", Severity.ERROR),
    EAPS_DOMAIN_EXISTS ("Target Eaps Domain exists", "1321", Severity.ERROR),
    EAPS_CTRLVLAN_NAME_LENGTH ("Control VLAN name length", "1322", Severity.ERROR),
    EAPS_CTRLVLAN_NAME_CHARACTERS ("Control VLAN name characterss", "1323", Severity.ERROR),
    EAPS_CTRLVLAN_ID_RANGE ("Control VLAN ID Range", "1324", Severity.ERROR),
    EAPS_SWVER_CHECK("EAPS software compatibility check", "1325", Severity.ERROR),
    EAPS_SHAREDLINK_SWVER_CHECK("EAPS Shared-Link Switch software compatibility check", "1326", Severity.ERROR),
    EAPS_SHAREDLNK_TIMER_SWITCH_SWVER_CHECK("Switch software compatibility check", "1327", Severity.ERROR),
    EAPS_DOMAINPORT_MLAGENABLED ("Domain Ports must not be MLAG enabled ports", "1328", Severity.ERROR),
    EAPS_MASTER_NODE_NOT_CHANGED ("Master Node has not changed", "1329", Severity.ERROR),

    //Ethernet service provisioning 1600-1800
//    PROVISIONING_SERVICE_NAME_INVALID_LENGTH ("Service name should not be more than 32 characters", "1600", scripting.Severity.ERROR),
//    PROVISIONING_SERVICE_NAME_INVALID_CHARACTERS ("Service name should be alphanumeric characters", "1601", scripting.Severity.ERROR),
//    PROVISIONING_SERVICE_NAME_EXISTS ("Service name should be unique in the system", "1602", scripting.Severity.ERROR),
//    PROVISIONING_SERVICE_ELINE_UNI_INVALID ("E-Line service should have only 2 UNI port", "1603", scripting.Severity.ERROR),
//    PROVISIONING_SERVICE_ELAN_UNI_INVALID ("E-LAN service should have atleast 2 UNI ports and not exceed maximum UNI port limit", "1604", scripting.Severity.ERROR),
//    PROVISIONING_SERVICE_UNTAGGED_PORT ("Untagged port can be added to only one Service", "1605", scripting.Severity.ERROR),
//    PROVISIONING_SERVICE_COUNT_PER_PORT ("UNI Port can not exceed maximum Service limit", "1606", scripting.Severity.ERROR),
//    PROVISIONING_SERVICE_DUPLICATE_PORT("UNI Port is already associated to the service", "1607", scripting.Severity.INFO),
//    PROVISIONING_SERVICE_INVALID_CUSTOMER("Invalid customer name", "1608", scripting.Severity.INFO),
//    PROVISIONING_SERVICE_BANDWIDTH_DELETE_ERROR("Can not delete a Bandwidth profile that is attached to one or more Services", "1609", scripting.Severity.ERROR),
//    PROVISIONING_SERVICE_BANDWIDTH_MODIFY_ERROR("Can not modify a Bandwidth profile that is attached to one or more Services", "1610", scripting.Severity.ERROR),
//    PROVISIONING_SERVICE_BANDWIDTH_EIR_CIR_ERROR("CIR should be less than or equal to EIR", "1611", scripting.Severity.ERROR),
//    PROVISIONING_SERVICE_BANDWIDTH_INVALID("Bandwidth parameters should not be invalid", "1612", scripting.Severity.ERROR),
//    PROVISIONING_SERVICE_DUPLICATE("E-Line/E-LAN service should be associated to only one transport service ", "1613", scripting.Severity.ERROR),
//    PROVISIONING_SERVICE_UNI_PORT_INVALID("E-Line/E-LAN service UNI port tagging should be valid", "1613", scripting.Severity.ERROR),
//    PROVISIONING_ISID_VALUE_INVALID ("ISID service value should be between 256-33,022", "1614", scripting.Severity.ERROR),
//    PROVISIONING_ISID_NAME_INVALID_LENGTH ("ISID service name should not be more than 32 characters", "1615", scripting.Severity.ERROR),
//    PROVISIONING_ISID_NAME_INVALID_CHARACTERS ("ISID service name should be alphanumeric characters", "1616", scripting.Severity.ERROR),
//    PROVISIONING_DUPLICATE_ISID_NAME("ISID service name should not be present on the selected devices", "1617", scripting.Severity.ERROR),
//    PROVISIONING_DUPLICATE_ISID_VALUE("ISID service value should not be present on the selected devices", "1618", scripting.Severity.ERROR),
//    PROVISIONING_DUPLICATE_ISID_NAME_VALUE("ISID service value should not be present on the selected devices", "1619", scripting.Severity.ERROR),
//    PROVISIONING_ENABLE_DISABLE_ERROR("Service can be enabled or disable only if it is protected by EAPS domain", "1620", scripting.Severity.ERROR),
//    PROVISIONING_PRECONDITION_ERROR("Internal error while preparing provisioning script request", "1621", scripting.Severity.ERROR),

    //    VMM_CONNECTION_ALREADY_EXISTS("VMM Connection already exists", "1700", scripting.Severity.ERROR),
//    VMM_VMTRACKING_PORTGROUP_NOT_EXIST("Internal Error: VM Tracking port group not found.", "1701", scripting.Severity.ERROR),
//    VMM_VMTRACKING_PORT_ALREADY_ADDED("One or more ports are already added as VM Tracking port", "1702", scripting.Severity.INFO),
//    VMM_VMTRACKING_PORT_NOT_ENABLE("One or more ports for deletion are not VM Tracking enabled", "1703", scripting.Severity.INFO),
//    VMM_VMTRACKING_NO_CHANGE("The request do not have any changes", "1704", scripting.Severity.ERROR),
//    VMM_VMTRACKING_RADIUS_DOWN("Ridgeline VM Mobility RADIUS server is not running. Please restart the RADIUS server and retry.", "1705", scripting.Severity.ERROR),
//    VMM_VMTRACKING_FTP_CONFIG_ERROR("Ridgeline VM Mobility FTP server configuration is not valid.", "1706", scripting.Severity.ERROR),
//    VMM_VMTRACKING_RADIUS_STATUS("Ridgeline VM Mobility RADIUS server status.", "1707", scripting.Severity.INFO),
//    VMM_VMTRACKING_FTP_SERVER_CONFIG_STATUS("Ridgeline VM Mobility FTP Server configuration.", "1708", scripting.Severity.INFO),
//    VMM_VMTRACKING_RADIUS_FTP_SYNC("Radius database and FTP repository updated..", "1709", scripting.Severity.INFO),
//    VMM_VMTRACKING_SWITCH_SYNC("vm-tracking switches notifies to download new vm-policy mappings", "1710", scripting.Severity.INFO),
//    VMM_VMTRACKING_FTP_POLICY_SYNC("Policy files synced with updated vm-policy map", "1711", scripting.Severity.INFO),
//    VMM_VMTRACKING_SERVER_DB_UPDATE("Server database updated with new VM-to-VPP associations.", "1712", scripting.Severity.INFO),
//    VMM_VMTRACKING_RADIUS_RESTART("Restarting Radius server..", "1713", scripting.Severity.INFO),
//    VMM_VPP_MODIFIED_NOCHANGE("Virtual-port profile updated. No change in policy associations to VMs.", "1714", scripting.Severity.INFO),
//    VMM_VMTRACKING_SWITCH_REAUTH("Issuing reauth on affected virtual machines.", "1715", scripting.Severity.INFO),
    PARTIAL_ERROR ("", "1800", Severity.ERROR),
    SKIPPED ("Skipped", "1801", Severity.ERROR),
    NOT_EXECUTED("Not Executed","1802",Severity.INFO),


    //    IDENTITY_ROLE_ALREADY_EXISTS("Role uniqueness check", "1900", scripting.Severity.ERROR),
//    IDENTITY_ROLE_MAX_LIMIT_EXCEEDED("Maximum allowed role count exceeded", "1901", scripting.Severity.ERROR),
//    IDENTITY_CHILD_ROLE_MAX_LIMIT_EXCEEDED("Maximum allowed child role count (8) exceeded", "1902", scripting.Severity.ERROR),
//    IDENTITY_PARENT_CIHLD_CIRCULAR_REFERENCE("Parent/child circular reference check", "1903", scripting.Severity.ERROR),
//    IDENTITY_ROLEMGMT_SWVER_CHECK("Identity Role Management is supported from XOS version 12.5 and above", "1904", scripting.Severity.ERROR),
//    IDENTITY_ROLE_DOES_NOT_EXISTS("Role availability check", "1905", scripting.Severity.ERROR),
//    IDENTITY_HIRARCHY_LEVEL_LIMIT_EXCEEDED("Maximum allowed role hierarchy lineage count (5) exceeded", "1906", scripting.Severity.ERROR),
//    IDENTITY_QUALIFIER_NOT_SET("ACL source address type not configured", "1907", scripting.Severity.ERROR),
//    IDENTITY_QUALIFIER_MISMATCH("ACL source address type is not compatible", "1908", scripting.Severity.ERROR),
//    IDENTITY_POLICY_MISMATCH("Incompatible identity management policies are found", "1909", scripting.Severity.ERROR),
//    IDENTITY_VERSION_CAPABILITY_MISMATCH_ENABLE_RBAC("You have configured features that require EXOS 12.7. \n " +
//            "Only switches running EXOS version 12.7 and above can be managed for role-based access control", "1910", scripting.Severity.ERROR),
//    IDENTITY_VERSION_CAPABILITY_MISMATCH_CREATE_ROLE("You have enabled role-based access control on one or more switches running EXOS version below 12.7 \n, " +
//                            "Some of the attributes in the match criteria requires EXOS version 12.7", "1911", scripting.Severity.ERROR),
//    IDENTITY_VERSION_CAPABILITY_MISMATCH_WHITE_LIST("You have enabled role-based access control on one or more switches running EXOS version below 12.7 \n, " +
//                                            "This feature requires EXOS version 12.7", "1912", scripting.Severity.ERROR),
//    //Directory server validation results type constants
//    DS_VALIDATION_IN_PROGRESS("Verifying", "1907", scripting.Severity.INFO),
//    DS_VALIDATION_SUCCESSFUL("Successful", "1908", scripting.Severity.INFO),
//    DS_VALIDATION_ERROR("Error", "1909", scripting.Severity.ERROR),
//    //License
//    FEATURE_NODELIMIT_CHECK("Feature Node Limit Check", "2000", scripting.Severity.ERROR),
//
//    //EAPS Verification - Data retrieve error constants
//    DEVICEPORTVLAN_READ_FAILED("Error while reading DevicePortVlan data from VLAN", "3001", scripting.Severity.ERROR),
//
//    GROUP_SETTING_ERROR("Group settings not correctly defined", "2001", scripting.Severity.ERROR),
//    GROUP_SETTING_NO_CHANGE("No change in group settings", "2002", scripting.Severity.ERROR),
//    GROUP_SETTING_UPDATION("Group settings updation in database", "2003", scripting.Severity.INFO),
//
//    IDM_ROLE_RFSH_USER_INACTIVE("Selected user is no longer active.", "2101", scripting.Severity.ERROR),
//    IDM_ROLE_RFSH_USERS_INACTIVE("Selected users are no longer active.", "2102", scripting.Severity.ERROR),
//    IDM_ROLE_RFSH_ROLE_USERS_INACTIVE("Selected role has no active users.", "2103", scripting.Severity.ERROR),
//    IDM_ROLE_RFSH_ROLES_USERS_INACTIVE("Selected roles have no active users.", "2104", scripting.Severity.ERROR),
//    IDM_ROLE_RFSH_NO_ACTIVE_USERS("There are no active users.", "2105", scripting.Severity.ERROR),
//
//    IDM_ROLE_RFSH_USER_RBAC_ERROR("Only active users from role-based access control enabled devices\n" +
//                    "can be refreshed.", "2106", scripting.Severity.ERROR),
//    IDM_ROLE_RFSH_RBAC_ERROR("Only active users from role-based access control enabled devices\n" +
//                    "can be refreshed. No such active users found.", "2107", scripting.Severity.ERROR),
//
//    IDM_ROLE_RFSH_VERSION_ERROR("Only active users from devices running EXOS version 12.7 and above can be\n" +
//                    "refreshed.", "2108", scripting.Severity.ERROR),
//    IDM_ROLE_RFSH_ROLE_VERSION_ERROR("Only active users of roles from devices running EXOS version 12.7 and above can be\n" +
//                    "refreshed.  No such active users found.", "2109", scripting.Severity.ERROR),
//    IDM_ROLE_RFSH_ALL_VERSION_ERROR("Only active users from devices running EXOS version 12.7  and above can be\n" +
//                    "refreshed. No such active users found.", "2110", scripting.Severity.ERROR),
//
//    IDM_ROLE_RFSH_USER_RBAC_AND_VERSION_ERROR("Only active users from role-based access control enabled devices\n" +
//                    "running EXOS version 12.7 and above can be refreshed.", "2111", scripting.Severity.ERROR),
//    IDM_ROLE_RFSH_RBAC_AND_VERSION_ERROR("Only active users from role-based access control enabled devices\n" +
//                    "running EXOS version 12.7 and above can be refreshed.\n" +
//                    "No such active users found.", "2112", scripting.Severity.ERROR),
//
//    IDM_ROLE_RFSH_PARTIAL_SUCCESS("Only active users from role-based access control enabled devices\n" +
//                    "running EXOS version 12.7 and above can be refreshed.\n" +
//                    "Please check audit log for details.", "2113", scripting.Severity.WARNING),
//    IDM_ROLE_RFSH_INACTIVE_USERS_PARTIAL_SUCCESS("Some of the selected users are no longer active", "2114", scripting.Severity.WARNING),
//
//    INVENTORY_PROVISIONING_INVALID_IP_ADDRESS("Invalid IP address","2201",scripting.Severity.ERROR),
//    INVENTORY_PROVISIONING_DEVICE_ALREADY_MANAGED("Device is already managed","2202", scripting.Severity.ERROR),
//    INVENTORY_PROVISIONING_NO_SSH_LICENSE("Server does not have SSH license","2203", scripting.Severity.ERROR),
//    INVENTORY_PROVISIONING_DATA_COLLECTION_STARTED("Starting Inventory data collection","2204",scripting.Severity.ERROR),
//    INVENTORY_UNKNOWN_AP("Unable to determine sysOID: Possible Reason, Either Detected AP is Controller's in-built AP or Missing sysOID<->model-number entries in NetconfDeviceSysOIDMap.xml", "2205", scripting.Severity.WARNING),
//
//    // RL Admin error. Error range 4000 - 4200
//    RL_ADMIN_BAD_CREDENTIALS_ERROR("Bad credentials provided", "4000", scripting.Severity.ERROR),
//    RL_ADMIN_TIMEOUT_ERROR("Timeout error", "4001", scripting.Severity.ERROR),
//    RL_ADMIN_RADIUS_ROLE("Radis sever return no role, or not-exist role", "4002", scripting.Severity.ERROR),
//    RL_ADMIN_SERVER_PROPERTIES_UNAVAILABLE("Unable to obtain server properties", "4003", scripting.Severity.ERROR),
//    RL_ADMIN_INVALID_PROPERTY_VALUE("Invalid property value provided", "4004", scripting.Severity.ERROR),
    RL_ADMIN_PROPERTY_NOT_PRESENT("The property with the provided name is not present", "4005", Severity.ERROR),
    //    RL_ADMIN_NO_USER_BY_MEK("Cannot find a user with the provided key", "4006", scripting.Severity.ERROR),
//    RL_ADMIN_NO_USER_BY_NAME("Cannot find a user with the provided name", "4007", scripting.Severity.ERROR),
//    RL_ADMIN_NO_ROLE_BY_MEK("Cannot find a role with the provided key", "4008", scripting.Severity.ERROR),
//    RL_ADMIN_NO_ROLE_BY_NAME("Cannot find a role with the provided name", "4009", scripting.Severity.ERROR),
//    RL_ADMIN_RESET_ADMIN_PASSWORD("Failed to reset password for 'admin' user", "4010", scripting.Severity.ERROR),
    RL_ADMIN_INIT_PROPERTIES_METADATA("Failed to initialize the properties-metadata configuration file", "4011", Severity.ERROR),
    //    RL_ADMIN_USER_ALREADY_EXISTS("User with the same name already exists","4012",scripting.Severity.ERROR),
//    RL_ADMIN_ROLE_ALREADY_EXISTS("Role with the same name already exists","4013",scripting.Severity.ERROR),
//    RL_ADMIN_CANNOT_MODIFY_NON_MODIFIABLE_ROLE("cannot modify non modifiable role","4014",scripting.Severity.ERROR),
//    RL_ADMIN_USER_WITH_THE_SAME_ROLE_EXISTS("This role is assigned to at least one user","4013",scripting.Severity.ERROR),
    REST_ROW_LIMIT("Number of return rows exceeds the configured limit","5000",Severity.ERROR),
    //
//    ALARM_CATEGORY_NOT_FOUND("Specified alarm category is not defined", "6000", scripting.Severity.ERROR),
//    ALARM_PROFILE_NOT_FOUND("Specified profile is not defined", "6001", scripting.Severity.ERROR),
//    ALARM_RAISING_EVENT_NOT_FOUND("Specified raising event is not defined", "6002", scripting.Severity.ERROR),
//    ALARM_CLEARING_EVENT_NOT_FOUND("Specified clearing event is not defined", "6003", scripting.Severity.ERROR),
//    ALARM_ALARM_NAME_DUPLICATE("Alarm with the same name is already defined", "6004", scripting.Severity.ERROR),
//    ALARM_RMON_NAME_DUPLICATE("Rmon with the same name is already defined", "6004", scripting.Severity.ERROR),
//    ALARM_SAME_EVENT_FOR_ALARMS("Some other alarm definition also has the same raising/clearing condition", "6005", scripting.Severity.ERROR),
//    ALARM_PROFILE_NAME_DUPLICATE("Profile with the same name is already defined", "6006", scripting.Severity.ERROR),
//    ALARM_CATEGORY_NAME_DUPLICATE("Category with the same name is already defined", "6007", scripting.Severity.ERROR),
//    ALARM_PROFILE_IS_USED("Profile is assigned to event or alarm", "6008", scripting.Severity.ERROR),
//    ALARM_CATEGORY_IS_USED("Category is assigned to alarm", "6009", scripting.Severity.ERROR),
//    ALARM_DEFINITION_NOT_FOUND("Specified alarm definition is not defined", "6010", scripting.Severity.ERROR),
//    EVENT_DEFINITION_NOT_FOUND("Specified event definition is not defined", "6011", scripting.Severity.ERROR),
//    ALARM_ANOTHER_OBJECT_VERSION("Specified object was already modified by another user", "6012", scripting.Severity.ERROR),
//    ALARM_DEFINITION_HAS_LINKED_ALARMS("Specified alarm definition has linked alarms", "6013", scripting.Severity.ERROR),
//    ALARM_BULK_REQUEST_FAILED("Errors occured during request execution:", "6014", scripting.Severity.ERROR),
//
//
//    FILTER_TYPE_NOT_SUPPORTED("Filter type not supported","7000",scripting.Severity.ERROR),
//    FILTER_PARSE_ERROR("Error while parsing filter value","7001",scripting.Severity.ERROR),
//
    REQUIRED ("Required", "8000", Severity.INFO),
    NOT_REQUIRED ("Not Required","8001", Severity.INFO),
    REQUEST_CANCELLED ("Request execution cancelled","8002", Severity.INFO),

    ;


    private final String displayString;
    private final String verboseDisplayString;
    private final String statusCode;
    private final Severity severity;
    private final ErrorType errorType;

    StatusType(String displayString, String statusCode, Severity severity)
    {
        this(displayString, statusCode, severity, ErrorType.APPLICATION);
    }
    StatusType(String displayString, String statusCode, Severity severity, ErrorType errorType)
    {
        this.displayString = displayString;
        this.verboseDisplayString = displayString;
        this.statusCode = statusCode;
        this.severity = severity;
        this.errorType = errorType;
    }

    public String getStatusCode()
    {
        return statusCode;
    }

    public Severity getSeverity()
    {
        return severity;
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

    /**
     * @return Returns the errorType.
     */
    public ErrorType getErrorType()
    {
        return this.errorType;
    }


    public static StatusType getStatusType(StatusCode status)
    {
        switch (status)
        {
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
}
