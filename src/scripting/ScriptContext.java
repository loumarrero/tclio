package scripting;


import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * {@code ScriptContext} are primarily used to send script requests to the scripting service
 * via the {@code ScriptingClient.submit} method.
 * <p>
 * To create a {@code ScriptContext} object, the name of a preloaded script is required. This name
 * should match the name attribute for the xml element within the script.xml file. For example,
 * {@code <script name="Config_Syslog">}
 *
 * @see ScriptingClient for more details
 */
public class ScriptContext {

    private static AtomicInteger ID = new AtomicInteger(0);
    private int requestID;
    private int waitTime;
    private int timeout;
    private ScriptEngine scriptEngine;
    private String scriptName;
    private ScriptBean scriptBean;
    private Map<String, String> localVars;


    /**
     * @param bean
     * @throws FileNotFoundException if file does not exists
     */
    public ScriptContext(ScriptBean bean) {

        scriptName = bean.getName();
        this.scriptBean = bean;
        this.requestID = ID.incrementAndGet();
        // waitTime = scriptBean.getScriptTimeout()*1000;
    }


    public String getScriptName() {
        return scriptName;
    }

    public int getRequestID() {
        return requestID;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public ScriptBean getScriptBean() {
        return scriptBean;
    }

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    public void setScriptEngine(ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
    }

    public String execute() {
        scriptEngine.eval(getLocalVars());
        return scriptEngine.getResponse();
    }

    /**
     * Sets or adds default params that will be added to every
     * device that is added via {@code addDevice}.
     *
     * @param params map of script variables to use for every device
     */
    public void addLocalVars(Map<String, String> params) {
        if (localVars == null) {
            localVars = new HashMap<String, String>();
        }
        localVars.putAll(params);
    }

    public Map<String, String> getLocalVars() {
        return localVars;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    static class ScriptEntry {
        String deviceID;
        Map<String, String> dataMap;

        ScriptEntry(String deviceID) {
            this.deviceID = deviceID;
            this.dataMap = new HashMap<>();
        }

        public void addData(String name, String value) {
            dataMap.put(name, value);
        }

        public void addData(Map<String, String> data) {
            this.dataMap.putAll(data);
        }

        public Map<String, ?> getData() {
            return dataMap;
        }
    }
}
