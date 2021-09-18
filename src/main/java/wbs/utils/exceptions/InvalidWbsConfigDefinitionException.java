package wbs.utils.exceptions;

public class InvalidWbsConfigDefinitionException extends RuntimeException {
    public InvalidWbsConfigDefinitionException() {}

    public InvalidWbsConfigDefinitionException(String message) {
        super(message);
    }
}
