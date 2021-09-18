package wbs.utils.util.providers.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A generator that returns a single Vector until {@link #refresh()} is called,
 * at which point it calculates a new value.
 */
public abstract class VectorGenerator {

    /**
     * Represents the subclasses of this generator.
     */
    public enum VectorGeneratorType {
        ADD, SUB, MUL, NORMALISE,
        CROSS, ROTATE
    }

    protected VectorGenerator() {}

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public VectorGenerator(ConfigurationSection section, WbsSettings settings, String directory) {}

    /**
     * Get a VectorGenerator based on a configuration section
     * @param typeString The name of the generator type
     * @param section The section to read from
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     * @return The built VectorGenerator
     */
    public static VectorGenerator buildGenerator(String typeString, ConfigurationSection section, WbsSettings settings, String directory) {
        VectorGeneratorType type = WbsEnums.getEnumFromString(VectorGeneratorType.class, typeString);

        if (type == null) {
            settings.logError("Invalid provider type: " + typeString, directory);
            throw new InvalidConfigurationException();
        }

        switch (type) {
            case ROTATE:
                return new VectorGeneratorRotate(section, settings, directory);

            case ADD:
                return new VectorGeneratorAdd(section, settings, directory);
            case SUB:
                return new VectorGeneratorSub(section, settings, directory);
            case MUL:
                return new VectorGeneratorMul(section, settings, directory);
            case NORMALISE:
                return new VectorGeneratorNormalise(section, settings, directory);
            case CROSS:
                return new VectorGeneratorCross(section, settings, directory);
        }

        return null;
    }

    protected abstract Vector getNewValue();


    /**
     * Save this generator in a config that can be read by its constructor
     * @param section The section to write to
     * @param path The field/path inside the given section
     */
    public abstract void writeToConfig(ConfigurationSection section, String path);

    private Vector value;

    /**
     * Generate a new value
     */
    public final void refresh() {
        refreshInternal();
        value = getNewValue();
    }

    protected void refreshInternal() {}

    /**
     * @return A clone of the most recently generated value
     */
    public Vector getValue() {
        return value.clone();
    }

    @Override
    public abstract VectorGenerator clone();
}
