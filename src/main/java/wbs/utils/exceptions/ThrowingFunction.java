package wbs.utils.exceptions;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Exception> extends Function<T, R> {
    R applyOrThrow(T t) throws E;
    default R apply(T t) {
        try {
            return applyOrThrow(t);
        } catch (Exception ex) {
            // Only wrap E, not any other classes.
            try {
                @SuppressWarnings("unchecked")
                E ex1 = (E) ex;
                throw new RuntimeException(ex1);
            } catch (ClassCastException notE) {
                return sneakyThrow(ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    default <H extends Throwable> R sneakyThrow(Throwable t) throws H {
        throw (H) t;
    }
}
