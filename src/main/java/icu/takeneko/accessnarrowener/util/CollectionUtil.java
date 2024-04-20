package icu.takeneko.accessnarrowener.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CollectionUtil {
    @SafeVarargs
    public static <E, C extends Collection<E>> C add(C collection, E... newElem){
        List<E> list = new ArrayList<>(collection);
        list.addAll(Arrays.asList(newElem));
        return (C) list;
    }

    @SafeVarargs
    public static <E, C extends Collection<E>> C without(C collection, E... elem){
        List<E> list = new ArrayList<>(collection);
        list.removeAll(Arrays.asList(elem));
        return (C) list;
    }
}
