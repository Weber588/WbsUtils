package wbs.utils.exceptions;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

/**
 * Thrown when a {@link ConfigurationSection} is either malformed and not valid
 * yaml, or when the configuration is not correct in a given context
 */
public class InvalidConfigurationException extends RuntimeException {

    @Nullable
    private String directory;
    private boolean isLogged = false;

    public InvalidConfigurationException() {
        isLogged = true;
    }
    public InvalidConfigurationException(String msg) {
        super(msg);
    }
    public InvalidConfigurationException(String msg, @Nullable String directory) {
        super(msg);
        this.directory = directory;
    }

    @Nullable
    public String getDirectory() {
        return directory;
    }
    public boolean isLogged() {
        return isLogged;
    }
}
