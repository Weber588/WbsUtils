package wbs.utils.util.providers.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.providers.VectorProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

/**
 * An abstract class to do many of the operations needed for a {@link VectorGenerator} when operating on
 * exactly 2 ordered {@link VectorProvider}s.
 */
public abstract class VectorBinaryFunctionalGenerator extends VectorGenerator {

    /**
     * The first provider to operate on.
     */
    VectorProvider a;
    /**
     * The second provider to operate on.
     */
    VectorProvider b;

    /**
     * Clone constructor.
     * @param clone The object to clone from.
     */
    public VectorBinaryFunctionalGenerator(VectorBinaryFunctionalGenerator clone) {
        a = new VectorProvider(clone.a);
        b = new VectorProvider(clone.b);
    }

    /**
     * @param a The first vector/provider to operate on.
     * @param b The second vector/provider to operate on.
     */
    public VectorBinaryFunctionalGenerator(VectorProvider a, VectorProvider b) {
        this.a = a;
        this.b = b;
    }

    /**
     * @param a The first vector/provider to operate on.
     * @param b The second vector/provider to operate on.
     */
    public VectorBinaryFunctionalGenerator(Vector a, Vector b) {
        this.a = new VectorProvider(a);
        this.b = new VectorProvider(b);
    }

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
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
