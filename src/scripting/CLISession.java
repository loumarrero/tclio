package scripting;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lmarrero on 4/27/2016.
 */
public class CLISession{

    private static int SO_TIMEOUT = 5000;
    private static int RETRIES  = 3;
    private static int BUF_SIZE = 1024;// 8192;
    
    TelnetClient tc = null;
    
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
    
    
    public void ssh(String ip){
        System.out.println(String.format("ssh to %s:%d",ip,23));
        testExpect();
    }
    
    
    public void telnet(String ip) throws IOException{
        System.out.println(String.format("telnet to %s:%d",ip,23));
        tc = new TelnetClient();
        tc.connect(ip, 23);
        testExpect();
    }
    
    public InputStream getInputStream() {
        return  tc.getInputStream();
    }
    
    public OutputStream getOutputStream() {
        return tc.getOutputStream();
    }
    
    
    public void testExpect(){
        List<ExpectInfo> expectList = new ArrayList<ExpectInfo>();
        expectList.add(new ExpectInfo(null,"login:","admin"));
        expectList.add(new ExpectInfo(null,"password:","n7830466"));
        // TODO: add support for banner

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
    
    public void runExpects(List<ExpectInfo> expects){
    
        StringBuilder stringBuilder = new StringBuilder();
        for(ExpectInfo info: expects){
        //    System.out.println("Processing: "+info);
            Response resp = expect(info);
            if(resp.output!=null)
                stringBuilder.append(resp.output);
            if(resp.err!=null){
                System.out.println("RUN ERROR: "+resp.err);
                break;
            }
        }

        System.out.print(stringBuilder.toString());
    }

    public boolean sendCommand(String cmd){
        boolean sent = false;
        try {
        //    System.out.println("Sending: "+cmd);
            OutputStream outstr = getOutputStream();
            outstr.write(cmd.getBytes(), 0 , cmd.length());
            outstr.flush();
            sent = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sent;
    }
    
    protected void sleep(long timeout){
        try {
         //   System.out.println("Prompt not detected yet sleeping...");
            Thread.sleep(timeout);
//            --retries;
//            if(retries <=0){
//                response.err = "Timed out waiting for prompt.";
//                bytesRead = -1;
//            }
        } catch (InterruptedException e) {
        }
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
                                    
                                    //System.out.println("--->"+testStr+"<---("+patternEntry.pattern.toString()+")");
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

  
    public static void main(String[] args) {
       CLISession test = new CLISession();
        try {
            test.telnet("10.54.147.249");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
