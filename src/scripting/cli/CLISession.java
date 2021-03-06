package scripting.cli;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;
import org.apache.commons.net.telnet.TelnetClient;
import util.JAXBLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lmarrero on 4/27/2016.
 */
public class CLISession {

    private static int SO_TIMEOUT = 5000;
    private static int RETRIES = 3;
    private static int BUF_SIZE = 1024;// 8192;

    private Logger _logger = Logger.getLogger(this.getClass().getName());

    private String shellPrompt;

    private CLIRule cliRule;

    private String userName;
    private String password;

    private String host;
    private int port;
    private boolean SSHEnabled;

    private boolean connected;
    private String lastErrorMessage;


    // telnet based on apache net
    private TelnetClient tc = null;

    // ssh trilead ssh2
    private String _name;
    private Connection _connection = null;
    private Session _session = null;


    private CLISession() {

    }

    public static CLISession newInstance(String ruleName) {
        CLISession session = new CLISession();

        try {
            CLIRule cliRule = JAXBLoader.getCLIRule(ruleName);
            session.setCliRule(cliRule);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return session;
    }

    public static void main(String[] args) {
        CLISession session = CLISession.newInstance("enterasys");
        try {

            String login = "admin";
            String passwd = "n7830466";
            String host = "10.54.147.249";
            int port = 23;
            boolean isSSH = false;
            session.setUserName(login);
            session.setPassword(passwd);
            session.setHost(host);
            session.setPort(port);
            session.setSSHEnabled(isSSH);

            System.out.println(session.getCliRule());
            //System.out.println(session.cli("show system", new QuestionPrompt("to quit:", " ")));
            //cliSession.testXOSExpect();
//            cliSession.ssh("10.56.0.10",22,"engineer","engineer");
//            cliSession.setShellPrompt("->");
//            System.out.println(cliSession.cli("show version"));
//            System.out.println(cliSession.cli("show system"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isSSHEnabled() {
        return SSHEnabled;
    }

    public void setSSHEnabled(boolean SSHEnabled) {
        this.SSHEnabled = SSHEnabled;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }


    public String getShellPrompt() {
        return shellPrompt;
    }

    public boolean connect() {
        if (isConnected()) {
            return true;
        }
        try {
            if (isSSHEnabled()) {
                connect_ssh();
            } else {
                connect_telnet();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isConnected();
    }

    public CLIRule getCliRule() {
        return cliRule;
    }

    public void setCliRule(CLIRule cliRule) {
        this.cliRule = cliRule;
        this.shellPrompt = cliRule.getShellPrompt();
    }

    protected void connect_ssh() {
        System.out.println(String.format("ssh to %s:%d", host, 23));
        if (_connection != null)
            throw new IllegalStateException("already connected");

        _connection =
                (port == 0) ? new Connection(host) : new Connection(host, 22);
        _name = _connection.getHostname() + ":" + _connection.getPort();

        try {
            adjustOptions();

            _connection.connect();

            boolean passwordUsed = authSSH();
            boolean userPromptExpected = false;
            boolean passwordPromptExpected = !passwordUsed;

            _logger.log(Level.FINE, _name + " connected");

            ExpectCmd cmd = new ExpectCmd(null, shellPrompt);
            cmd.record = false;
            cli(cmd);
        } catch (Exception ex) {
            _logger.info(_name + " communication error while connecting.");
            // add more causes as you learn them.
            _logger.info(" SSH port may not be available");
            _logger.log(Level.FINE, _name + " communication error while connecting", ex);

            _connection = null;

            //throw new Exception("communication error while connecting: "+ ex);
        }
    }

    public void setShellPrompt(String prompt) {
        shellPrompt = prompt;
    }

    protected boolean authTelnet() {
        // telnet authentication
        List<ExpectCmd> expectList = new ArrayList<ExpectCmd>();
        expectList.add(new ExpectCmd(null, cliRule.getLoginPrompt(), userName));
        expectList.add(new ExpectCmd(null, cliRule.getPasswordPrompt(), password));
        // TODO process errors
        String results = cli(expectList);

        return true;
    }

    /**
     * Authenticate the connection.
     *
     * @throw ShellAuthenticationFailure - loginid/password not recognized
     * @throw ShellCommunicationException - there was a communication error.
     * @throw IllegalStateException - called at wrong time
     * @throw UnsupportedOperationException - the connection does not support explicit authentication
     */
    protected boolean authSSH()
            throws Exception {
        if (_connection == null)
            throw new IllegalStateException("not connected");
        if (_session != null)
            throw new IllegalStateException("already authenticated");

        // First, SSH authentication.
        //
        boolean ok = false;
        boolean passwordUsed = false;

        try {
            // We have to check which kinds of authentication are supported.
            // Most devices support password and public key. But X-series
            // for some reason supports none - this mean it prompts for
            // username and password during the session.
            //
            String authMethods[] = _connection.getRemainingAuthMethods(userName);

            if (_logger.isLoggable(Level.FINE)) {
                if (authMethods == null)
                    _logger.log(Level.FINE, _name + " auth methods: null");
                else if (authMethods.length == 0)
                    _logger.log(Level.FINE, _name + " auth methods: empty set");
                else {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(_name).append(" auth methods: ");
                    for (String method : authMethods)
                        buffer.append(method).append(", ");
                    _logger.log(Level.FINE, buffer.toString());
                }
            }

            boolean canUsePassword = false;
            //boolean canUsePublicKey = false;

            for (String method : authMethods) {
                if ("password".equals(method))
                    canUsePassword = true;
                //if ("publickey".equals(method))
                //    canUsePublicKey = true;
            }

            if (canUsePassword) {
                ok = _connection.authenticateWithPassword(userName, password);
                passwordUsed = true;
            } else if (authMethods.length == 0)
                ok = _connection.authenticateWithNone(userName);

            if (ok)
                _logger.log(Level.FINE, _name + " authenticated");
        } catch (Exception ex) {
            _logger.log(Level.FINE, _name + " communication error while authenticating", ex);

            _connection.close();

            throw new Exception("communication error while authenticating", ex);
        }

        if (!ok) {
            _logger.log(Level.FINE, _name + " authentication failure");

            _connection.close();
            _connection = null;

            throw new Exception("");
        }

        // If authentication succeeds, set up the shell.
        //
        // Some devices demand a PTY requested (N-Series). Some devices fail if you
        // request one (XSR).
        // So we try it, and if it fails, we ignore the error and continue.
        //
        try {
            _session = _connection.openSession();

            try {
                _session.requestDumbPTY();
            } catch (IOException ex) {
                _logger.log(Level.FINE, _name + " server refused to allocate a PTY");
            }

            _session.startShell();

            _logger.log(Level.FINE, _name + " shell started");
        } catch (Exception ex) {
            _logger.log(Level.FINE, _name + " communication error while starting shell", ex);

            if (_session != null)
                _session.close();
            _connection.close();
            _session = null;
            _connection = null;

            throw new Exception("communication error while starting shell", ex);
        }

        return passwordUsed;
    }

    protected void connect_telnet() throws IOException {
        System.out.println(String.format("telnet to %s:%d", host, port));
        tc = new TelnetClient();
        tc.connect(host, port);

        // TODO: add support for banner
        authTelnet();
    }

    public InputStream getInputStream() {
        return tc != null ? tc.getInputStream() : _session.getStdout();
    }

    public OutputStream getOutputStream() {
        return tc != null ? tc.getOutputStream() : _session.getStdin();
    }

    public String cli(String cmd) {
        ExpectCmd expectCmd = new ExpectCmd(cmd);
        expectCmd.record = true;
        expectCmd.addPattern(shellPrompt, null, false); // required
        return cli(expectCmd);
    }

    public String cli(String cmd, QuestionPrompt questionPrompt) {
        List<QuestionPrompt> questionPrompts = new ArrayList<>();
        questionPrompts.add(questionPrompt);
        return cli(cmd, questionPrompts);
    }

    public String cli(String cmd, List<QuestionPrompt> qprompts) {
        ExpectCmd expectCmd = new ExpectCmd(cmd);
        expectCmd.record = true;
        expectCmd.addPattern(shellPrompt, null, false); // required

        for (QuestionPrompt questionPrompt : qprompts) {
            expectCmd.addPattern(questionPrompt.prompt, questionPrompt.reply, true);
        }
        expectCmd.addPattern(shellPrompt, null, false);
        return cli(expectCmd);
    }

    public String cli(ExpectCmd cmd) {
        // telnet authentication
        List<ExpectCmd> expectList = new ArrayList<ExpectCmd>();
        expectList.add(cmd);
        return cli(expectList);
    }

    public void testXOSExpect() {
        // telnet authentication
        List<ExpectCmd> expectList = new ArrayList<ExpectCmd>();

        ExpectCmd cmd = new ExpectCmd("show system");
        cmd.record = true;
        cmd.addPattern("to quit:", " ", true);
        cmd.addPattern(" #$", null, false);
        expectList.add(cmd);

        // TODO:
//        Do you want to save configuration changes to currently selected configuration
//        file (primary.cfg) and reboot?
//        (y - save and reboot, n - reboot without save, <cr> - cancel command)

        cli(expectList);
    }

    public void testEOSExpect() {
        List<ExpectCmd> expectList = new ArrayList<ExpectCmd>();
        ExpectCmd cmd = new ExpectCmd(null, "->");
        cmd.record = false;
        expectList.add(cmd);

        cmd = new ExpectCmd("show system");
        cmd.record = true;
        cmd.addPattern("->", null, false);
        expectList.add(cmd);

        // TODO:
//        Do you want to save configuration changes to currently selected configuration
//        file (primary.cfg) and reboot?
//        (y - save and reboot, n - reboot without save, <cr> - cancel command)

        System.out.println(cli(expectList));
    }

    public String cli(List<ExpectCmd> expects) {

        StringBuilder stringBuilder = new StringBuilder();
        for (ExpectCmd info : expects) {
            //System.out.println("Processing: "+info);
            Response resp = expect(info);
            if (resp.output != null)
                stringBuilder.append(resp.output);
            if (resp.err != null) {
                System.out.println("RUN ERROR: " + resp.err);
                break;
            }
        }
        return stringBuilder.toString();
    }

    protected boolean sendCommand(String cmd) {
        boolean sent = false;
        try {
            //  System.out.println("Sending: "+cmd);
            OutputStream outstr = getOutputStream();
            outstr.write(cmd.getBytes(), 0, cmd.length());
            outstr.flush();
            sent = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sent;
    }

    protected boolean find(PatternEntry patternEntry, String promptTest) {
        boolean matched = false;
        // System.out.print(" TEST: "+promptTest+"...\n");
        Matcher m = patternEntry.pattern.matcher(promptTest);
        if (m.find()) {
            matched = true;
        }
        return matched;
    }

    private Response expect(ExpectCmd expectInfo) {

        Response response = new Response();
        InputStream instr = getInputStream();
        StringBuilder outputBuilder = new StringBuilder();
        boolean done = false;

        try {

            byte[] buff = new byte[BUF_SIZE];
            int ret_read = 0;
            int lastEolIdx = -1;
            int lineCount = 0;


            // send command (if any)
            if (expectInfo.command != null) {
                if (!sendCommand(expectInfo.command)) {
                    response.err = "Failed to send command: " + expectInfo.command;
                }
            }

            do {
                ret_read = instr.read(buff);
                if (ret_read > 0) {
                    char lastCh = (char) buff[ret_read - 1];

                    String str = new String(buff, 0, ret_read);

                    // get rid of all the ansi escape codes
                    //str =  str.replaceAll("\u001B\\[[;\\d]*m", "");
                    str = str.replaceAll("\\e\\[[\\d;]*[^\\d;]", "");

                    // keep track of the last new line
                    int eolIdx = str.lastIndexOf('\n');
                    int len = outputBuilder.length();
                    if (eolIdx >= 0) {
                        lastEolIdx = len + eolIdx;
                        lineCount++;
                    }

                    outputBuilder.append(str);

                    if (lastCh != '\n' && lastEolIdx >= 0) {
                        String testStr = outputBuilder.substring(lastEolIdx + 1).trim();
                        if (!testStr.isEmpty()) {
                            //System.out.println("TESTING: "+testStr);
                            for (PatternEntry patternEntry : expectInfo.patterns) {
                                if (patternEntry.pattern.matcher(testStr).find()) {
                                    if (expectInfo.record) {
                                        outputBuilder.delete(lastEolIdx + 1, outputBuilder.length());
                                    } else {
                                        outputBuilder.delete(0, outputBuilder.length());
                                    }

                                    //  System.out.println("--->"+testStr+"<---("+patternEntry.pattern.toString()+")");
                                    // clear the prompt
                                    if (patternEntry.reply != null) {
                                        if (!sendCommand(patternEntry.reply)) {
                                            response.err = "Failed to auto-reply to more prompt: " + testStr;
                                        }
                                    }
                                    if (!patternEntry.hasMore) {
                                        done = true;
                                    }
                                    break;
                                } else {
                                    //  System.out.println("no match");
                                }
                            }
                        }
                    }
                    // System.out.println(str);
                }
            }
            while (!done && ret_read >= 0);
        } catch (Exception e) {
            e.printStackTrace();
            response.err = "IOException: " + e.getMessage();
        } finally {
            // System.out.println("DONE Expect!");
            //   System.out.println(outputBuilder.toString());
            if (!done) {
                response.err = "Failed processing command: ";
            }
            response.output = outputBuilder.toString();
        }
        return response;
    }

    public void close() {
        try {
            if (tc != null) {
                tc.disconnect();
            }

        } catch (IOException e) {
        } finally {
            tc = null;
        }

        if (_session != null)
            _session.close();
        if (_connection != null)
            _connection.close();
        _session = null;
        _connection = null;
    }

    // Adjust any options on the connection.
    //
    private void adjustOptions() {
        // This is here because we have one SSH server implementation, FreSSH on
        // SecureStack switches, which, using the trilead defaults, will negotiate
        // to use hmac-sha1-96 ... and then subsequently lose the connection trying
        // to use it. So we take that option off of our plate.

        // Get the supported MAC options, and remove some of them.
        //
        String[] defaultMacs = Connection.getAvailableMACs();

        Vector<String> defaultMacList = new Vector<String>();
        for (String mac : defaultMacs)
            defaultMacList.add(mac);

        defaultMacList.remove("hmac-sha1-96");
        defaultMacList.remove("hmac-md5-96");

        String[] supportedMacs = defaultMacList.toArray(new String[]{});
        _connection.setClient2ServerMACs(supportedMacs);
        _connection.setServer2ClientMACs(supportedMacs);
    }

    public static class QuestionPrompt {
        String prompt;
        String reply;

        public QuestionPrompt(String p, String r) {
            prompt = p;
            reply = r;
        }
    }

    static class Response {
        String err;
        String lastPrompt;
        PatternEntry patternEntry;
        String output;

        public boolean hasMore() {
            return (err == null && patternEntry != null && patternEntry.hasMore);
        }

        public PatternEntry next() {
            return patternEntry;
        }
    }

    static class PatternEntry {
        Pattern pattern;
        String reply;
        boolean hasMore = false;

        public PatternEntry(String expect, String reply, boolean more) {
            pattern = Pattern.compile(expect);
            if (reply != null && !reply.endsWith("\n"))
                reply += "\n";
            this.reply = reply;
            hasMore = more;
        }
    }

    static class ExpectCmd {
        String command;
        int maxPageCount = 1;
        int pageCount = 0;
        List<PatternEntry> patterns;
        boolean record;

        public ExpectCmd(String cmd) {
            this(cmd, null, null, false);
        }

        public ExpectCmd(String cmd, String expect) {
            this(cmd, expect, null, false);
        }

        public ExpectCmd(String cmd, String expect, String reply) {
            this(cmd, expect, reply, false);
        }


        public ExpectCmd(String cmd, String expect, String reply, boolean hasMore) {
            if (cmd != null && !cmd.endsWith("\n"))
                cmd += "\n";
            command = cmd;
            patterns = new ArrayList<>();
            if (expect != null) {
                addPattern(expect, reply, hasMore);
            }
        }

        public void addPattern(String pattern) {
            addPattern(pattern, null, false);
        }

        public void addPattern(String pattern, String reply) {
            addPattern(pattern, reply, false);
        }

        public void addPattern(String pattern, String reply, boolean hasMore) {

            if (pattern != null) {
                patterns.add(new PatternEntry(pattern, reply, hasMore));
            }

        }

        public List<PatternEntry> getPatterns() {
            return patterns;
        }
    }
}
