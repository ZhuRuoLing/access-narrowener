package icu.takeneko.accessnarrowener.test;

import icu.takeneko.accessnarrowener.AccessNarrowener;
import icu.takeneko.accessnarrowener.TransformRule;
import icu.takeneko.accessnarrowener.util.ClassUtil;
import net.bytebuddy.agent.ByteBuddyAgent;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static icu.takeneko.accessnarrowener.AccessNarrowener.findAllClassMatching;

public class Test {
    final Map<String, byte[]> resultMap = new ConcurrentHashMap<>();

    @org.junit.jupiter.api.Test
    void test() throws IOException {
        var inst = ByteBuddyAgent.install();
        inst.addTransformer(new ClassRedefiner(this), true);
        var result = AccessNarrowener.process(findAllClassMatching("icu\\.takeneko"), TransformRule.DEFAULT);
        final Iterator<Path> iterator = Files.walk(ClassUtil.PATCHER_DUMP_PATH)
                .sorted(Comparator.reverseOrder()).iterator();
        while (iterator.hasNext()) {
            Files.delete(iterator.next());
        }
        result.forEach((s, bytes) -> {
            try {
                ClassUtil.writeClassBytes(s,bytes);
                resultMap.put(s,bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        result.forEach((s, bytes) -> {
            try {
                System.out.println("s = " + s);
                //Class.forName(s);
                inst.retransformClasses(Class.forName(s));
            } catch (Throwable e) {
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
    public static class ClassRedefiner implements ClassFileTransformer{

        private final Test testInstance;

        public ClassRedefiner(Test testInstance) {
            this.testInstance = testInstance;
        }

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            if (testInstance.resultMap.keySet().contains(className.replace("/","."))){
                System.out.println(className);
            }
            return testInstance.resultMap.get(className.replace("/","."));
        }
    }
}
