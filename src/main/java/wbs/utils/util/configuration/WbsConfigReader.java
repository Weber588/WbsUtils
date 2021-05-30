package wbs.utils.util.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import wbs.utils.exceptions.MissingRequiredKeyException;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A static class to read configs and automatically provide errors to the WbsSettings object without external logic
 */
public final class WbsConfigReader {
    private WbsConfigReader() {}

    /**
     * Check if a config is null, and if it is, log it against the current settings
     * @param section The section to check for a key
     * @param field The key
     * @param settings The settings to log errors to
     * @param directory The path to the object being configured
     * @return True if the field is missing
     */
    public static boolean isNull(ConfigurationSection section, String field,
                                 @Nullable WbsSettings settings, @Nullable String directory) {
        if (section.get(field) == null) {
            if (settings != null) {
                settings.logError("Missing field: " + field, directory);
            }
            return true;
        }

        return false;
    }

    /**
     * Check if a config is null, and if it is, log it against the current settings
     * @param section The section to check for a key
     * @param field The key
     * @param settings The settings to log errors to
     * @param directory The path to the object being configured
     */
    public static void requireNotNull(@Nullable ConfigurationSection section, String field,
                                      @Nullable WbsSettings settings, @Nullable String directory) throws MissingRequiredKeyException {
        if (section == null) return;

        if (section.get(field) == null) {
            if (settings != null) {
                settings.logError("Missing required field: " + field, directory + "/" + field);
            }
            throw new MissingRequiredKeyException();
        }
    }

    /**
     * Check if a config is null, and if it is, log it against the current settings
     * @param section The section to check for a key
     * @param field The key
     * @param settings The settings to log errors to
     * @param directory The path to the object being configured
     * @param error The error to show if the object does not exist
     * @return True if the field is missing
     */
    public static boolean isNull(ConfigurationSection section,
                                 String field,
                                 @Nullable WbsSettings settings, @Nullable String directory,
                                 @Nullable String error) {
        if (section.get(field) == null) {
            if (settings != null) {
                settings.logError(error, directory);
            }
            return true;
        }

        return false;
    }

}
