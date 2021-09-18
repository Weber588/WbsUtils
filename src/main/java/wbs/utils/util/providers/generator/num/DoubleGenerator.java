package wbs.utils.util.providers.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A generator that returns a single double until {@link #refresh()} is called,
 * at which point it calculates a new value.
 */
public abstract class DoubleGenerator {

    /**
     * Represents the subclasses of this generator.
     */
    public enum GeneratorType {
        // Actual generators that do functions
        PULSE, RANDOM, CYCLE, PINGPONG,
        // Math generators that take multiple providers as args
        ADD, SUB, MUL, DIV, MOD,
        ABS, MIN, MAX, CLAMP
    }

    protected DoubleGenerator() {}

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public DoubleGenerator(ConfigurationSection section, WbsSettings settings, String directory) {}

    /**
     * Get a DoubleGenerator based on a configuration section
     * @param typeString The name of the generator type
     * @param section The section to read from
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     * @return The built DoubleGenerator
     */
    public static DoubleGenerator buildGenerator(String typeString, ConfigurationSection section, WbsSettings settings, String directory) {
        GeneratorType type;

        type = WbsEnums.getEnumFromString(GeneratorType.class, typeString);

        if (type == null) {
            settings.logError("Invalid provider type: " + typeString, directory);
            throw new InvalidConfigurationException("Invalid provider type: " + typeString);
        }

        switch (type) {
            case PULSE:
                return new PulseGenerator(section, settings, directory);
            case RANDOM:
                return new RandomGenerator(section, settings, directory);
            case CYCLE:
                return new CycleGenerator(section, settings, directory);
            case PINGPONG:
                return new PingPongGenerator(section, settings, directory);

            case ADD:
                return new AdditionGenerator(section, settings, directory);
            case SUB:
                return new SubtractionGenerator(section, settings, directory);
            case MUL:
                return new MultiplicationGenerator(section, settings, directory);
            case DIV:
                return new DivisionGenerator(section, settings, directory);

            case MOD:
                return new ModuloGenerator(section, settings, directory);
            case ABS:
                return new AbsGenerator(section, settings, directory);
            case MIN:
                return new MinGenerator(section, settings, directory);
            case MAX:
                return new MaxGenerator(section, settings, directory);
            case CLAMP:
                return new ClampGenerator(section, settings, directory);
        }

        return null;
    }

    /**
     * Generate a new value to override the current one
     * @return The new value
     */
    protected abstract double getNewValue();

    /**
     * Save this generator in a config that can be read by its constructor
     * @param section The section to write to
     * @param path The field/path inside the given section
     */
    public abstract void writeToConfig(ConfigurationSection section, String path);

    private double value;

    /**
     * Generate a new value.
     */
    public final void refresh() {
        refreshInternal();
        value = getNewValue();
    }

    protected void refreshInternal() {}

    /**
     * @return The most recently generated value
     */
    public double getValue() {
        return value;
    }

    public abstract DoubleGenerator clone();
}
