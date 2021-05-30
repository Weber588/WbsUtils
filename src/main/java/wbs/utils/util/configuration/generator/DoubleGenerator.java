package wbs.utils.util.configuration.generator;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.plugin.WbsSettings;

public abstract class DoubleGenerator {

    public enum GeneratorType {
        // Actual generators that do functions
        PULSE, RANDOM, CYCLE, PINGPONG,
        // Math generators that take multiple providers as args
        ADD, SUB, MUL, DIV, MOD,
        ABS, MIN, MAX, CLAMP
    }

    protected DoubleGenerator() {}

    public DoubleGenerator(ConfigurationSection section, WbsSettings settings, String directory) {}

    public static DoubleGenerator buildGenerator(String typeString, ConfigurationSection section, WbsSettings settings, String directory) {
        GeneratorType type;
        try {
            type = WbsEnums.getEnumFromString(GeneratorType.class, typeString);
        } catch (IllegalArgumentException e) {
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

    protected abstract double getNewValue();

    public abstract void writeToConfig(ConfigurationSection section, String path);

    private double value;

    /**
     * Generate a new value
     */
    public void refresh() {
        value = getNewValue();
    }

    public double getValue() {
        return value;
    }

}
