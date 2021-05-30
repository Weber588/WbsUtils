package wbs.utils.exceptions;

public class MissingRequiredKeyException extends RuntimeException {

    public MissingRequiredKeyException() {}

    public MissingRequiredKeyException(String msg) {
        super(msg);
    }
}
