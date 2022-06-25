package wbs.utils.util.providers;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.exceptions.MissingRequiredKeyException;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.providers.generator.GeneratorManager;
import wbs.utils.util.providers.generator.num.DoubleGenerator;
import wbs.utils.util.plugin.WbsSettings;

import java.util.Objects;
import java.util.Set;

/**
 * Represents a number (typically a double) that may either be static, or change over time
 */
public class NumProvider implements Provider {

    @Nullable
    private DoubleGenerator generator;
    private boolean staticField = true;
    private double staticValue = Double.MIN_VALUE;

    public NumProvider(NumProvider clone) {
        if (clone.staticField) {
            staticValue = clone.staticValue;
        } else {
            generator = Objects.requireNonNull(clone.generator).clone();
            staticField = false;
        }
    }

    /**
     * Create this provider with a given, pre-configured DoubleGenerator
     * @param generator The DoubleGenerator to generate numbers
     */
    public NumProvider(@NotNull DoubleGenerator generator) {
        Objects.requireNonNull(generator);
        this.generator = generator;
        staticField = false;
    }

    /**
     * Create this provider with a single static value
     * @param staticValue The value to return from {@link #val()}
     */
    public NumProvider(double staticValue) {
        this.staticValue = staticValue;
    }

    /**
     * Create this provider from a configuration section, and log any errors
     * against the given settings.
     * @param section The parent section of the key-pair that contains this provider
     * @param field The key/field where this provider exists inside the given section
     * @param settings The WbsSettings to log errors against
     * @param directory The path taken through the section to reach this provider, for
     *                  logging purposes
     */
    public NumProvider(ConfigurationSection section, String field, WbsSettings settings, String directory) {
        this(section, field, settings, directory, 0);
    }

    /**
     * Create this provider from a configuration section, and log any errors
     * against the given settings.
     * @param section The parent section of the key-pair that contains this provider
     * @param field The key/field where this provider exists inside the given section
     * @param settings The WbsSettings to log errors against
     * @param directory The path taken through the section to reach this provider, for
     *                  logging purposes
     * @param defaultValue The default double to use in case a double exists but is malformed
     */
    public NumProvider(ConfigurationSection section, String field, WbsSettings settings, String directory, double defaultValue) {
        if (section.getDouble(field, Double.MIN_VALUE) != Double.MIN_VALUE) {
            staticField = true;
            staticValue = section.getDouble(field, defaultValue);
        } else {
            WbsConfigReader.requireNotNull(section, field, settings, directory);
            ConfigurationSection providerSection = section.getConfigurationSection(field);
            assert providerSection != null;

            staticField = false;

            Set<String> providerKeys = providerSection.getKeys(false);
            if (providerKeys.isEmpty()) {
                settings.logError("You must specify either a provider or a number.", directory);
                throw new MissingRequiredKeyException();
            } else if (providerKeys.size() > 1) {
                settings.logError("Too many sections. Choose a single provider from the following: "
                        + String.join(", ", GeneratorManager.getRegisteredIds()), directory);
                throw new InvalidConfigurationException();
            }

            String typeString = (String) providerKeys.toArray()[0];

            generator = GeneratorManager.getGenerator(typeString, providerSection.getConfigurationSection(typeString), settings, directory);
        }
    }

    /**
     * Generate a new value
     */
    public void refresh() {
        if (!staticField) {
            assert generator != null;
            generator.refresh();
        }
    }

    /**
     * Get the current value this object represents
     * @return The current value
     */
    public double val() {
        if (staticField) {
            return staticValue;
        }

        assert generator != null;
        return generator.getValue();
    }

    /**
     * Get the current value as an integer
     * @return The current integer value
     */
    public int intVal() {
        if (staticField) {
            return (int) staticValue;
        }

        assert generator != null;
        double generatorVal = generator.getValue();
        if (Double.isInfinite(generatorVal)) {
            if (generatorVal == Double.POSITIVE_INFINITY) return Integer.MAX_VALUE;
            if (generatorVal == Double.NEGATIVE_INFINITY) return Integer.MIN_VALUE;
        }
        return (int) generatorVal;
    }

    /**
     * Save this provider in a config that can be read by the constructor
     * @param section The section to write to
     * @param path The field/path inside the given section
     */
    public void writeToConfig(ConfigurationSection section, String path) {
        if (staticField) {
            section.set(path, staticValue);
        } else {
            assert generator != null;
            generator.writeToConfig(section, path);
        }
    }
}
