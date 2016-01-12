package instrumentation;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * Created by Unknown on 10/01/2016.
 */
public class Test implements ClassFileTransformer {

    public static void main(String[] args) {
        try {

            com.sun.tools.attach.VirtualMachine virtualMachine = com.sun.tools.attach.VirtualMachine.attach("7744");
            virtualMachine.loadAgent("C:\\Users\\Unknown\\IdeaProjects\\ATestProject\\out\\artifacts\\JavaAgent\\JavaAgent.jar");
        } catch (AttachNotSupportedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AgentInitializationException e) {
            e.printStackTrace();
        } catch (AgentLoadException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        return new byte[0];
    }
}
