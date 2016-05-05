package scripting;

import com.trilead.ssh2.ChannelCondition;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;
import org.apache.commons.net.telnet.TelnetClient;

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
public class CLISession{

    private static int SO_TIMEOUT = 5000;
    private static int RETRIES  = 3;
    private static int BUF_SIZE = 1024;// 8192;

    private Logger _logger = Logger.getLogger(this.getClass().getName());
    
    // telnet based on apache net
    private TelnetClient tc = null;
    
    // ssh trilead ssh2
    private String _name;
    private Connection _connection = null;
    private Session _session = null;
    private static int _waitMask =
            ChannelCondition.CLOSED
                    + ChannelCondition.EOF
                    + ChannelCondition.STDERR_DATA
                    + ChannelCondition.STDOUT_DATA
                    + ChannelCondition.EOF
                    + ChannelCondition.TIMEOUT;

    private static int _closedMask =
            ChannelCondition.CLOSED
                    + ChannelCondition.EOF
                    + ChannelCondition.EOF
                    + ChannelCondition.TIMEOUT;
    
    class Response {
        String err;
        String lastPrompt;
        PatternEntry patternEntry;
        String output;
        
        public boolean hasMore(){
            return (err==null && patternEntry!=null && patternEntry.hasMore);    
        } 
        
        public PatternEntry next() {
            return patternEntry;
        }
    }
    
    class PatternEntry {
        Pattern pattern;
        String  reply;
        boolean hasMore = false;
        
        public PatternEntry(String expect,String reply,boolean more){
            pattern = Pattern.compile(expect);
            if(reply!=null && !reply.endsWith("\n"))
                 reply += "\n";
            this.reply = reply;
            hasMore = more;
        }
    }
    
    class ExpectInfo {
        String command;
        int maxPageCount = 1;
        int pageCount = 0;
        List<PatternEntry> patterns;
        boolean record;

        public ExpectInfo(String cmd){
            this(cmd,null,null,false);
        }
        
        public ExpectInfo(String cmd,String expect){
            this(cmd,expect,null,false);
        }

        public ExpectInfo(String cmd,String expect,String reply){
            this(cmd,expect,reply,false);
        }

        
        public ExpectInfo(String cmd,String expect,String reply, boolean hasMore){
            if(cmd!=null && !cmd.endsWith("\n"))
                cmd += "\n";
            command = cmd;
            patterns = new ArrayList<>();
            if(expect!=null){
                addPattern(expect,reply,hasMore);    
            }
        }

        public void addPattern(String pattern){
            addPattern(pattern,null,false);
        }
        
        public void addPattern(String pattern,String reply){
            addPattern(pattern,reply,false);
        }

        public void addPattern(String pattern,String reply,boolean hasMore){
            
            if(pattern!=null){
                patterns.add(new PatternEntry(pattern,reply,hasMore));    
            }
            
        }
        
        public List<PatternEntry> getPatterns() {
            return patterns;
        }
    }
    
    
    public void ssh(String host,int port,String loginUser, String loginPassword){
        System.out.println(String.format("ssh to %s:%d",host,23));
        if (_connection != null)
            throw new IllegalStateException("already connected");

        _connection =
                (port == 0) ? new Connection(host) : new Connection(host, 22);
        _name = _connection.getHostname() + ":" + _connection.getPort();
        
        try
        {
            adjustOptions();

            _connection.connect();
            
            boolean passwordUsed = authSSH(loginUser, loginPassword);
            boolean userPromptExpected = false;
            boolean passwordPromptExpected = !passwordUsed;
            
            _logger.log(Level.FINE,_name + " connected");
        }
        catch (Exception ex)
        {
            _logger.info(_name + " communication error while connecting.");
            // add more causes as you learn them.
            _logger.info(" SSH port may not be available");
            _logger.log(Level.FINE,_name + " communication error while connecting", ex);

            _connection = null;

            //throw new Exception("communication error while connecting: "+ ex);
        }
    }

    public boolean authTelnet(String loginid,String password){
        // telnet authentication
        List<ExpectInfo> expectList = new ArrayList<ExpectInfo>();
        expectList.add(new ExpectInfo(null,"login:",loginid));
        expectList.add(new ExpectInfo(null,"password:",password));
        // TODO process errors
        runExpects(expectList);
        return true;
    }
    

    /**
     * Authenticate the connection. 
     *
     * @param loginid the login username (if needed)
     * @param password the login password
     *
     * @throw ShellAuthenticationFailure - loginid/password not recognized
     * @throw ShellCommunicationException - there was a communication error.
     * @throw IllegalStateException - called at wrong time
     * @throw UnsupportedOperationException - the connection does not support explicit authentication
     */
    public boolean authSSH(String loginid, String password)
            throws Exception
    {
        if (_connection == null)
            throw new IllegalStateException("not connected");
        if (_session != null)
            throw new IllegalStateException("already authenticated");

        // First, SSH authentication.
        //
        boolean ok = false;
        boolean passwordUsed = false;

        try
        {
            // We have to check which kinds of authentication are supported. 
            // Most devices support password and public key. But X-series 
            // for some reason supports none - this mean it prompts for 
            // username and password during the session.
            // 
            String authMethods[] = _connection.getRemainingAuthMethods(loginid);

            if (_logger.isLoggable(Level.FINE))
            {
                if (authMethods == null)
                    _logger.log(Level.FINE,_name + " auth methods: null");
                else if (authMethods.length == 0)
                    _logger.log(Level.FINE,_name + " auth methods: empty set");
                else
                {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(_name).append(" auth methods: ");
                    for (String method : authMethods)
                        buffer.append(method).append(", ");
                    _logger.log(Level.FINE,buffer.toString());
                }
            }

            boolean canUsePassword = false;
            //boolean canUsePublicKey = false;

            for (String method : authMethods)
            {
                if ("password".equals(method))
                    canUsePassword = true;
                //if ("publickey".equals(method))
                //    canUsePublicKey = true;
            }

            if (canUsePassword)
            {
                ok = _connection.authenticateWithPassword(loginid, password);
                passwordUsed = true;
            }

            else if (authMethods.length == 0)
                ok = _connection.authenticateWithNone(loginid);

            if (ok)
                _logger.log(Level.FINE,_name + " authenticated");
        }
        catch (Exception ex)
        {
            _logger.log(Level.FINE,_name + " communication error while authenticating", ex);

            _connection.close();

            throw new Exception("communication error while authenticating", ex);
        }

        if (!ok)
        {
            _logger.log(Level.FINE,_name + " authentication failure");

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
        try
        {
            _session = _connection.openSession();

            try
            {
                _session.requestDumbPTY();
            }
            catch(IOException ex)
            {
                _logger.log(Level.FINE,_name + " server refused to allocate a PTY");
            }

            _session.startShell();

            _logger.log(Level.FINE,_name + " shell started");
        }
        catch (Exception ex)
        {
            _logger.log(Level.FINE,_name + " communication error while starting shell", ex);

            if (_session != null)
                _session.close();
            _connection.close();
            _session = null;
            _connection = null;

            throw new Exception("communication error while starting shell", ex);
        }

        return passwordUsed;
    }



    public void telnet(String ip,String loginid,String password) throws IOException{
        System.out.println(String.format("telnet to %s:%d",ip,23));
        tc = new TelnetClient();
        tc.connect(ip, 23);

        // TODO: add support for banner
        authTelnet(loginid,password);
    }
    
    public InputStream getInputStream() {
        return  tc !=null ? tc.getInputStream() : _session.getStdout();
    }
    
    public OutputStream getOutputStream() {
        return  tc !=null ? tc.getOutputStream() : _session.getStdin();
    }
    
    
    public void testXOSExpect(){
        // telnet authentication
        List<ExpectInfo> expectList = new ArrayList<ExpectInfo>();
        
        ExpectInfo cmd = new ExpectInfo("show system");
        cmd.record = true;
        cmd.addPattern("to quit:"," ",true);
        cmd.addPattern(" #$",null,false);
        expectList.add(cmd);

        // TODO:
//        Do you want to save configuration changes to currently selected configuration
//        file (primary.cfg) and reboot?
//        (y - save and reboot, n - reboot without save, <cr> - cancel command)

        runExpects(expectList);
    }

    public void testEOSExpect(){
        List<ExpectInfo> expectList = new ArrayList<ExpectInfo>();
        ExpectInfo cmd = new ExpectInfo(null,"->");
        cmd.record = false;
        expectList.add(cmd);
        
        cmd = new ExpectInfo("show system");
        cmd.record = true;
        cmd.addPattern("->",null,false);
        expectList.add(cmd);

        // TODO:
//        Do you want to save configuration changes to currently selected configuration
//        file (primary.cfg) and reboot?
//        (y - save and reboot, n - reboot without save, <cr> - cancel command)

        runExpects(expectList);
    }


    public void runExpects(List<ExpectInfo> expects){
    
        StringBuilder stringBuilder = new StringBuilder();
        for(ExpectInfo info: expects){
            //System.out.println("Processing: "+info);
            Response resp = expect(info);
            if(resp.output!=null)
                stringBuilder.append(resp.output);
            if(resp.err!=null){
                System.out.println("RUN ERROR: "+resp.err);
                break;
            }
        }

        System.out.print("RESPONSE:\n"+stringBuilder.toString());
    }

    public boolean sendCommand(String cmd){
        boolean sent = false;
        try {
          //  System.out.println("Sending: "+cmd);
            OutputStream outstr = getOutputStream();
            outstr.write(cmd.getBytes(), 0 , cmd.length());
            outstr.flush();
            sent = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sent;
    }
    
    protected boolean find(PatternEntry patternEntry,String promptTest) {
        boolean matched = false;
       // System.out.print(" TEST: "+promptTest+"...\n");
        Matcher m = patternEntry.pattern.matcher(promptTest);
        if(m.find()){
            matched = true;
        }
        return matched;
    }
   

    public Response expect(ExpectInfo expectInfo) {

         Response response = new Response();
         InputStream instr = getInputStream();
         StringBuilder outputBuilder = new StringBuilder();
         boolean done =false;

        try
        {
            
            byte[] buff = new byte[BUF_SIZE];
            int ret_read = 0;
            int lastEolIdx = -1;
            int lineCount =0;


            // send command (if any)
            if(expectInfo.command !=null){
                if(!sendCommand(expectInfo.command)){
                    response.err = "Failed to send command: "+expectInfo.command;
                }
            }

            do
            {
                ret_read = instr.read(buff);
                if(ret_read > 0)
                {
                    char lastCh = (char)buff[ret_read-1];

                    String str =new String(buff, 0, ret_read);

                    // get rid of all the ansi escape codes
                    //str =  str.replaceAll("\u001B\\[[;\\d]*m", "");
                    str =  str.replaceAll("\\e\\[[\\d;]*[^\\d;]","");

                    // keep track of the last new line
                    int eolIdx = str.lastIndexOf('\n');
                    int len = outputBuilder.length();
                    if(eolIdx>=0) {
                        lastEolIdx = len+eolIdx;
                        lineCount++;
                    }

                    outputBuilder.append(str);
                    
                    if(lastCh != '\n' && lastEolIdx>=0) {
                        String testStr = outputBuilder.substring(lastEolIdx+1).trim();
                        if(!testStr.isEmpty()) {
                            //System.out.println("TESTING: "+testStr);
                            for(PatternEntry patternEntry: expectInfo.patterns){
                                if(patternEntry.pattern.matcher(testStr).find()){
                                    if(expectInfo.record){
                                        outputBuilder.delete(lastEolIdx+1,outputBuilder.length());    
                                    }else{
                                        outputBuilder.delete(0,outputBuilder.length());
                                    }
                                    
                                  //  System.out.println("--->"+testStr+"<---("+patternEntry.pattern.toString()+")");
                                    // clear the prompt
                                    if(patternEntry.reply!=null){
                                        if(!sendCommand(patternEntry.reply)){
                                            response.err= "Failed to auto-reply to more prompt: "+testStr;
                                        }
                                    }
                                    if(!patternEntry.hasMore){
                                        done = true;
                                    }
                                    break;
                                }else{
                                  //  System.out.println("no match");
                                }
                            }
                        }
                    }
                   // System.out.println(str);
                }
            }
            while (!done && ret_read >= 0);
        }catch (Exception e) {
            e.printStackTrace();
            response.err = "IOException: "+e.getMessage();
        }finally {
           // System.out.println("DONE Expect!");
         //   System.out.println(outputBuilder.toString());
            if(!done){
                response.err = "Failed processing command: ";
            }
            response.output = outputBuilder.toString();
        }
        return response;
    }
    
  
    public void close() {
        try {
            if(tc!=null)
                tc.disconnect();
        } catch (IOException e) {
        }
    }

    // Adjust any options on the connection.
    //
    private void adjustOptions()
    {
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

        String[] supportedMacs = defaultMacList.toArray(new String[] {});
        _connection.setClient2ServerMACs(supportedMacs);
        _connection.setServer2ClientMACs(supportedMacs);
    }
    
    public static void main(String[] args) {
       CLISession test = new CLISession();
        try {
            test.telnet("10.54.147.249","admin","n7830466");
            test.testXOSExpect();
//            test.ssh("10.56.0.10",22,"engineer","engineer");
//            test.testEOSExpect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
