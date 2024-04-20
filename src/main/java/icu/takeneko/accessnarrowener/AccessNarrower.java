package icu.takeneko.accessnarrowener;

import icu.takeneko.accessnarrowener.util.CollectionUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessNarrower {

    public static Map<String, byte[]> process(List<String> classNames, TransformRule rule) throws IOException {
        List<LinkedClassAccessNarrower> classes = new ArrayList<>();
        classes.addAll(classNames.stream().map(it -> new LinkedClassAccessNarrower(it, rule)).toList());
        for (LinkedClassAccessNarrower clz : classes) {
            clz.setOtherClasses(CollectionUtil.without(classes, clz));
            clz.loadClassFile();
        }
        for (LinkedClassAccessNarrower clz : classes) {
            clz.transform();
        }
        Map<String, byte[]> result = new HashMap<>();
        classes.stream()
                .map(it -> Map.entry(it.className, it.getBytes()))
                .forEach(it -> result.put(it.getKey(), it.getValue()));
        return result;
    }
}
