package loader;

import com.sun.org.apache.bcel.internal.util.ClassLoader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Unknown on 21/01/2016.
 */
public class JarClassTransformer extends ClassLoader implements IJarClassLoader, ClassTransformer {
    private static final String CLASS_CONS = ".class";
    private final HashMap<String, byte[]> jarEntries = new HashMap<>();
    private final HashMap<String, Class<?>> klazzMap = new HashMap<>();
    private final ArrayList<IClassTransformListener> transformers = new ArrayList<>();
    private JarFile jarFile;

    public JarClassTransformer() {
    }


    public JarClassTransformer(JarFile jarFile) throws IOException {
        Objects.requireNonNull(jarFile);
        loadAndTransformJar(jarFile);
    }

    public static <T extends JarFile> JarClassTransformer loadJarFile(final T jarFile) throws IOException {
        Objects.requireNonNull(jarFile);
        return new JarClassTransformer(jarFile);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        byte[] classBytes = jarEntries.get(name);

        if (classBytes != null) {
            klazzMap.put(name, defineClass(name, classBytes, 0, classBytes.length));
            return klazzMap.get(name);
        }

        return super.loadClass(name);
    }

    public void forEachClass(Consumer<Class<?>> consumer) {
        klazzMap.values().stream().forEach(e -> consumer.accept(e));
    }

    public boolean containsClassMatching(Predicate<Class<?>> pred) {
        return klazzMap.values().stream().filter(e -> pred.test(e)).count() > 0;
    }

    private final void forEachJarEntry(BiConsumer<String, byte[]> consumer) throws IOException {
        for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
            JarEntry en = entries.nextElement();

            if (en.isDirectory() || !en.getName().endsWith(CLASS_CONS))
                continue;

            consumer.accept(en.getName().replace(CLASS_CONS, ""), readEntry(jarFile.getInputStream(en), (int) en.getSize()));
        }
    }

    @Override
    public <T extends JarFile> void loadJar(T file) throws IOException {
        Objects.requireNonNull(file);
        this.jarFile = file;
        forEachJarEntry((n, b) -> {
            jarEntries.put(n, b);
        });
    }

    public <T extends JarFile> void loadAndTransformJar(T file) throws IOException {
        Objects.requireNonNull(file);
        this.jarFile = file;
        forEachJarEntry((n, b) -> {
            jarEntries.put(n, transform(b));
        });
    }

    private final <T extends InputStream> byte[] readEntry(final T in, final int entrySize) throws IOException {
        byte[] entryBytes = new byte[entrySize];
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(in)) {
            bufferedInputStream.read(entryBytes, 0, entryBytes.length);
        }
        in.close();
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
        return klazzMap.get(className);
    }
}
