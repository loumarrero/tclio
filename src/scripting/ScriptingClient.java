package scripting;


import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * {@link ScriptingClient} is a singleton that supports executing system scripts (tcl based)
 * to devices over telnet/ssh. The implementation leverages the legacy scripting APIs while exposing a
 * simpler and a more concise way of processing script request/responses asynchronously.
 * <p>
 * To use the {@code ScriptingClient}, the following preconditions must be done:
 * <p>
 * <pre>
 *     1. Create a TCL script in the following directory:
 *
 *          common/etc/stage/server/appdata/scripting/bundled_scripts/xml
 *
 *        The script has to be embedded in an xml file that conforms to the following
 *        syntax rules defined in the following files (also in the same path)
 *              Section definition.xml
 *              Parameter definition.xml
 *              Conditional statement.xml
 *              Handle Prompts.xml
 *              Macro as script.xml
 *        NOTES: use an existing xml file as a starting point (e.g,Config_Syslog.xml).
 *        Scripts with <scriptOwner/> set to system will be hidden from the OneView UI. However,
 *        during script testing it is recommended that the scriptOwner field be removed to allow
 *        the script to show under the Scripts menu. Once the script works set the scriptOwever element
 *        back to system.
 *
 *     2. Add the name of the script file to the master list the scripting system uses to keep the DB in sync:
 *
 *         common/etc/state/server/appdata/scripting/bundled_scripts/updated_scripts.list
 *
 *     3. Create a {@code ScriptContext(String name)}
 *
 *         the name should match the in the script xml file (e.g., <script name="Config_Syslog">)
 *         add values for variables referenced in the script when the device is added
 *         via {@code ScriptContext.addDevice(id,params)}.
 *
 *     4. Create a {@link ScriptListener} to handle async responses
 * </pre>
 * <p>
 * Example:
 * <p>
 * <pre>{@code
 *
 *      ScriptContext request = new ScriptContext("Config_Syslog");
 *      Map<String,String> params = new HashMap<>();
 *      params.put("hostIP","10.10.10.10");
 *      request.addDevice(deviceID,params);
 *
 *      ScriptingClient.INSTANCE.submit(request,new ScriptListener() {
 *
 *          boolean beforeScriptRun(ScriptContext request){...}
 *
 *          void afterScriptRun(ScriptContext request,ScriptResponse response){...}
 *      });
 *
 * }
 * </pre>
 *
 * @author lmarrero
 * @since OneView 7.0
 */
public enum ScriptingClient {
    INSTANCE;

    private static final Logger logger = Logger.getLogger(ScriptingClient.class.getName());
    private ExecutorService executor;
    private ConcurrentLinkedQueue<FutureRequestTask> futures;

    ScriptingClient() {

        // compute best effort case for number of threads. however, the "best"
        // guess for max threads would be profiling the server under load. In
        // reality there is no good answer but the following formula is better
        // than guessing out of thing air.
        // threads = n_cpu * u_cpu * (1+w/c)
        //   n_cpu = number of cores available to the jvm
        //   u_cpu = target cpu utilization
        //   w/c   = ratio of wait time to compute time
        int maxThreads = (int) (Runtime.getRuntime().availableProcessors() * 0.30 * (1 + 10));
        maxThreads = Integer.getInteger("extreme.scriptingclient.maxthreads", maxThreads);

        executor = Executors.newFixedThreadPool(maxThreads,
                new WorkerThreadFactory("ScriptingClient"));

        futures = new ConcurrentLinkedQueue<FutureRequestTask>();

    }


    /**
     * Submits the given request at some time in the future. The {@code ScriptListener} will be
     * called before and after the {@code ScriptContext.call}.
     *
     * @param request        the callable task
     * @param scriptListener the callback for handling the response
     */
    public void submit(ScriptContext request, ScriptListener scriptListener) {


        // Only called as a safety net to clean up any misbehaved tasks that didn't exit cleanly.
        // in the time it specified in the request.
        purgeExpiredTasks();

        try {
            ScriptRunner scriptRunner = new ScriptRunner(request, scriptListener);

            logger.log(Level.FINE, "adding request [" + request.getScriptName() + ':' + scriptRunner.getId() + "] to executor");
            Future<ScriptResponse> ft = executor.submit(scriptRunner);
            FutureRequestTask futureRequestTask = new FutureRequestTask(scriptRunner, ft);
            futures.offer(futureRequestTask);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }


    /**
     * Submits the given request at some time in the future. The {@code ScriptListener} will be
     * called before and after the {@code ScriptContext.call}.
     *
     * @param request the callable task
     */
    public Future<ScriptResponse> submit(ScriptContext request) {

        Future<ScriptResponse> futureResponse = null;

        try {
            ScriptRunner scriptRunner = new ScriptRunner(request, null);

            logger.log(Level.FINE, "submitting request [" + request.getScriptName() + ':' + scriptRunner.getId() + "] to executor");
            futureResponse = executor.submit(scriptRunner);

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return futureResponse;
    }

    /**
     * Submits the given request at some time in the future. The {@code ScriptListener} will be
     * called before and after the {@code ScriptContext.call}.
     */
    public ScriptResponse submitAndWait(ScriptContext request, long secs) {

        ScriptResponse response = new ScriptResponse();

        ScriptRunner scriptRunner = new ScriptRunner(request, null);

        try {
            logger.log(Level.FINE, "submitting request [" + request.getScriptName() + ':' + scriptRunner.getId() + "] to executor");
            Future<ScriptResponse> ft = executor.submit(scriptRunner);
            response = ft.get(secs, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return response;
    }

    /**
     * Makes a best effort attempt to cancel a request. If the request is not running it
     * will remove it from the waiting queue. However, if the request actively
     * running it will be sent an interruption exception but will be up to the request to
     * "honor" this interruption. That is, if the request was not designed to be handle
     * interrupted request then this cancel will simply mark it as cancelled.
     * <p>
     * <p>This method does not wait for actively executing tasks to
     * terminate.
     * <p>
     * <p>There are no guarantees beyond best-effort attempts to stop
     * processing actively executing tasks.
     *
     * @param id the id for the request
     */
    public void cancel(int id) {
        FutureRequestTask foundTask = null;
        for (FutureRequestTask task : futures) {
            if (task.runner.requestID == id) {
                foundTask = task;
                break;
            }
        }

        if (foundTask != null) {
            //logger.log(Level.FINER,"removing request: "+id);
            System.out.println("removing request: " + id);
            try {
                foundTask.response.cancel(true);
            } finally {
                futures.remove(foundTask);
            }

        }
    }

    /**
     * Attempts a graceful shutdown of all actively executing requests. In addition, it will close
     * the {@code Executor} which will prevent any {@code submit} from working.
     * <p>
     * <p>This method does not wait for actively executing tasks to
     * terminate.
     * <p>
     * <p>There are no guarantees beyond best-effort attempts to stop
     * processing actively executing tasks.
     */
    public void shutdown() {
        // first graceful shutdown all tasks and notify the listener
        cancelAll();

        // then shutdown the executor
        try {
            logger.log(Level.FINER, "attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.log(Level.FINER, "tasks interrupted: " + e);
        } finally {
            if (!executor.isTerminated()) {
                logger.log(Level.FINER, "cancel non-finished tasks");
            }
            executor.shutdownNow();
            logger.log(Level.FINER, "shutdown finished");
        }
    }


    private void cancelAll() {
        for (FutureRequestTask task : futures) {
            logger.log(Level.FINER, "cancelling request: " + task.runner.getId());
            task.response.cancel(true);
        }
    }

    public void showOutstanding() {
        if (!futures.isEmpty()) {
            System.out.println("Outstanding requests:");
            for (FutureRequestTask task : futures) {
                try {
                    System.out.println("\trequest: " + task.runner.scriptContext.getScriptName());
                } catch (Exception e) {
                }
                // task.response.cancel(true);
            }
        } else {
            System.out.println("No requests outstanding");
        }

    }

    private void purgeExpiredTasks() {
        try {
            for (FutureRequestTask task : futures) {
                if (task.runner.hasExpired()) {
                    logger.log(Level.FINER, "cancelling request: " + task.runner.getId());
                    task.response.cancel(true);
                }
            }
        } catch (Exception e) {
            logger.log(Level.FINER, "exception in purgeExpiredTasks: " + e.getMessage());
        }
    }


    class FutureRequestTask {
        Future<ScriptResponse> response;
        ScriptRunner runner;

        FutureRequestTask(ScriptRunner runner, Future<ScriptResponse> response) {
            this.runner = runner;
            this.response = response;
        }

        protected void done() {
            logger.log(Level.FINER, "removing request: " + runner.requestID);
            futures.remove(this);
        }
    }

    class ScriptRunner implements Callable<ScriptResponse> {
        int requestID;
        int waitTime;
        long startTime;
        long endTime;
        ScriptContext scriptContext;
        ScriptListener externalListener;
        Boolean CONTINUE = Boolean.FALSE;
        ScriptResponse scriptResponse;

        ScriptRunner(ScriptContext context, ScriptListener cb) {
            scriptContext = context;
            requestID = context.getRequestID();
            waitTime = context.getWaitTime();
            externalListener = cb;
            scriptResponse = new ScriptResponse();
            scriptResponse.setStatusType(StatusType.ERROR);
        }

        public int getId() {
            return requestID;
        }

        public boolean hasExpired() {
            boolean expired = false;
            if (startTime > 0) {
                long etime = (endTime <= 0) ? System.currentTimeMillis() - startTime : endTime - startTime;
                expired = (etime > waitTime);
            }
            return expired;
        }

        @Override
        public ScriptResponse call() throws Exception {
            CONTINUE = Boolean.FALSE;
            try {

                if (externalListener != null && !externalListener.beforeScriptRun(scriptContext)) {

                    scriptResponse.setRequestId(requestID);
                    scriptResponse.setStatusType(StatusType.SCRIPTING_RUNSCRIPT_STOPPED_INFO);
                } else {
                    // RunScriptRequest runScriptRequest = runScriptReq.createRunScriptRequest();
                    // synchronous call to the scripting service
                    startTime = System.currentTimeMillis();
                    ;

                    scriptResponse.setResponse(scriptContext.execute());
                    CONTINUE = Boolean.TRUE;
                    scriptResponse.setStatusType(StatusType.COMPLETED);
                }

            } catch (Exception ex) {
                logger.log(Level.FINE, ex.getMessage(), ex);
            } finally {
                endTime = System.currentTimeMillis() - startTime;
                if (!CONTINUE.booleanValue()) {
                    scriptResponse = new ScriptResponse();
                    scriptResponse.setRequestId(requestID);
                    scriptResponse.setStatusType(StatusType.SCRIPTING_INTERNAL_ERROR_RUN);
                }
            }

            try {
                if (externalListener != null)
                    externalListener.afterScriptRun(scriptContext, scriptResponse);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "exception calling onComplete: " + scriptContext.getScriptName() + ", reason: " + e.getMessage());
            }

            //return CONTINUE;
            return scriptResponse;
        }
    }

}

class WorkerThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public WorkerThreadFactory(String prefix) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup()
                : Thread.currentThread().getThreadGroup();
        namePrefix = prefix + "-"
                + poolNumber.getAndIncrement()
                + "-thread-";
    }


    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}

