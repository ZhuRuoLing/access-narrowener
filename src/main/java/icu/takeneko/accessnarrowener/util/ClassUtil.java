package icu.takeneko.accessnarrowener.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClassUtil {
    private static final Path patcherDumps = Path.of("./.access-narrowener");

    public static byte[] getClassBytes(String className) throws IOException {
        try (InputStream in = ClassUtil.class.getClassLoader().getResourceAsStream(className.replace('.', '/') + ".class")) {
            return in.readAllBytes();
        }
    }

    public static void writeClassBytes(String className, byte[] bytes) throws IOException {
        Path path = patcherDumps.resolve(className + ".class");
        Files.createDirectories(path.getParent());
        Files.write(path, bytes);
    }
}
