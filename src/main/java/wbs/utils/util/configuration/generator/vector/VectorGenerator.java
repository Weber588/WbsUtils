package wbs.utils.util.configuration.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.configuration.generator.num.*;
import wbs.utils.util.plugin.WbsSettings;

public abstract class VectorGenerator {

    public enum VectorGeneratorType {

        ADD, SUB, MUL, NORMALISE,
        CROSS, ROTATE
    }

    private VectorGenerator() {}

    public VectorGenerator(ConfigurationSection section, WbsSettings settings, String directory) {}

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

    public abstract void writeToConfig(ConfigurationSection section, String path);

    private Vector value;

    /**
     * Generate a new value
     */
    public void refresh() {
        value = getNewValue();
    }

    public Vector getValue() {
        return value.clone();
    }


}
