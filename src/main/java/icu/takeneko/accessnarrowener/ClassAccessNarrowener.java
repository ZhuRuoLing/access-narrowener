package icu.takeneko.accessnarrowener;

import icu.takeneko.accessnarrowener.util.OpcodeUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;

public class ClassAccessNarrowener extends ClassTransformer {
    private final Map<FieldRef, FieldNode> fieldNodeMap = new HashMap<>();
    private final Map<Integer, FieldMethodRef> getterRef = new HashMap<>();
    private final Map<Integer, FieldMethodRef> setterRef = new HashMap<>();
    private List<ClassAccessNarrowener> others;
    private final TransformRule transformRule;

    public ClassAccessNarrowener(
            String className,
            TransformRule transformRule
    ) {
        super(className);
        this.transformRule = transformRule;
    }

    @Override
    public ClassNode loadClassFile() throws IOException {
        super.loadClassFile();
        for (FieldNode field : this.classNode.fields) {
            if (OpcodeUtil.isFinal(field.access) && !OpcodeUtil.isPublic(field.access)) continue;
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
        for (FieldRef ref : fieldNodeMap.keySet()) {
            getterRef.put(
                    ref.hash(),
                    new FieldMethodRef(
                            ref.owner(),
                            ref.getterDesc(),
                            ref.getterName(),
                            ref.isStatic()
                    )
            );
            setterRef.put(
                    ref.hash(),
                    new FieldMethodRef(
                            ref.owner(),
                            ref.setterDesc(),
                            ref.setterName(),
                            ref.isStatic()
                    )
            );
        }
        return this.classNode;
    }

    @Override
    public void transform() {
        for (MethodNode method : classNode.methods) {
            ListIterator<AbstractInsnNode> iter = method.instructions.iterator();
            while (iter.hasNext()) {
                AbstractInsnNode insnNode = iter.next();
                for (ClassAccessNarrowener other : others) {
                    Optional<AbstractInsnNode> optional = other.transformFieldAccessInsn(insnNode);
                    if (optional.isEmpty()) continue;
                    AbstractInsnNode insn = optional.get();
                    iter.remove();
                    iter.add(insn);
                    break;
                }
            }
        }
        fieldNodeMap.forEach((ref, fieldNode) -> {
            classNode.methods.add(ref.generateGetter());
            if (!OpcodeUtil.isFinal(fieldNode.access)) {
                classNode.methods.add(ref.generateSetter());
            }
            int originalAccess = fieldNode.access;
            originalAccess |= Opcodes.ACC_PRIVATE;
            originalAccess &= ~Opcodes.ACC_PROTECTED;
            originalAccess &= ~Opcodes.ACC_PUBLIC;
            fieldNode.access = originalAccess;
        });
    }

    public Optional<AbstractInsnNode> transformFieldAccessInsn(AbstractInsnNode insnNode) {
        if (!OpcodeUtil.isTransformableOpcode(insnNode.getOpcode())) return Optional.empty();
        if (insnNode instanceof FieldInsnNode insn) {
            int fieldRefHash = new FieldRef(
                    0,
                    insn.owner,
                    insn.desc,
                    insn.name,
                    OpcodeUtil.isStaticFieldAccessOpcode(insn.getOpcode())
            ).hash();
            switch (insn.getOpcode()) {
                case Opcodes.GETSTATIC -> {
                    FieldMethodRef methodRef = getterRef.get(fieldRefHash);
                    if (methodRef == null) {
                        return Optional.empty();
                    } else {
                        return Optional.of(new MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                methodRef.owner(),
                                methodRef.name(),
                                methodRef.descriptor()
                        ));
                    }
                }
                case Opcodes.PUTSTATIC -> {
                    FieldMethodRef methodRef = setterRef.get(fieldRefHash);
                    if (methodRef == null) {
                        return Optional.empty();
                    } else {
                        return Optional.of(new MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                methodRef.owner(),
                                methodRef.name(),
                                methodRef.descriptor()
                        ));
                    }
                }
                case Opcodes.GETFIELD -> {
                    FieldMethodRef methodRef = getterRef.get(fieldRefHash);
                    if (methodRef == null) {
                        return Optional.empty();
                    } else {
                        return Optional.of(new MethodInsnNode(
                                Opcodes.INVOKEVIRTUAL,
                                methodRef.owner(),
                                methodRef.name(),
                                methodRef.descriptor()
                        ));
                    }
                }
                case Opcodes.PUTFIELD -> {
                    FieldMethodRef methodRef = setterRef.get(fieldRefHash);
                    if (methodRef == null) {
                        return Optional.empty();
                    } else {
                        return Optional.of(new MethodInsnNode(
                                Opcodes.INVOKEVIRTUAL,
                                methodRef.owner(),
                                methodRef.name(),
                                methodRef.descriptor()
                        ));
                    }
                }
                default -> {
                    return Optional.empty();
                }
            }
        } else {
            return Optional.empty();
        }
    }

    public void setOtherClasses(List<ClassAccessNarrowener> others) {
        this.others = others;
    }
}
