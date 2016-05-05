package scripting;

/**
 * Created by lmarrero on 5/3/2016.
 */
public class Test {
    //used to test java runtime and io subsystem during configure
    public static void main(String[] argv) {
        System.out.println("OK");
        System.exit(0);
    }

    //used to test javah utility for .h file generation
    private static native void foo();

    //used to test java invocation after install
    public static String isOK() {
        return "OK";
    }

}
