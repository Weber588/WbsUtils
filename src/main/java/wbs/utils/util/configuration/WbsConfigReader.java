package wbs.utils.util.configuration;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.exceptions.InvalidWorldException;
import wbs.utils.exceptions.MissingRequiredKeyException;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A static class to read configs and automatically provide errors to the WbsSettings object without external logic
 */
@SuppressWarnings("unused")
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
    public static void requireNotNull(@NotNull ConfigurationSection section, String field,
                                      @Nullable WbsSettings settings, @Nullable String directory) throws MissingRequiredKeyException {
        if (section.get(field) == null) {
            if (settings != null) {
                settings.logError("Missing required field: " + field, directory + "/" + field);
            }
            throw new MissingRequiredKeyException();
        }
    }

    /**
     * @param section The parent section to check
     * @param sectionName The name of the field to verify exists, and is a section
     * @param settings The settings to log to if the target section is missing
     * @param directory The path to the section if settings is defined
     * @throws MissingRequiredKeyException If the field does not exist, or is not a section
     */
    public static void requireSection(@NotNull ConfigurationSection section, String sectionName,
                                      @Nullable WbsSettings settings, @Nullable String directory) throws MissingRequiredKeyException {
        if (section.getConfigurationSection(sectionName) == null) {
            if (settings != null) {
                settings.logError(sectionName + " must be a section, or empty by setting it to \"{}\"", directory + "/" + sectionName);
            }
            throw new MissingRequiredKeyException();
        }
    }

    /**
     * Get a required section from a ConfigurationSection, and throw an error if missing.
     * @param section The parent section
     * @param sectionName The name of the section to retrieve
     * @param settings The settings to log to if the target section is missing
     * @param directory The path to the section if settings is defined
     * @return The found section, guaranteed not to be null
     */
    public static @NotNull ConfigurationSection getRequiredSection(@NotNull ConfigurationSection section, String sectionName,
                                                                   @Nullable WbsSettings settings, @Nullable String directory) throws MissingRequiredKeyException {
        ConfigurationSection foundSection = section.getConfigurationSection(sectionName);
        if (foundSection == null) {
            if (settings != null) {
                settings.logError(sectionName + " must be a section, or empty by setting it to \"{}\"", directory + "/" + sectionName);
            }
            throw new MissingRequiredKeyException();
        }

        return foundSection;
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

    /**
     * Get a non-null location given the name of a config section, and automatically
     * log errors to the provided settings if non-null.
     * @param section The section to read from
     * @param sectionName The name of the subsection to read from within section
     * @param settings The settings to log errors to automatically, if provided
     * @param directory The path taken to the section to read from, for logging purposes only
     * @return The non-null location found.
     * @throws MissingRequiredKeyException If a field (sectionName, x, y, z, or world) is missing
     * @throws InvalidWorldException If the world field was set, but is not a valid/loaded world.
     */
    public static @NotNull Location getRequiredLocation(@NotNull ConfigurationSection section, String sectionName,
                                                                   @Nullable WbsSettings settings, @Nullable String directory) throws MissingRequiredKeyException, InvalidWorldException {
        ConfigurationSection locSection = getRequiredSection(section, sectionName, settings, directory);

        requireNotNull(locSection, "x", settings, directory);
        requireNotNull(locSection, "y", settings, directory);
        requireNotNull(locSection, "z", settings, directory);
        requireNotNull(locSection, "world", settings, directory);

        int x = locSection.getInt("x");
        int y = locSection.getInt("y");
        int z = locSection.getInt("z");
        String worldName = locSection.getString("world");

        assert worldName != null;
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            if (settings != null) {
                settings.logError("Invalid world: " + worldName, directory);
            }
            throw new InvalidWorldException();
        }

        return new Location(world, x, y, z);
    }

}
