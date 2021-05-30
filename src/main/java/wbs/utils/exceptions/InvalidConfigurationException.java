package wbs.utils.exceptions;

public class InvalidConfigurationException extends RuntimeException {

    public InvalidConfigurationException() {}
    public InvalidConfigurationException(String msg) {
        super(msg);
    }
}
