package wbs.utils.util.providers.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

public abstract class BinaryFunctionalGenerator extends DoubleGenerator {

    protected NumProvider a, b;

    public BinaryFunctionalGenerator() {}

    public BinaryFunctionalGenerator(BinaryFunctionalGenerator clone) {
        a = new NumProvider(clone.a);
        b = new NumProvider(clone.b);
    }

    public BinaryFunctionalGenerator(NumProvider a, NumProvider b) {
        this.a = a;
        this.b = b;
    }

    public BinaryFunctionalGenerator(double a, double b) {
        this.a = new NumProvider(a);
        this.b = new NumProvider(b);
    }

    public BinaryFunctionalGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        WbsConfigReader.requireNotNull(section, "a", settings, directory + "/a");
        WbsConfigReader.requireNotNull(section, "b", settings, directory + "/b");

        a = new NumProvider(section, "a", settings, directory + "/a");
        b = new NumProvider(section, "b", settings, directory + "/b");
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        a.writeToConfig(section, path + ".a");
        b.writeToConfig(section, path + ".b");
    }
}
