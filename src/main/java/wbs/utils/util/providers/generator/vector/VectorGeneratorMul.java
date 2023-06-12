package wbs.utils.util.providers.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.providers.VectorProvider;
import wbs.utils.util.plugin.WbsSettings;

import java.util.List;

/**
 * A functional generator that takes two or more VectorProviders and
 * returns their product
 */
public class VectorGeneratorMul extends VectorFunctionalGenerator {

    /**
     * Clone constructor.
     * @param clone The object to clone from.
     */
    public VectorGeneratorMul(VectorGeneratorMul clone) {
        super(clone);
    }

    public VectorGeneratorMul(VectorProvider ... args) {
        super(args);
    }

    public VectorGeneratorMul(List<Vector> args) {
        super(args);
    }

    public VectorGeneratorMul(Vector ... args) {
        super(args);
    }

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public VectorGeneratorMul(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory, 2);
    }

    @Override
    protected Vector getNewValue() {
        Vector total = new Vector(1, 1, 1);
        for (VectorProvider provider : args) {
            total.multiply(provider.val());
        }
        return total;
    }

    @Override
    public VectorGeneratorMul clone() {
        return new VectorGeneratorMul(this);
    }
}
