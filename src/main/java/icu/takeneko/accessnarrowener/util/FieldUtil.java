package icu.takeneko.accessnarrowener.util;

import org.objectweb.asm.Opcodes;

public class FieldUtil {
    public static int getReturnOpcode(String descriptor){
        return switch (descriptor){
            case "I" -> Opcodes.IRETURN;
            case "L" -> Opcodes.LRETURN;
            case "F" -> Opcodes.FRETURN;
            case "D" -> Opcodes.DRETURN;
            default -> Opcodes.ARETURN;
        };
    }

    public static int getStoreOpcode(String desc){
        return switch (desc){
            case "I" -> Opcodes.ISTORE;
            case "L" -> Opcodes.LSTORE;
            case "F" -> Opcodes.FSTORE;
            case "D" -> Opcodes.DSTORE;
            default -> Opcodes.ASTORE;
        };
    }

    public static int getLoadOpcode(String desc){
        return switch (desc){
            case "I" -> Opcodes.ILOAD;
            case "L" -> Opcodes.LLOAD;
            case "F" -> Opcodes.FLOAD;
            case "D" -> Opcodes.DLOAD;
            default -> Opcodes.ALOAD;
        };
    }
}
