package scripting;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * A simple decorator for the {@code RunScriptResponse} class. The class is used by
 * the {@code ScriptingClient} and adds convenience methods to facilitate error handling, as
 * well as, processing of responses returned by the scripting service.
 *
 * @see ScriptingClient
 */
public class ScriptResponse {
    private int               requestId;
    private StatusType        statusType;
    private String response;

    /**
     * Allow {@code ScriptRespone} objects to be created with no backing {@code RunScriptResponse}
     * The purpose is to support error handling when no  {@code RunScriptResponse} is returned. In this
     * case only an error code and error message is set.
     */
    public ScriptResponse(){}
    

    public boolean isSuccessful(){
        return true;
    }

    public List<Integer> getDeviceIds() {
        return Collections.EMPTY_LIST;
    }

    public void processResponses(ScriptResponseCB cb){
//        if(runScriptResponse==null) return;
//        Map<ManagedEntityKey,RunScriptResponseStatusMessage> responses =   runScriptResponse.getRunResponses();
//        for(Map.Entry<ManagedEntityKey,RunScriptResponseStatusMessage> entry:responses.entrySet()){
//            ManagedEntityKey key = entry.getKey();
//            RunScriptResponseStatusMessage result = entry.getValue();
//            if(result.getStatusType() == StatusType.NO_ERROR){
//                cb.onSuccess(key.getId());
//            }else {
//                cb.onFailed(key.getId(),result.getStatusType(),result.getStatusType().getDisplayString());
//            }
//        }
    }
    

    public void setRequestId(int id){
        this.requestId = id;
    }


    public void setStatusType(StatusType type){
        this.statusType = type;
    }

    public void setStatusType(StatusType type,String message){
        this.statusType = type;
    }

    public StatusType getStatusType() {
        return this.statusType;
    }
    
    public void setResponse(String response){
        this.response = response;
    }
    
    public String getResponse(){
        return response;
    }

}
