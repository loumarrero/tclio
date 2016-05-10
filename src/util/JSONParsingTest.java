package util;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

public class JSONParsingTest {

    private ScriptEngine engine;

    static void fun3(ScriptObjectMirror mirror) {
        System.out.println("Map:");
        System.out.println("\t" + mirror.getClassName() + ": " +
                Arrays.toString(mirror.getOwnKeys(true)));
    }

    public static void fun4(ScriptObjectMirror person) {
        System.out.println("Full Name is: " + person.callMember("getFullName"));
        System.out.println("First Name is: " + person.get("firstName"));
        System.out.println("Age is: " + person.get("age"));
    }

    public static void fun5(ScriptObjectMirror vendor) {
        System.out.println("Full Name is: " + vendor.get("family").getClass());
    }

    public static void main(String[] args) {
        JSONParsingTest test = new JSONParsingTest();
        long start = System.currentTimeMillis();
        test.initEngine();
        try {

            long t1 = System.currentTimeMillis();
            //test.parseJson();
            test.test();
            System.out.println("Sub Time: " + (System.currentTimeMillis() - t1));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            long end = System.currentTimeMillis() - start;
            System.out.println("Time: " + end);
        }
    }

    public void initEngine() {
        ScriptEngineManager sem = new ScriptEngineManager();
        this.engine = sem.getEngineByName("javascript");
    }

    public void parseJson() throws IOException, ScriptException {
        String json = new String(Files.readAllBytes(Paths.get("person.js")));
        String script = "Java.asJSONCompatible(" + json + ")";
        Object result = this.engine.eval(script);
        //assertThat(result, instanceOf(Map.class));
        Map contents = (Map) result;
        contents.forEach((t, u) -> {

            if (u instanceof ScriptObjectMirror) {
                fun3((ScriptObjectMirror) u);
            } else
                System.out.println(t + ":" + u);
        });
    }

    public void test() throws ScriptException {
        try {
            engine.eval(new FileReader("test.js"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
