package scripting;


/**
 * Callback interface called by  {@code ScriptingClient} for
 * asynchronous handling of {@code ScriptRequest} and {@code ScriptResponse} objects.
 */
public interface ScriptListener {

    /**
     * Called by {@code ScriptingClient) before executing the script. This
     * allows the caller to verify/ or add  data, or halt the request.
     *
     * @param request
     * @return {@code true} {@code ScriptingClient) will execute the script
     * {@code false}  {@code ScriptingClient) will not execute the script.
     */
    boolean beforeScriptRun(ScriptRequest request);

    /**
     * Called by {@code ScriptingClient) after the executing the script request.
     *
     * @param request
     * @param response callback for the results
     */
    void afterScriptRun(ScriptRequest request, ScriptResponse response);

}
