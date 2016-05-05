package scripting;

public interface ScriptResponseCB {

    void onSuccess(int deviceId);
    void onFailed(int deviceId, StatusType type, String message);
}
