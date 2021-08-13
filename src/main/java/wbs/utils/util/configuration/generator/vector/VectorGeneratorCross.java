package wbs.utils.util.configuration.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.configuration.VectorProvider;
import wbs.utils.util.plugin.WbsSettings;

import java.util.Arrays;
import java.util.List;

/**
 * A functional generator that accepts exactly two VectorProviders and
 * returns their cross product
 */
public class VectorGeneratorCross extends VectorBinaryFunctionalGenerator{

    public VectorGeneratorCross(VectorGeneratorCross clone) {
        super(clone);
    }
    public VectorGeneratorCross(VectorProvider a, VectorProvider b) {
        super(a, b);
    }
    public VectorGeneratorCross(Vector a, Vector b) {
        super(a, b);
    }

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public VectorGeneratorCross(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory);
    }

    @Override
    protected Vector getNewValue() {
        return a.val().crossProduct(b.val());
    }

    @Override
    public VectorGeneratorCross clone() {
        return new VectorGeneratorCross(this);
    }
}
