package wbs.utils.exceptions;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Thrown when a key is required in a {@link ConfigurationSection} and it
 * was missing
 */
public class MissingRequiredKeyException extends RuntimeException {

    public MissingRequiredKeyException() {}

    public MissingRequiredKeyException(String msg) {
        super(msg);
    }
}
