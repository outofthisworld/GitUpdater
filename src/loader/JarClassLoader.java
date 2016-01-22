package loader;

import com.sun.org.apache.bcel.internal.util.ClassLoader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Unknown on 21/01/2016.
 */
public class JarClassLoader extends ClassLoader implements IJarClassLoader, ClassTransformer {
    private final HashMap<String, Class<?>> jarEntries = new HashMap<>();
    private final ArrayList<IClassTransformListener> transformers = new ArrayList<>();
    private JarFile jarFile;

    public JarClassLoader() {
    }

    
    public JarClassLoader(JarFile jarFile) throws IOException {
        loadJar(jarFile);
    }


    public static <T extends JarFile> JarClassLoader loadJarFile(final T jarFile) throws IOException {
        return new JarClassLoader(jarFile);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (jarEntries.get(name) != null)
            return jarEntries.get(name);

        return super.loadClass(name);
    }

    public void forEachClass(Consumer<Class<?>> consumer) {
        jarEntries.values().stream().forEach(e -> consumer.accept(e));
    }

    public boolean containsClassMatching(Predicate<Class<?>> pred) {
        return jarEntries.values().stream().filter(e -> pred.test(e)).count() > 0;
    }

    private final void forEachJarEntry(BiConsumer<String, byte[]> consumer) throws IOException {
        for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
            JarEntry en = entries.nextElement();

            if (en.isDirectory() || !en.getName().endsWith(".class"))
                continue;

            consumer.accept(en.getName(), readEntry(jarFile.getInputStream(en), (int) en.getSize()));
        }
    }

    @Override
    public <T extends JarFile> void loadJar(T file) throws IOException {
        this.jarFile = file;
        forEachJarEntry((n, b) -> {
            jarEntries.put(n, defineClass(n, b, 0, b.length));
        });
    }

    public <T extends JarFile> void loadAndTransformJar(T file) throws IOException {
        this.jarFile = file;
        forEachJarEntry((n, b) -> {
            jarEntries.put(n, defineClass(n, transform(b), 0, b.length));
        });
    }

    private final <T extends InputStream> byte[] readEntry(final T in, final int entrySize) throws IOException {
        byte[] entryBytes = new byte[entrySize];
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(in)) {
            bufferedInputStream.read(entryBytes, 0, entryBytes.length);
        }
        return entryBytes;
    }

    @Override
    public byte[] transform(byte[] classBytes) {
        transformers.stream().forEach(t -> t.transform(classBytes));
        return classBytes;
    }

    @Override
    public void addTransFormer(IClassTransformListener transformer) {
        transformers.add(transformer);
    }

    @Override
    public void removeTransformer(IClassTransformListener transformer) {
        transformers.remove(transformer);
    }

    @Override
    public Class<?> getClass(String className) {
        return jarEntries.get(className);
    }
}
