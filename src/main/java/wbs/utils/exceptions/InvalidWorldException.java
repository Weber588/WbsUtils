package wbs.utils.exceptions;

/**
 * Thrown when there is an attempt to retrieve a world that does
 * not exist
 */
public class InvalidWorldException extends IllegalArgumentException {
    public InvalidWorldException() {}

    public InvalidWorldException(String message) {
        super(message);
    }
}
