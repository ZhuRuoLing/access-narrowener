package icu.takeneko.accessnarrowener;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkedClassAccessNarrower extends ClassTransformer {
    private final Map<FieldRef, FieldNode> fieldNodeMap = new HashMap<>();
    private List<LinkedClassAccessNarrower> others;
    private final TransformRule transformRule;

    public LinkedClassAccessNarrower(
            String className,
            TransformRule transformRule) {
        super(className);
        this.transformRule = transformRule;
    }

    @Override
    public ClassNode loadClassFile() throws IOException {
        super.loadClassFile();
        for (FieldNode field : this.classNode.fields) {
            fieldNodeMap.put(
                    new FieldRef(
                            field.access,
                            this.className.replace(".", "/"),
                            field.desc,
                            field.name,
                            (field.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC
                    ),
                    field
            );
        }
        List<FieldRef> removes = new ArrayList<>();
        fieldNodeMap.keySet().forEach(ref -> {
            if (!transformRule.shouldTransformFieldRef(this.className, ref)) {
                removes.add(ref);
            }
        });
        for (FieldRef ref : removes) {
            fieldNodeMap.remove(ref);
        }
        return this.classNode;
    }

    @Override
    public void transform() {
        fieldNodeMap.forEach((ref, fieldNode) -> {
            classNode.methods.add(ref.generateGetter());
            classNode.methods.add(ref.generateSetter());
            int originalAccess = fieldNode.access;
            originalAccess |= Opcodes.ACC_PRIVATE;
            originalAccess &= ~Opcodes.ACC_PUBLIC;
            fieldNode.access = originalAccess;
        });
    }

    public boolean findLink(FieldAccess access) {
        if (others == null) return false;
        return false;
    }

    public void setOtherClasses(List<LinkedClassAccessNarrower> others) {
        this.others = others;
    }
}
