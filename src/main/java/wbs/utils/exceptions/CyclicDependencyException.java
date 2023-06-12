package wbs.utils.exceptions;

/**
 * Used in {@link wbs.utils.util.entities.state.EntityState}s to ensure state restoration is done
 * in an order that doesn't cause a loop.
 */
public class CyclicDependencyException extends RuntimeException {
    public CyclicDependencyException() {}
    public CyclicDependencyException(String message) {
        super(message);
    }
}
