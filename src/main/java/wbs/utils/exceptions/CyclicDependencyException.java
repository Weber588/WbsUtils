package wbs.utils.exceptions;

public class CyclicDependencyException extends RuntimeException {
    public CyclicDependencyException() {}
    public CyclicDependencyException(String message) {
        super(message);
    }
}
