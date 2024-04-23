package icu.takeneko.accessnarrowener.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class PathUtil {
    public static Path normalizeExistingPath(Path path) {
        try {
            return path.toRealPath();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String removePrefix(String thiz, String prefix){
        if (prefix == null || prefix.isBlank() || !thiz.startsWith(prefix))return thiz;
        return thiz.substring(prefix.length());
    }

    public static String removePostfix(String thiz, String postfix){
        if (postfix == null || postfix.isBlank() || !thiz.endsWith(postfix))return thiz;
        return thiz.substring(0, thiz.lastIndexOf(postfix));
    }
}
