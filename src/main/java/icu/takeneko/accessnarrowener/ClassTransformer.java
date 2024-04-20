package icu.takeneko.accessnarrowener;

import icu.takeneko.accessnarrowener.util.ClassUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;

public abstract class ClassTransformer {
    protected ClassNode classNode;
    public final String className;

    public ClassTransformer(String className){
        this.className = className;
    }

    public ClassNode loadClassFile() throws IOException {
        byte[] file = ClassUtil.getClassBytes(className);
        classNode = new ClassNode();
        ClassReader reader = new ClassReader(file);
        reader.accept(classNode, 0);
        return classNode;
    }

    public abstract void transform();

    public ClassNode getClassNode() {
        return classNode;
    }

    public byte[] getBytes(){
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

}
