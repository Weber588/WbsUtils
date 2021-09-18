package wbs.utils.util.providers.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.providers.VectorProvider;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A functional generator that accepts exactly two VectorProviders and
 * returns the value of the first minus the value of the second
 */
public class VectorGeneratorSub extends VectorBinaryFunctionalGenerator {

    public VectorGeneratorSub(VectorGeneratorSub clone) {
        super(clone);
    }
    public VectorGeneratorSub(VectorProvider a, VectorProvider b) {
        super(a, b);
    }
    public VectorGeneratorSub(Vector a, Vector b) {
        super(a, b);
    }


    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public VectorGeneratorSub(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory);
    }

    @Override
    protected Vector getNewValue() {
        return a.val().subtract(b.val());
    }

    @Override
    public VectorGeneratorSub clone() {
        return new VectorGeneratorSub(this);
    }
}
