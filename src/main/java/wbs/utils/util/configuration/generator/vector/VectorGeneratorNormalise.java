package wbs.utils.util.configuration.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.VectorProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

public class VectorGeneratorNormalise extends VectorGenerator {

    private VectorProvider value;

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
}
