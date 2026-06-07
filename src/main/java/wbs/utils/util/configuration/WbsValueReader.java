package wbs.utils.util.configuration;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.apache.commons.lang3.NumberRange;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.plugin.WbsSettings;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@NullMarked
public class WbsValueReader {
    public static final String DEFAULT_REQUIRED_VALUE_ERROR = "\"%s\" is a required value.";

    @Nullable
    private WbsSettings settings;
    private boolean isRequired = false;
    private String requiredValueString = DEFAULT_REQUIRED_VALUE_ERROR;
    private ConfigurationSection section;
    private String key;
    @Nullable
    private String sectionDirectory;

    public WbsValueReader(ConfigurationSection section, String key, @Nullable String sectionDirectory) {
        updateSection(section, key, sectionDirectory);
    }

    @SuppressWarnings("CopyConstructorMissesField") // Just can't see section, key, sectionDirectory
    public WbsValueReader(WbsValueReader clone) {
        this(clone.section, clone.key, clone.sectionDirectory);

        this.settings = clone.settings;
        this.isRequired = clone.isRequired;
        this.requiredValueString = clone.requiredValueString;
    }

    @Contract("_ -> new")
    public WbsValueReader getChildReader(String sectionKey) {
        return getChildReader(sectionKey, null);
    }
    @Contract("_, _ -> new")
    public WbsValueReader getChildReader(String sectionKey, @Nullable String defaultKey) {
        String currentKey = this.key;
        updateKey(sectionKey);

        ConfigurationSection childSection = readSection();
        if (defaultKey == null) {
            Set<String> keys = childSection.getKeys(false);
            if (keys.isEmpty()) {
                throw new InvalidConfigurationException("Comparison section must have fields when no defaultKey is provided.", directory());
            }

            defaultKey = keys.stream().findFirst().orElseThrow();
        }

        WbsValueReader childReader = new WbsValueReader(this).updateSection(childSection, defaultKey, directory());

        updateKey(currentKey);

        return childReader;
    }

    //region Config

    public WbsValueReader updateSection(ConfigurationSection section, String key, @Nullable String sectionDirectory) {
        this.section = section;
        this.key = key;
        this.sectionDirectory = sectionDirectory;

        return this;
    }

    public WbsValueReader updateKey(String key) {
        this.key = key;
        return this;
    }

    public String key() {
        return key;
    }

    public ConfigurationSection section() {
        return section;
    }

    public @Nullable String directory() {
        return sectionDirectory != null ? sectionDirectory + "/" + key : null;
    }

    public String getFormattedRequiredValueString() {
        return requiredValueString.formatted(key);
    }

    @SuppressWarnings("SameParameterValue")
    @Contract(mutates = "this")
    public WbsValueReader setRequiredValueString(String requiredValueString) {
        this.requiredValueString = requiredValueString;
        return this;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public WbsValueReader isRequired(boolean required) {
        isRequired = required;
        return this;
    }

    public @Nullable WbsSettings settings() {
        return settings;
    }

    public WbsValueReader settings(@Nullable WbsSettings settings) {
        this.settings = settings;
        return this;
    }

    //endregion
    
    //region Helper methods

    @Nullable
    @Contract("-> null")
    private <T> T nullOrThrow() throws InvalidConfigurationException {
        return nullOrThrow(null);
    }

    /// @return `null`
    /// @param <T> An arbitrary type determined by the caller automatically -- the value is always null.
    /// @throws InvalidConfigurationException If isRequired is true
    @SuppressWarnings("SameParameterValue")
    @Nullable
    @Contract("_ -> null")
    private <T> T nullOrThrow(@Nullable String customRequiredError) throws InvalidConfigurationException {
        return validateNullability(null, customRequiredError);
    }

    @UnknownNullability
    @Contract("!null -> !null")
    public <T> T validateNullability(@Nullable T value) throws InvalidConfigurationException {
        return validateNullability(value, null);
    }

    /// If isRequired is true, validates that the given value is non-null, and returns it. Otherwise, an exception is thrown.
    ///
    /// If isRequired is false, returns value.
    /// @param value The value to check for nullness
    /// @return value
    /// @param <T> The type of the given value
    /// @throws InvalidConfigurationException If value is null, and isRequired is true
    @UnknownNullability
    @Contract("!null, _ -> !null")
    public <T> T validateNullability(@Nullable T value, @Nullable String customRequiredError) throws InvalidConfigurationException {
        if (isRequired) {
            if (value == null) {
                throw new InvalidConfigurationException(customRequiredError != null ? customRequiredError : requiredValueString, directory());
            }
        }

        return value;
    }

    /// Validates that a parsed value is not null, handling isRequired and logging as needed. If value is not null,
    /// it will be returned immediately.
    ///
    /// This method is the same as {@link WbsValueReader#validateNullability(Object, String)}, except that it will log a
    /// caller-provided error if the value is present on the section, but value provided was still null.
    /// @param value The value read from the section.
    /// @param errorIfKeyExists A custom error message to display if the key is present on the section, but value
    ///     given was null.
    /// @return value
    /// @param <T> The type of value to return
    /// @throws InvalidConfigurationException If value was null and isRequired is true. The error will depend on whether
    ///     the key was present in the section.
    @UnknownNullability
    @Contract("!null, _, _ -> !null")
    public <T> T validateNullability(@Nullable T value, @Nullable String customRequiredError, String errorIfKeyExists) throws InvalidConfigurationException {
        if (value != null) {
            return value;
        }

        String error;
        if (!section.contains(key)) {
            error = customRequiredError != null ? customRequiredError : requiredValueString;
        } else {
            error = errorIfKeyExists;
        }

        if (isRequired) {
            throw new InvalidConfigurationException(error, directory());
        } else {
            log(error);
            return null;
        }
    }

    /// Validates that a parsed value is not null, handling isRequired and logging as needed. If value is not null,
    /// it will be returned immediately.
    ///
    /// If isRequired = true, returns a non-null defaultValue, or throws an exception
    ///
    /// If isRequired = false, always returns defaultValue, but logs the error without any exceptions.
    /// @param value The value to return if parsing was successful.
    /// @param errorIfNull The error to display or throw if parsing failed (i.e. value is null)
    /// @param defaultValue The default to use. If value was null, the return value will always be defaultValue unless an exception is thrown.
    /// @return A validated object that may only be null if isRequired = false
    /// @throws InvalidConfigurationException If both value and defaultValue is null and the reader requires a value.
    /// @param <T> The return type.
    @UnknownNullability
    @Contract(value = "!null, _, _ -> !null; null, _, !null -> !null; null, _, null -> null", pure = true)
    private <T> T validateParsing(@Nullable T value, String errorIfNull, @Nullable T defaultValue) throws InvalidConfigurationException {
        if (value != null) {
            return value;
        }

        if (defaultValue == null) {
            if (isRequired) {
                throw new InvalidConfigurationException(errorIfNull, directory());
            }

            // Not required, so just log that it failed to parse, and return null
            log(errorIfNull);
        }

        return defaultValue;
    }

    private void log(String error) {
        log(error, directory());
    }

    private void log(InvalidConfigurationException ex) {
        log(ex.getMessage(), ex.getDirectory());
    }
    private void log(String error, @Nullable String directory) {
        if (settings != null) {
            settings.logError(error, directory);
        }
    }
    
    public boolean isNumber() {
        return section.get(key) instanceof Number;
    }

    private @Nullable String getSubdirectory(String childKey) {
        String directory = directory();
        return directory == null ? null : directory + "/" + childKey;
    }

    @UnknownNullability
    public <T> T readFromChildSection(String sectionKey, String key, Function<WbsValueReader, @UnknownNullability T> function) {
        String currentKey = this.key;
        updateKey(sectionKey);
        ConfigurationSection childSection = readSection();
        updateKey(currentKey);

        return readFromOtherSection(childSection, key, getSubdirectory(key), function);
    }
    @UnknownNullability
    public <T> T readFromChildSection(ConfigurationSection section, String key, Function<WbsValueReader, @UnknownNullability T> function) {
        return readFromOtherSection(section, key, getSubdirectory(key), function);
    }
    @UnknownNullability
    public <T> T readFromOtherSection(ConfigurationSection section, String key, @Nullable String sectionDirectory, Function<WbsValueReader, @UnknownNullability T> function) {
        ConfigurationSection currentSection = this.section;
        String currentDirectory = this.sectionDirectory;
        String currentKey = this.key;

        this.section = section;
        this.sectionDirectory = sectionDirectory;
        this.key = key;

        T value = function.apply(this);

        this.section = currentSection;
        this.sectionDirectory = currentDirectory;
        this.key = currentKey;

        return value;
    }
    
    //endregion

    //region Read methods

    @UnknownNullability
    public Object read() {
        Object value = section.get(key);

        return validateNullability(value);
    }

    @UnknownNullability
    public ConfigurationSection readSection() {
        ConfigurationSection value = section.getConfigurationSection(key);

        return validateNullability(value);
    }

    @UnknownNullability
    public <T> T construct(ConfigConstructor<T> constructor) {
        return validateNullability(constructor.construct(section, key, settings, directory()));
    }

    @UnknownNullability
    public <T> T constructFrom(RegexRoutedConstructorManager<T> constructorManager) {
        return constructFrom(constructorManager, key);
    }

    @UnknownNullability
    public <T> T constructFrom(RegexRoutedConstructorManager<T> constructorManager, String constructorTypeKey) {
        ConfigConstructor<T> constructor = constructorManager.getConstructor(constructorTypeKey);
        validateNullability(constructor);

        if (constructor == null) {
            return null;
        }

        return construct(constructor);
    }

    @UnknownNullability
    public <E extends Enum<E>> E readEnum(Class<E> clazz) {
        return readEnum(clazz, null);
    }
    @UnknownNullability
    public <E extends Enum<E>> E readEnum(Class<E> clazz, @Nullable E defaultValue) {
        String asString = section.getString(key);

        if (asString == null) {
            return validateNullability(defaultValue, key + " is a required field. Please choose from the following: " +
                    WbsEnums.joiningPrettyStrings(clazz));
        }

        E instance = WbsEnums.getEnumFromString(clazz, asString);

        return validateParsing(instance, "\"" + asString + "\" is not a valid value for " + clazz.getName() + ". Please choose from the following: " +
                WbsEnums.joiningPrettyStrings(clazz), defaultValue);
    }

    @UnknownNullability
    public NamespacedKey readNamespacedKey(@Nullable Plugin defaultNamespace) {
        return readNamespacedKey(defaultNamespace, (NamespacedKey) null);
    }
    @Contract("_, !null -> !null")
    @UnknownNullability
    public NamespacedKey readNamespacedKey(@Nullable Plugin defaultNamespace,
                                           @Nullable Keyed defaultKeyHolder) {
        return readNamespacedKey(defaultNamespace, defaultKeyHolder != null ? defaultKeyHolder.getKey() : null);
    }
    @Contract("_, !null -> !null")
    @UnknownNullability
    public NamespacedKey readNamespacedKey(@Nullable Plugin defaultNamespace,
                                           @Nullable NamespacedKey defaultValue) {
        String asString = section.getString(key);

        if (asString == null) {
            return validateNullability(defaultValue);
        }

        NamespacedKey namespacedKey = parseNamespacedKey(asString, defaultNamespace);

        return validateParsing(namespacedKey, "Invalid namespaced key: %s".formatted(asString), defaultValue);
    }

    public static @Nullable NamespacedKey parseNamespacedKey(@Nullable String asString, @Nullable Plugin defaultNamespace) {
        if (asString == null) {
            return null;
        }
        return NamespacedKey.fromString(asString, defaultNamespace);
    }

    @UnknownNullability
    public <T extends Keyed> T readRegistryEntry(RegistryKey<T> registryKey) {
        return readRegistryEntry(registryKey, null);
    }
    @Contract("_, !null -> !null")
    @UnknownNullability
    public <T extends Keyed> T readRegistryEntry(RegistryKey<T> registryKey, @Nullable T defaultValue) {
        String asString = section.getString(key);

        if (asString == null) {
            return validateNullability(defaultValue);
        }

        NamespacedKey namespacedKey = readNamespacedKey(null, defaultValue);

        if (namespacedKey == null) {
            // defaultValue is always null here -- if it wasn't, its key would've been returned above
            return nullOrThrow();
        }

        T registryEntry = parseRegistryEntry(registryKey, namespacedKey);

        return validateParsing(
                registryEntry,
                "Invalid key for registry %s: %s".formatted(registryKey.key().asString(), namespacedKey.asString()),
                defaultValue
        );
    }

    public static <T extends Keyed> @Nullable T parseRegistryEntry(RegistryKey<T> registryKey, @Nullable NamespacedKey namespacedKey) {
        if (namespacedKey == null) {
            return null;
        }
        return RegistryAccess.registryAccess().getRegistry(registryKey).get(namespacedKey);
    }

    @UnknownNullability
    public Vector readVector() {
        return readVector(null);
    }
    
    @Contract("!null -> !null")
    @UnknownNullability
    public Vector readVector(@Nullable Vector defaultValue) {
        return readVector(defaultValue, "x", "y", "z");
    }

    @Contract("!null, _, _, _ -> !null")
    @UnknownNullability
    public Vector readVector(@Nullable Vector defaultValue, String xName, String yName, String zName) {
        ConfigurationSection vectorSection = section.getConfigurationSection(key);

        if (vectorSection == null) {
            if (isNumber()) {
                double allValues = section.getDouble(key);

                return new Vector(allValues, allValues, allValues);
            }

            return validateNullability(defaultValue);
        }

        if (defaultValue == null) {
            defaultValue = new Vector(0, 0, 0);
        }

        double x = vectorSection.getDouble(xName, defaultValue.getX());
        double y = vectorSection.getDouble(yName, defaultValue.getY());
        double z = vectorSection.getDouble(zName, defaultValue.getZ());

        return new Vector(x, y, z);
    }


    @UnknownNullability
    public Number readNumber() {
        return readNumber(null);
    }

    @Contract("!null -> !null")
    @UnknownNullability
    public Number readNumber(@Nullable Number defaultValue) {
        return validateNullability(parseNumber(defaultValue, section.getString(key)));
    }

    @Contract("!null, _ -> !null")
    @UnknownNullability
    private Number parseNumber(@Nullable Number defaultValue, @Nullable String asString) {
        if (asString == null) {
            return validateNullability(defaultValue);
        }

        Number value = null;
        String customError = "Invalid number: " + asString;

        try {
            value = NumberFormat.getInstance().parse(asString);
        } catch (ParseException ex) {
            customError = ex.getMessage();
        }

        return validateParsing(value, customError, defaultValue);
    }

    @UnknownNullability
    public NumberRange<Number> readNumberRange(@Nullable NumberRange<Number> defaultValue) {
        if (defaultValue == null) {
            defaultValue = new NumberRange<>(Long.MIN_VALUE, Long.MAX_VALUE, null);
        }

        NumberRange<Number> finalDefaultValue = defaultValue;
        Number min = defaultValue.getMinimum();
        Number max = defaultValue.getMaximum();
        if (section.isConfigurationSection(key)) {
            ConfigurationSection asSection = section.getConfigurationSection(key);
            if (asSection != null) {
                min = readFromChildSection(asSection, "min", reader -> reader.readNumber(finalDefaultValue.getMinimum()));
                max = readFromChildSection(asSection, "max", reader -> reader.readNumber(finalDefaultValue.getMaximum()));
            }
        } else if (section.isString(key)) {
            String string = section.getString(key);

            try {
                min = NumberFormat.getInstance().parse(string);
            } catch (ParseException ex) {
                if (string != null) {
                    String[] args = string.split("\\s?to\\s?");
                    if (args.length >= 2) {
                        min = parseNumber(min, args[0]);
                        max = parseNumber(max, args[1]);
                    }
                }
            }
        }

        try {
            return new NumberRange<>(min, max, null);
        } catch (IllegalArgumentException ex) {
            return defaultValue; // defaultValue is non-null at this point
        }
    }

    @UnknownNullability
    public String readString() {
        return validateNullability(section.getString(key));
    }

    @Contract("!null -> !null")
    @UnknownNullability
    public ItemStack readItem(@Nullable ItemStack defaultValue) {
        if (section.isItemStack(key)) {
            return validateNullability(section.getItemStack(key, defaultValue));
        }

        String asString = section.getString(key);

        if (asString == null) {
            return validateNullability(defaultValue);
        }

        ItemStack stack;
        try {
            stack = Bukkit.getItemFactory().createItemStack(asString);
        } catch (IllegalArgumentException ex) {
            log("Failed to parse item stack ", ex.getMessage());
            return validateNullability(defaultValue);
        }

        return validateNullability(stack);
    }

    @UnknownNullability
    public ShapelessRecipe readShapelessRecipe(NamespacedKey recipeKey, ItemStack result) {
        if (section.isList(key)) {
            List<ItemType> ingredients = getListFromType(true, true, true, String.class, asString ->
                    parseRegistryEntry(RegistryKey.ITEM, parseNamespacedKey(asString, null))
            );

            ShapelessRecipe recipe = new ShapelessRecipe(recipeKey, result);


            // TODO :Full item support
            for (ItemType ingredient : ingredients) {
                recipe.addIngredient(ingredient.createItemStack());
            }
            return recipe;
        }

        ConfigurationSection childSection = readSection();
        return nullOrThrow();
    }

    @UnknownNullability
    public <I, T> List<T> getListFromType(boolean allowNullEntries, boolean treatExceptionsAsNull, boolean logErrors, Class<I> iClass, Function<@NotNull I, @Nullable T> reader) {
        return getList(allowNullEntries, treatExceptionsAsNull, logErrors, o -> {
            if (iClass.isAssignableFrom(o.getClass())) {
                return reader.apply(iClass.cast(o));
            }
            return null;
        });
    }
    @UnknownNullability
    public <T> List<T> getList(boolean bypassNullabilityCheck, boolean treatExceptionsAsNull, boolean logErrors, Function<@NotNull Object, @Nullable T> reader) {
        List<?> list = section.getList(key);
        
        if (list == null) {
            return nullOrThrow("Must be a list.");
        }
        
        List<@UnknownNullability T> parsedList = new LinkedList<>();

        for (Object object : list) {
            T parsed = null;

            if (treatExceptionsAsNull) {
                try {
                    parsed = reader.apply(object);
                } catch (InvalidConfigurationException ex) {
                    if (logErrors) {
                        log(ex);
                    }
                }
            } else {
                parsed = reader.apply(object);
            }
            
            if (!bypassNullabilityCheck) {
                parsed = validateNullability(parsed);
            }
            
            parsedList.add(parsed);
        }

        return parsedList;
    }

    //endregion
}
