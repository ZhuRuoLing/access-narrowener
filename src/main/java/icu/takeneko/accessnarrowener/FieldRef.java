package icu.takeneko.accessnarrowener;

import icu.takeneko.accessnarrowener.util.OpcodeUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ArrayList;
import java.util.Objects;

public record FieldRef(
        int access,
        String owner,
        String descriptor,
        String name,
        boolean isStatic
) {
    private static final boolean ADD_SYNTHETIC = System.getProperty("accessnarrowener.addSynthetic") != null;

    public MethodNode generateGetter() {
        MethodNode methodNode = new MethodNode();
        methodNode.name = getterName();
        methodNode.desc = getterDesc();
        methodNode.access = Opcodes.ACC_PUBLIC;
        if (ADD_SYNTHETIC) {
            methodNode.access |= Opcodes.ACC_SYNTHETIC;
        }
        if (isStatic) {
            methodNode.access |= Opcodes.ACC_STATIC;
        }
        LabelNode startLabel = new LabelNode();
        LabelNode endLabel = new LabelNode();
        methodNode.localVariables = new ArrayList<>();
        methodNode.localVariables.add(new LocalVariableNode(
                "this",
                "L" + owner + ";",
                owner,
                startLabel,
                endLabel,
                0
        ));
        InsnList insnList = methodNode.instructions;
        insnList.add(startLabel);
        if (!isStatic) {
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            insnList.add(new FieldInsnNode(Opcodes.GETFIELD, owner, name, descriptor));
        } else {
            insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, owner, name, descriptor));
        }
        insnList.add(new InsnNode(OpcodeUtil.getReturnOpcode(descriptor)));
        insnList.add(endLabel);
        return methodNode;
    }

    public MethodNode generateSetter() {
        MethodNode methodNode = new MethodNode();
        methodNode.name = setterName();
        methodNode.desc = setterDesc();
        methodNode.access = Opcodes.ACC_PUBLIC;

        if (ADD_SYNTHETIC) {
            methodNode.access |= Opcodes.ACC_SYNTHETIC;
        }
        if (isStatic) {
            methodNode.access |= Opcodes.ACC_STATIC;
        }
        InsnList insnList = methodNode.instructions;
        LabelNode startLabel = new LabelNode();
        LabelNode endLabel = new LabelNode();
        methodNode.localVariables = new ArrayList<>();
        methodNode.localVariables.add(new LocalVariableNode(
                "this",
                "L" + owner + ";",
                owner,
                startLabel,
                endLabel,
                0
        ));
        methodNode.localVariables.add(new LocalVariableNode(
                "value",
                descriptor,
                descriptor,
                startLabel,
                endLabel,
                1
        ));
        methodNode.parameters = new ArrayList<>();
        methodNode.parameters.add(new ParameterNode("value", 0));
        insnList.add(startLabel);
        if (!isStatic) {
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            insnList.add(new VarInsnNode(OpcodeUtil.getLoadOpcode(descriptor), 1)); // param 1
            insnList.add(new FieldInsnNode(Opcodes.PUTFIELD, owner, name, descriptor));
        } else {
            insnList.add(new VarInsnNode(OpcodeUtil.getLoadOpcode(descriptor), 1)); // param 1
            insnList.add(new FieldInsnNode(Opcodes.PUTSTATIC, owner, name, descriptor));
        }
        insnList.add(new InsnNode(Opcodes.RETURN));
        insnList.add(endLabel);
        return methodNode;
    }

    public String setterDesc(){
        return String.format("(%s)V", descriptor);
    }

    public String getterDesc(){
        return String.format("()%s", descriptor);
    }

    public String getterName() {
        return String.format("getter$%x$%s", owner.hashCode(), name);
    }

    public String setterName() {
        return String.format("setter$%x$%s", owner.hashCode(), name);
    }

    public int hash() {
        return Objects.hash(owner, descriptor, name, isStatic);
    }
}
