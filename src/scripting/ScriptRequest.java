package scripting;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * {@code ScriptRequest} are primarily used to send script requests to the scripting service
 * via the {@code ScriptingClient.submit} method.
 *
 * To create a {@code ScriptRequest} object, the name of a preloaded script is required. This name
 * should match the name attribute for the xml element within the script.xml file. For example,
 *      {@code <script name="Config_Syslog">}
 *
 *
 * @see ScriptingClient for more details
 */
public class ScriptRequest{

    private static AtomicInteger ID = new AtomicInteger(0);
    private int                        requestID;
    private int                        waitTime;
    private String                     scriptName;
    private ScriptBean                 scriptBean;
    private Map<String,String>         defaultParams;
    private Map<Integer,ScriptEntry>   deviceScriptMap;


    /**
     *
     * @param name script name of the tcl file that has been loaded into the db
     * @throws FileNotFoundException if file does not exists
     */
    public ScriptRequest(String name){

        scriptName = name;
        scriptBean = null;//new ScriptingDAOBean().findScriptByName(scriptName);
//        if(scriptBean ==  null) {
//            throw new FileNotFoundException(scriptName);
//        }

        deviceScriptMap = new HashMap<Integer,ScriptEntry>();
        requestID = ID.incrementAndGet();
       // waitTime = scriptBean.getScriptTimeout()*1000;
    }


    public String getScriptName(){
        return scriptName;
    }


    public int getRequestID() {
        return requestID;
    }

    public int getWaitTime() {
        return waitTime;
    }

    /**
     * Adds the device id to the list of script target (s).
     *
     *
     * @param deviceID of the target device
     */
    public void addDevice(long deviceID){
        addDevice(deviceID,null);
    }

    /**
     * Adds the device to the list of script target. In addition, a map of
     * device parameters will be used to supply the script with values for
     * all variables with no value. That is, will depend on the values passed
     * in deviceParams to supply a default value. As a result, the deviceParams
     * keys should match the same name for the variable referenced in the script.
     *
     * {@code addDefaultParams} can be used to supply default data for all devices
     *
     * @param deviceID of the target device
     * @param deviceParams map of script data values
     */
    public void addDevice(long deviceID, Map<String,String> deviceParams){
        ScriptEntry scriptEntry =  deviceScriptMap.get(deviceID);
        if(scriptEntry==null){
            scriptEntry = new ScriptEntry(deviceID);
            if(defaultParams!=null){
                scriptEntry.addData(defaultParams);
            }

            deviceScriptMap.put((int)deviceID,scriptEntry);
        }

        if(deviceParams!=null){
            scriptEntry.addData(deviceParams);
        }

    }

    /**
     *
     * @return list of target devices
     */
    public List<Integer> getDeviceIds() {
        List<Integer> ids= new ArrayList<Integer>();
        ids.addAll(deviceScriptMap.keySet());
        return ids;
    }


    /**
     * Sets or adds default params that will be added to every
     * device that is added via {@code addDevice}.
     *
     * @param params map of script variables to use for every device
     */
    public void addDefaultParams(Map<String,String>  params){
        if(defaultParams==null){
            defaultParams = new HashMap<String,String>();
        }
        defaultParams.putAll(params);
    }


    static class ScriptEntry {
        long deviceID;
        Map<String,String>  dataMap;

        ScriptEntry(long deviceID){
            this.deviceID = deviceID;
            this.dataMap  = new HashMap<String,String>();
        }
        public void addData(String name,String value){
            dataMap.put(name,value);
        }

        public void addData(Map<String,String>  data){
            this.dataMap.putAll(data);
        }

        public Map<String,String> getData() {
            return dataMap;
        }
    }
}
