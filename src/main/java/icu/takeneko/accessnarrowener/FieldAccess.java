package icu.takeneko.accessnarrowener;

public record FieldAccess(
        Access access,
        String owner,
        String descriptor
) {
    public enum Access{
        GET,PUT
    }
}
