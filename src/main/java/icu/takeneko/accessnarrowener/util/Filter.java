package icu.takeneko.accessnarrowener.util;

@FunctionalInterface
public interface Filter<T> {
    boolean test(T t);
}
