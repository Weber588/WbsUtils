package wbs.utils.util.configuration;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.apache.commons.lang.math.NumberRange;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.exceptions.InvalidWorldException;
import wbs.utils.exceptions.MissingRequiredKeyException;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.plugin.WbsSettings;
import wbs.utils.util.string.WbsStrings;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A static class to read configs and automatically provide errors to the WbsSettings object without external logic
 */
@NullMarked
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
    public static void requireNotNull(ConfigurationSection section, String field,
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
    public static void requireSection(ConfigurationSection section, String sectionName,
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
    public static ConfigurationSection getRequiredSection(ConfigurationSection section, String sectionName,
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
     * Get a list of {@link T} from a String list
     * @param section The section to read from
     * @param field The name of the field that contains a String to parse as {@link T}
     * @param settings The settings to log to if a value is invalid for the given enum class
     * @param directory The path to the field for if settings is defined
     * @param clazz The enum class to build from the given String
     * @return A non-null enum value
     */
    public static <T extends Enum<T>> T getRequiredEnum(ConfigurationSection section, String field,
                                                                 @Nullable WbsSettings settings, @Nullable String directory, Class<T> clazz) throws InvalidConfigurationException {
        return Objects.requireNonNull(getEnum(section, field, settings, directory, clazz, true));
    }

    /**
     * Get a list of {@link T} from a String list
     * @param section The section to read from
     * @param field The name of the field that contains a String to parse as {@link T}
     * @param settings The settings to log to if a value is invalid for the given enum class
     * @param directory The path to the field for if settings is defined
     * @param clazz The enum class to build from the given String
     * @return An enum that may be null
     */
    public static <T extends Enum<T>> @Nullable T getEnum(ConfigurationSection section, String field,
                                                                 @Nullable WbsSettings settings, @Nullable String directory, Class<T> clazz) throws InvalidConfigurationException {
        return getEnum(section, field, settings, directory, clazz, false);
    }

    /**
     * Get a list of {@link T} from a String list
     * @param section The section to read from
     * @param field The name of the field that contains a String to parse as {@link T}
     * @param settings The settings to log to if a value is invalid for the given enum class
     * @param directory The path to the field for if settings is defined
     * @param clazz The enum class to build from the given String
     * @param isRequired Whether or not to throw an exception if the field is missing
     * @return An enum that may be null
     */
    private static <T extends Enum<T>> @Nullable T getEnum(ConfigurationSection section, String field,
                                                           @Nullable WbsSettings settings, @Nullable String directory, Class<T> clazz, boolean isRequired) throws InvalidConfigurationException {
        String asString = section.getString(field);

        if (asString == null) {
            if (isRequired) {
                if (settings != null) {
                    settings.logError(WbsStrings.capitalize(field) + " is a required field. Please choose from the following: " +
                            WbsEnums.joiningPrettyStrings(clazz), directory + "/" + field);
                }
                throw new MissingRequiredKeyException();
            } else {
                return null;
            }
        }

        @Nullable
        T instance = WbsEnums.getEnumFromString(clazz, asString);

        if (instance == null) {
            if (settings != null) {
                settings.logError("Invalid " + field + ": " + asString + ". Please choose from the following: " +
                        WbsEnums.joiningPrettyStrings(clazz), directory + "/" + field);
            }
            throw new InvalidConfigurationException();
        }

        return instance;
    }

    /**
     * Get a list of {@link T} from a String list
     * @param section The section to read from
     * @param field The name of the field that contains the String list
     * @param settings The settings to log to if a value is invalid for the given enum class
     * @param directory The path to the field for if settings is defined
     * @param clazz The enum class to build from the given String list
     * @return A list of enum values that may be empty
     */
    public static <T extends Enum<T>> List<T> getEnumList(ConfigurationSection section, String field,
                                                                    @Nullable WbsSettings settings, @Nullable String directory, Class<T> clazz) {
        List<String> asStringList = section.getStringList(field);
        List<T> enumList = new LinkedList<>();
        String chooseFromListPrompt = "Please choose from the following: " + WbsEnums.joiningPrettyStrings(clazz);

        for (String asString : asStringList) {
            @Nullable
            T instance = WbsEnums.getEnumFromString(clazz, asString);

            if (instance == null) {
                if (settings != null) {
                    settings.logError("Invalid " + field + ": " + asString + ". " + chooseFromListPrompt, directory + "/" + field);
                }
                continue;
            }

            enumList.add(instance);
        }

        return enumList;
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
    public static Location getRequiredLocation(ConfigurationSection section, String sectionName,
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

    public static List<@Nullable NamespacedKey> getNamespacedKeys(ConfigurationSection section, String key) {
        return getNamespacedKeys(section, key, null, null);
    }

    public static List<@Nullable NamespacedKey> getNamespacedKeys(ConfigurationSection section,
                                                        String key,
                                                        @Nullable WbsSettings settings, @Nullable String directory
    ) {
        return getNamespacedKeys(section, key, settings, directory, null);
    }
    public static List<@Nullable NamespacedKey> getNamespacedKeys(ConfigurationSection section,
                                                        String key,
                                                        @Nullable WbsSettings settings, @Nullable String directory,
                                                        @Nullable Plugin defaultNamespace) {
        return getNamespacedKeys(section, key, settings, directory, defaultNamespace, new LinkedList<>());
    }

    public static List<@Nullable NamespacedKey> getNamespacedKeys(ConfigurationSection section,
                                                        String key,
                                                        @Nullable WbsSettings settings, @Nullable String directory,
                                                        @Nullable Plugin defaultNamespace,
                                                        List<NamespacedKey> defaultValues) {
        if (!section.isList(key)) {
            NamespacedKey foundKey = getNamespacedKey(section, key);

            if (foundKey != null) {
                List<NamespacedKey> singleEntry = new LinkedList<>();

                singleEntry.add(foundKey);

                return singleEntry;
            } else {
                return defaultValues;
            }

        }

        List<@Nullable NamespacedKey> list = new LinkedList<>();

        for (String keyString : section.getStringList(key)) {
            NamespacedKey namespacedKey = NamespacedKey.fromString(keyString, defaultNamespace);

            if (namespacedKey != null) {
                list.add(namespacedKey);
            }
        }

        return list;
    }

    public static @Nullable NamespacedKey getNamespacedKey(ConfigurationSection section, String key) {
        return getNamespacedKey(section, key, null, null);
    }

    public static @Nullable NamespacedKey getNamespacedKey(ConfigurationSection section,
                                                           String key,
                                                           @Nullable Plugin defaultNamespace) {
        return getNamespacedKey(section, key, null, null, defaultNamespace, null);
    }

    public static @Nullable NamespacedKey getNamespacedKey(ConfigurationSection section,
                                                           String key,
                                                           @Nullable WbsSettings settings, @Nullable String directory,
                                                           @Nullable Plugin defaultNamespace) {
        return getNamespacedKey(section, key, settings, directory, defaultNamespace, null);
    }

    @Contract("_, _, !null -> !null")
    @Nullable
    public static NamespacedKey getNamespacedKey(ConfigurationSection section,
                                                 String key,
                                                 @Nullable NamespacedKey defaultValue
    ) {
        return getNamespacedKey(section, key, null, null, null, defaultValue);
    }

    @Contract("_, _, _, !null -> !null")
    @Nullable
    public static NamespacedKey getNamespacedKey(ConfigurationSection section,
                                                 String key,
                                                 @Nullable Plugin defaultNamespace,
                                                 @Nullable NamespacedKey defaultValue
    ) {
        return getNamespacedKey(section, key, null, null, defaultNamespace, defaultValue);
    }

    @Contract("_, _, _, _, _, !null -> !null")
    @Nullable
    public static NamespacedKey getNamespacedKey(ConfigurationSection section,
                                                 String key,
                                                 @Nullable WbsSettings settings, @Nullable String directory,
                                                 @Nullable Plugin defaultNamespace,
                                                 @Nullable NamespacedKey defaultValue) {
        String asString;
        if (defaultValue != null) {
            asString = section.getString(key, defaultValue.asString());
        } else {
            asString = section.getString(key);
        }

        if (asString == null) {
            return defaultValue;
        }

        NamespacedKey namespacedKey = NamespacedKey.fromString(asString, defaultNamespace);

        if (namespacedKey == null) {
            if (settings != null) {
                settings.logError("Invalid key: %s".formatted(asString), directory + "/" + key);
            }
            return defaultValue;
        }

        return namespacedKey;
    }

    public static <T extends Keyed> List<T> getRegistryEntries(ConfigurationSection section,
                                                               String key,
                                                               RegistryKey<T> registryKey
    ) {
        return getRegistryEntries(section, key, registryKey, null, null);
    }

    public static <T extends Keyed> List<T> getRegistryEntries(ConfigurationSection section,
                                                               String key,
                                                               RegistryKey<T> registryKey,
                                                               List<T> defaultValues
    ) {
        return getRegistryEntries(section, key, registryKey, null, null, defaultValues);
    }

    public static <T extends Keyed> List<T> getRegistryEntries(ConfigurationSection section,
                                                               String key,
                                                               RegistryKey<T> registryKey,
                                                               @Nullable WbsSettings settings, @Nullable String directory
    ) {
        return getRegistryEntries(section, key, registryKey, settings, directory, new LinkedList<>());
    }

    public static <T extends Keyed> List<T> getRegistryEntries(ConfigurationSection section,
                                                               String key,
                                                               RegistryKey<T> registryKey,
                                                               @Nullable WbsSettings settings, @Nullable String directory,
                                                               List<T> defaultValues
    ) {
        if (!section.isList(key)) {
            T foundEntry = getRegistryEntry(section, key, registryKey);

            if (foundEntry != null) {
                List<T> singleEntry = new LinkedList<>();

                singleEntry.add(foundEntry);

                return singleEntry;
            } else {
                return defaultValues;
            }
        }

        List<T> entries = new LinkedList<>();

        for (NamespacedKey namespacedKey : WbsConfigReader.getNamespacedKeys(section, key)) {
            @Nullable
            T registryEntry = RegistryAccess.registryAccess().getRegistry(registryKey).get(namespacedKey);

            if (registryEntry != null) {
                entries.add(registryEntry);
            } else {
                if (settings != null) {
                    settings.logError("Invalid key for registry %s: %s".formatted(registryKey.key().asString(), namespacedKey.asString()), directory + "/" + key);
                }
            }
        }

        return entries;
    }

    @Nullable
    public static <T extends Keyed> T getRegistryEntry(ConfigurationSection section, String key, RegistryKey<T> registryKey) {
        return getRegistryEntry(section, key, registryKey, null, null);
    }

    @Contract("_, _, _, !null -> !null")
    @Nullable
    public static <T extends Keyed> T getRegistryEntry(ConfigurationSection section,
                                                       String key,
                                                       RegistryKey<T> registryKey,
                                                       @Nullable T defaultValue
    ) {
        return getRegistryEntry(section, key, registryKey, null, null, defaultValue);
    }

    @Nullable
    public static <T extends Keyed> T getRegistryEntry(ConfigurationSection section,
                                                       String key,
                                                       RegistryKey<T> registryKey,
                                                       @Nullable WbsSettings settings, @Nullable String directory
    ) {
        return getRegistryEntry(section, key, registryKey, settings, directory, null);
    }

    @Contract("_, _, _, _, _, !null -> !null")
    @Nullable
    public static <T extends Keyed> T getRegistryEntry(ConfigurationSection section,
                                                       String key,
                                                       RegistryKey<T> registryKey,
                                                       @Nullable WbsSettings settings, @Nullable String directory,
                                                       @Nullable T defaultValue) {
        NamespacedKey blockKey = WbsConfigReader.getNamespacedKey(section, key, null, defaultValue != null ? defaultValue.getKey() : null);
        if (blockKey == null) {
            return null;
        }

        @Nullable
        T registryEntry = RegistryAccess.registryAccess().getRegistry(registryKey).get(blockKey);

        if (registryEntry == null) {
            return defaultValue;
        }

        return registryEntry;
    }

    @Nullable
    public static Vector getVector(ConfigurationSection section, String key) {
        return getVector(section, key, null);
    }

    @Contract("_, _, !null -> !null")
    @Nullable
    public static Vector getVector(ConfigurationSection section, String key, @Nullable Vector defaultValue) {
        ConfigurationSection vectorSection = section.getConfigurationSection(key);

        if (vectorSection == null) {
            if (section.isDouble(key)) {
                double allValues = section.getDouble(key);

                return new Vector(allValues, allValues, allValues);
            }

            return defaultValue;
        }

        if (defaultValue == null) {
            defaultValue = new Vector(0, 0, 0);
        }

        double x = vectorSection.getDouble("x", defaultValue.getX());
        double y = vectorSection.getDouble("y", defaultValue.getY());
        double z = vectorSection.getDouble("z", defaultValue.getZ());

        return new Vector(x, y, z);
    }

    public static NumberRange getNumberRange(ConfigurationSection section, String key) {
        return getNumberRange(section, key, null);
    }

    public static NumberRange getNumberRange(ConfigurationSection section, String key, @Nullable NumberRange defaultValue) {
        if (defaultValue == null) {
            defaultValue = new NumberRange(Long.MIN_VALUE, Long.MAX_VALUE);
        }

        Number min = defaultValue.getMinimumNumber();
        Number max = defaultValue.getMaximumNumber();
        if (section.isConfigurationSection(key)) {
            ConfigurationSection asSection = section.getConfigurationSection(key);
            if (asSection != null) {
                min = getNumber(section, "min", min);
                max = getNumber(section, "max", max);
            }
        } else if (section.isString(key)) {
            String string = section.getString(key);

            try {
                min = NumberFormat.getInstance().parse(string);
            } catch (ParseException ex) {
                if (string != null) {
                    String[] args = string.split("-");
                    if (args.length >= 2) {
                        min = getNumber(min, args[0]);
                        max = getNumber(max, args[1]);
                    }
                }
            }
        }

        try {
            return new NumberRange(min, max);
        } catch (IllegalArgumentException ex) {
            return defaultValue;
        }
    }

    @Nullable
    public static Number getNumber(ConfigurationSection section, String key) {
        return getNumber(section, key, null);
    }

    @Contract("_, _, !null -> !null")
    @Nullable
    public static Number getNumber(ConfigurationSection section, String key, @Nullable Number defaultValue) {
        return getNumber(defaultValue, section.getString(key));
    }

    @Contract("!null, _ -> !null")
    @Nullable
    private static Number getNumber(@Nullable Number defaultValue, @Nullable String asString) {
        if (asString == null) {
            return defaultValue;
        }
        try {
            defaultValue = NumberFormat.getInstance().parse(asString);
        } catch (ParseException ignored) {}

        return defaultValue;
    }

    @Nullable
    public static Boolean getBoolean(ConfigurationSection section, String ... aliases) {
        return getWithAliases(section, ConfigurationSection::isBoolean, ConfigurationSection::getBoolean, aliases);
    }

    @Nullable
    public static Integer getInt(ConfigurationSection section, String ... aliases) {
        return getWithAliases(section, ConfigurationSection::isInt, ConfigurationSection::getInt, aliases);
    }

    @Nullable
    public static <T> T getWithAliases(ConfigurationSection section, BiPredicate<ConfigurationSection, String> check, BiFunction<ConfigurationSection, String, T> getter, String ... aliases) {
        for (String alias : aliases) {
            if (check.test(section, alias)) {
                return getter.apply(section, alias);
            }
        }

        return null;
    }
}
