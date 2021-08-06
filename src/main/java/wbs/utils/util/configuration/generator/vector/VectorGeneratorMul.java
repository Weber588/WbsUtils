package wbs.utils.util.configuration.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.configuration.VectorProvider;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A functional generator that takes two or more VectorProviders and
 * returns their product
 */
public class VectorGeneratorMul extends VectorFunctionalGenerator {

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
}
