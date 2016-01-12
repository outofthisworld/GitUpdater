package instrumentation;

import javax.swing.*;
import java.lang.instrument.Instrumentation;

/**
 * Created by Unknown on 12/01/2016.
 */
public class JavaAgent {

    public static void premain(String args, Instrumentation inst) throws Exception {
        JOptionPane.showMessageDialog(null, "Hello from premain agent!");
    }

    public static void agentmain(String args, Instrumentation inst) throws Exception {

        JOptionPane.showMessageDialog(null, "Hello from premain agent!");
    }
}
