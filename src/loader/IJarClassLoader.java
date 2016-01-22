package loader;

/**
 * Created by Unknown on 21/01/2016.
 */
public interface IJarClassLoader extends IJarLoader {
    Class<?> getClass(String className);
}
