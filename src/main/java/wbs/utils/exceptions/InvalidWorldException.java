package wbs.utils.exceptions;

public class InvalidWorldException extends RuntimeException {
    public InvalidWorldException() {}

    public InvalidWorldException(String message) {
        super(message);
    }
}
