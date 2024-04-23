package icu.takeneko.accessnarrowener.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClassUtil {
    public static final Path PATCHER_DUMP_PATH = Path.of("./.access-narrowener");

    public static byte[] getClassBytes(String className) throws IOException {
        try (InputStream in = ClassUtil.class.getClassLoader().getResourceAsStream(className.replace('.', '/') + ".class")) {
            return in.readAllBytes();
        }
    }

    public static void writeClassBytes(String className, byte[] bytes) throws IOException {
        Path path = PATCHER_DUMP_PATH.resolve(className.replace(".","/") + ".class");
        Files.createDirectories(path.getParent());
        Files.write(path, bytes);
    }
}
