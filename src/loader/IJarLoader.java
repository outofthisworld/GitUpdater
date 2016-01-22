package loader;

import java.io.IOException;
import java.util.jar.JarFile;

/**
 * Created by Unknown on 21/01/2016.
 */
public interface IJarLoader {
    <T extends JarFile> void loadJar(T file) throws IOException;
}
