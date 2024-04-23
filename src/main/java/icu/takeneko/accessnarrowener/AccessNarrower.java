package icu.takeneko.accessnarrowener;

import icu.takeneko.accessnarrowener.util.CollectionUtil;
import icu.takeneko.accessnarrowener.util.PathUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipError;
import java.util.zip.ZipFile;

public class AccessNarrower {

    public static Map<String, byte[]> process(List<String> classNames, TransformRule rule) throws IOException {
        List<ClassAccessNarrower> classes = new ArrayList<>();
        classes.addAll(classNames.stream().map(it -> new ClassAccessNarrower(it, rule)).toList());
        for (ClassAccessNarrower clz : classes) {
            clz.setOtherClasses(CollectionUtil.without(classes, clz));
            clz.loadClassFile();
        }
        for (ClassAccessNarrower clz : classes) {
            clz.transform();
        }
        Map<String, byte[]> result = new HashMap<>();
        classes.stream()
                .forEach(it -> result.put(it.className, it.getBytes()));
        return result;
    }

    public static List<String> findAllClassMatching(String regexPattern) throws IOException {
        Pattern pattern = Pattern.compile(regexPattern);
        List<Path> classPathEntries = new ArrayList<>();
        for (String cp : System.getProperty("java.class.path").split(File.pathSeparator)) {
            Path path = Paths.get(cp);
            if (Files.exists(path)) {
                classPathEntries.add(PathUtil.normalizeExistingPath(path));
            }
        }
        List<String> result = new ArrayList<>();
        for (Path entry : classPathEntries) {
            if (Files.isDirectory(entry)) {
                Files.walk(entry)
                        .filter(it -> it.toFile().isFile() && it.toString().endsWith(".class"))
                        .map(it -> PathUtil.removePrefix(it.toString(), entry.toString()))
                        .map(it -> PathUtil.removePrefix(it.replace("\\", "/"), "/"))
                        .forEach(result::add);
            } else {
                try (ZipFile zf = new ZipFile(entry.toFile())) {
                    var it = zf.entries().asIterator();
                    while (it.hasNext()) {
                        ZipEntry ze = it.next();
                        if (ze.isDirectory()) continue;
                        if (!ze.getName().endsWith(".class")) continue;
                        result.add(ze.getName());
                    }
                } catch (ZipError | IOException e) {
                    throw new IOException("Error reading " + entry, e);
                }
            }
        }
        return result.stream()
                .map(it -> it.replace("/","."))
                .map(it -> PathUtil.removePostfix(it, ".class"))
                .filter(it -> pattern.matcher(it).find())
                .toList();
    }
}
