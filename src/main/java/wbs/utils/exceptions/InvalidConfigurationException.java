package wbs.utils.exceptions;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Thrown when a {@link ConfigurationSection} is either malformed and not valid
 * yaml, or when the configuration is not correct in a given context
 */
public class InvalidConfigurationException extends RuntimeException {

    public InvalidConfigurationException() {}
    public InvalidConfigurationException(String msg) {
        super(msg);
    }
}
