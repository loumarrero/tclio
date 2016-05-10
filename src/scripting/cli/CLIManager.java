package scripting.cli;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public enum CLIManager {
    INSTANCE;

    Map<String, SessionEntry> sessionMap;

    CLIManager() {
        sessionMap = new ConcurrentHashMap<>();
    }

    public static void main(String[] args) {
        String host = "";
        int port = 0;
        boolean isSSH = false;

    }

    public String cli(String host, String cmd) {
        return null;
    }

    public String cli(String host, String cmd, CLISession.QuestionPrompt questionPrompt) {
        return null;
    }

    public String cli(String host, String cmd, List<CLISession.QuestionPrompt> qprompts) {
        return null;
    }

    protected Map<String, String> getCLICreds() {
        String login = "";
        String password = "";
        boolean isSSH = false;
        return null;
    }

    protected void getDeviceProps() {
        // BELOW is all the properties that used to get sent as Context.REQUIRED_PROPS_KEY
        // in addition to the response file stuff (see PopulateCommPropertiesTask)

        // all interpretScriptRequest.getDeviceProperties()
        // and then appendResponse the following:

        // TODO: what is the difference between RunData and DeviceProperties in InterpretScriptRequest

//        credential.put(DEVICE_LOGIN_PROPERTY.CLI_LOGIN.getPropertyName(),
//                authCred.getUserName()!=null?authCred.getUserName():"");//can be null, return empty string
//        credential.put(DEVICE_LOGIN_PROPERTY.CLI_PASSWD.getPropertyName(),
//                authCred.getLoginPassword()!=null?authCred.getLoginPassword():"");//can be null, return empty string
//        credential.put(CliConstants.PROPERTY_IS_SSH,
//                new Boolean(authCred.getType().equalsIgnoreCase("SSH")));
//        credential.put(DEVICETYPE_PROPERTY.DEVICE_TYPE.getPropertyName(), device.getDeviceDisplayType());
//        credential.put(DEVICETYPE_PROPERTY.IS_XOS_CAPABLE.getPropertyName(), isXOS);
//        credential.put(DEVICETYPE_PROPERTY.FAMILY.getPropertyName(), propMgr.getFamilyFromOid(sysObjectID));
//        credential.put(DEVICETYPE_PROPERTY.VENDOR.getPropertyName(), vendor);
        // credential.put(DEVICETYPE_PROPERTY.SUPPORT_BANNER_ACK.getPropertyName(), isXOS);

        String serverIp = "localhost";//CoreServiceUtils.getServerIPAddress();
        String hostName = serverIp;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
        }


//        allProperties.put(SERVER_PROPERTY.IP_ADDRESS.getPropertyName(), serverIp);
//        allProperties.put(SERVER_PROPERTY.SERVER_ADDRESS.getPropertyName(), serverIp);
//        allProperties.put(SERVER_PROPERTY.HTTP_PORT.getPropertyName(), CoreServiceUtils.getWebServerPort());
//        allProperties.put(SERVER_PROPERTY.HOSTNAME.getPropertyName(),hostName);
//
//        Integer port = Integer.getInteger(ServerPropertyConstants.DEVICES_TELNETPORT, 23);
//        allProperties.put(SERVER_PROPERTY.TELNET_PORT.getPropertyName(), port);
//        port = Integer.getInteger(ServerPropertyConstants.DEVICE_SSH_PORT, 22);;
//        allProperties.put(SERVER_PROPERTY.SSH_PORT.getPropertyName(), port);
    }

    protected void getCreds() {
//        if (device != null){
//            //sysOid = device.getSysObjectId();
//            //softwareVersion = device.getFirmware();
//
//            //Get the credentials
//            Profile aProfile = null;
//            boolean runWithAdminCreds = false;
//            if (domainAndUser.equalsIgnoreCase("NetSight Server")){
//                runWithAdminCreds = true;
//            }
//            if (runWithAdminCreds){
//                UserGroup theUser = (UserGroup)AppContext.getAppCtxInstance().getProfileCache().getAdminUserGroup();
//                if(theUser!=null){
//                    aProfile = ServerContext.getAppCtxInstance().getProfileCache().getProfile(device.getId(),theUser.getGroupId().longValue());
//                }else{
//                    //Should never happen
//                    LOG.error("AdminUserGroup does not exist!");
//                }
//            }else{
//                try {
//                    UserGroup theUser;
//                    UserService userService = ServerContext.getAppCtxInstance().getUserService();
//                    String[] parts = domainAndUser.split("\\\\");
//                    if (parts.length > 1){
//                        theUser = userService.getGroupForUser(parts[1], parts[0]);
//                    }else{
//                        theUser = userService.getGroupForUser(parts[0], "");
//                    }
//                    if(theUser!=null){
//                        aProfile = ServerContext.getAppCtxInstance().getProfileCache().getProfile(device.getId(),theUser.getGroupId().longValue());
//                    }
//                } catch (Exception e) {
//                    LOG.error("Error determining user group for: "+domainAndUser, e);
//                }
//            }
//
//            if (aProfile != null){
//                AuthCred authCred = aProfile.getAuthCred();
//
//                credential.put(DEVICE_LOGIN_PROPERTY.CLI_LOGIN.getPropertyName(),
//                        authCred.getUserName()!=null?authCred.getUserName():"");//can be null, return empty string
//                credential.put(DEVICE_LOGIN_PROPERTY.CLI_PASSWD.getPropertyName(),
//                        authCred.getLoginPassword()!=null?authCred.getLoginPassword():"");//can be null, return empty string
//                credential.put(CliConstants.PROPERTY_IS_SSH,
//                        new Boolean(authCred.getType().equalsIgnoreCase("SSH")));
//            }
//        }
    }

    static class SessionEntry {
        String host;
        CLISession session;
        // cli profile (ssh/telnet,port,cli prompts,...)
    }

}
