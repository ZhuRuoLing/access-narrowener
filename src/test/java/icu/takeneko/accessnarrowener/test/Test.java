package icu.takeneko.accessnarrowener.test;

import icu.takeneko.accessnarrowener.AccessNarrower;
import icu.takeneko.accessnarrowener.TransformRule;
import icu.takeneko.accessnarrowener.util.ClassUtil;
import icu.takeneko.accessnarrowener.util.PathUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipError;
import java.util.zip.ZipFile;

import static icu.takeneko.accessnarrowener.AccessNarrower.findAllClassMatching;

public class Test {
    @org.junit.jupiter.api.Test
    void test() throws IOException {
        var result = AccessNarrower.process(findAllClassMatching("icu\\.takeneko"), TransformRule.DEFAULT);
        final Iterator<Path> iterator = Files.walk(ClassUtil.PATCHER_DUMP_PATH)
                .sorted(Comparator.reverseOrder()).iterator();
        while (iterator.hasNext()) {
            Files.delete(iterator.next());
        }
        result.forEach((s, bytes) -> {
            try {
                ClassUtil.writeClassBytes(s,bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @org.junit.jupiter.api.Test
    void test1() throws IOException {
        try {
            for (String s : findAllClassMatching("icu\\.takeneko")) {
                System.out.println(s);
            }
        }catch (Throwable t){
            t.printStackTrace();
        }
    }
}
