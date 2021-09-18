package wbs.utils.util.providers.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.providers.VectorProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

public abstract class VectorBinaryFunctionalGenerator extends VectorGenerator {

    VectorProvider a, b;

    public VectorBinaryFunctionalGenerator() {}

    public VectorBinaryFunctionalGenerator(VectorBinaryFunctionalGenerator clone) {
        a = new VectorProvider(clone.a);
        b = new VectorProvider(clone.b);
    }

    public VectorBinaryFunctionalGenerator(VectorProvider a, VectorProvider b) {
        this.a = a;
        this.b = b;
    }

    public VectorBinaryFunctionalGenerator(Vector a, Vector b) {
        this.a = new VectorProvider(a);
        this.b = new VectorProvider(b);
    }

    public VectorBinaryFunctionalGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        ConfigurationSection aSec = WbsConfigReader.getRequiredSection(section, "a", settings, directory + "/a");
        ConfigurationSection bSec = WbsConfigReader.getRequiredSection(section, "b", settings, directory + "/b");

        a = new VectorProvider(aSec, settings, directory + "/a");
        b = new VectorProvider(bSec, settings, directory + "/b");
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        a.writeToConfig(section, path + ".a");
        b.writeToConfig(section, path + ".b");
    }
}
