package wbs.utils.util.providers.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.providers.VectorProvider;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A generator that accepts a VectorProvider and normalizes it
 */
public class VectorGeneratorNormalise extends VectorGenerator {

    private final VectorProvider value;

    public VectorGeneratorNormalise(VectorGeneratorNormalise clone) {
        value = new VectorProvider(clone.value);
    }

    public VectorGeneratorNormalise(VectorProvider value) {
        this.value = value;
    }

    public VectorGeneratorNormalise(Vector value) {
        this.value = new VectorProvider(value);
    }

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public VectorGeneratorNormalise(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory);

        String normName = (String) section.getKeys(false).toArray()[0];

        ConfigurationSection normaliseSection = section.getConfigurationSection(normName);
        value = new VectorProvider(normaliseSection, settings, directory);
    }

    @Override
    protected Vector getNewValue() {
        return value.val().normalize();
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        value.writeToConfig(section, path);
    }

    @Override
    public VectorGeneratorNormalise clone() {
        return new VectorGeneratorNormalise(this);
    }
}
