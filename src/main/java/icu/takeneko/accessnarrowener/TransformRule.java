package icu.takeneko.accessnarrowener;

import icu.takeneko.accessnarrowener.util.CollectionUtil;
import icu.takeneko.accessnarrowener.util.Filter;

import java.util.List;

public class TransformRule {
    public static final TransformRule DEFAULT = new TransformRule(
            List.of("Ljava"),
            List.of("java"),
            $ -> true,
            $ -> true
    );

    private final List<String> ownerIgnorePrefix;
    private final List<String> classNameIgnorePrefix;
    private final Filter<FieldRef> fieldRefFilter;
    private final Filter<FieldAccess> fieldAccessFilter;

    public TransformRule(
            List<String> ownerIgnorePrefix,
            List<String> classNameIgnorePrefix,
            Filter<FieldRef> fieldRefFilter,
            Filter<FieldAccess> fieldAccessFilter
    ) {
        this.ownerIgnorePrefix = ownerIgnorePrefix;
        this.classNameIgnorePrefix = classNameIgnorePrefix;
        this.fieldRefFilter = fieldRefFilter;
        this.fieldAccessFilter = fieldAccessFilter;
    }

    public TransformRule ownerIgnorePrefix(String s){
        return new TransformRule(
                CollectionUtil.add(this.ownerIgnorePrefix, s),
                this.classNameIgnorePrefix,
                this.fieldRefFilter,
                this.fieldAccessFilter
        );
    }

    public TransformRule classNameIgnorePrefix(String s){
        return new TransformRule(
                this.ownerIgnorePrefix,
                CollectionUtil.add(this.classNameIgnorePrefix, s),
                this.fieldRefFilter,
                this.fieldAccessFilter
        );
    }

    public TransformRule fieldRefFilter(Filter<FieldRef> f){
        return new TransformRule(
                this.ownerIgnorePrefix,
                this.classNameIgnorePrefix,
                f,
                this.fieldAccessFilter
        );
    }

    public TransformRule fieldAccessFilter(Filter<FieldAccess> f){
        return new TransformRule(
                this.ownerIgnorePrefix,
                this.classNameIgnorePrefix,
                this.fieldRefFilter,
                f
        );
    }

    public boolean shouldTransformFieldRef(String className, FieldRef ref){
        return this.ownerIgnorePrefix.stream().noneMatch(it -> ref.owner().startsWith(it))
                && this.classNameIgnorePrefix.stream().noneMatch(className::startsWith)
                && this.fieldRefFilter.test(ref);
    }

    public boolean shouldTransformFieldAccess(String className, FieldAccess ref) {
        return this.ownerIgnorePrefix.stream().noneMatch(it -> ref.owner().startsWith(it))
                && this.classNameIgnorePrefix.stream().noneMatch(className::startsWith)
                && this.fieldAccessFilter.test(ref);
    }
}
