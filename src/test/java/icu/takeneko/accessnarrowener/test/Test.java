package icu.takeneko.accessnarrowener.test;

import icu.takeneko.accessnarrowener.AccessNarrower;
import icu.takeneko.accessnarrowener.TransformRule;
import icu.takeneko.accessnarrowener.util.ClassUtil;

import java.io.IOException;
import java.util.List;

public class Test {
    @org.junit.jupiter.api.Test
    void test() throws IOException {
        var result = AccessNarrower.process(List.of("A", "B"), TransformRule.DEFAULT);
        result.forEach((s, bytes) -> {
            try {
                ClassUtil.writeClassBytes(s,bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
