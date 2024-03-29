package wbs.utils.util.providers.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.providers.VectorProvider;
import wbs.utils.util.plugin.WbsSettings;

import java.util.List;

/**
 * A functional generator that accepts any number of arguments and returns their
 * sum
 */
public class VectorGeneratorAdd extends VectorFunctionalGenerator {

    /**
     * Clone constructor.
     * @param clone The object to clone from.
     */
    public VectorGeneratorAdd(VectorGeneratorAdd clone) {
        super(clone);
    }

    public VectorGeneratorAdd(VectorProvider ... args) {
        super(args);
    }

    public VectorGeneratorAdd(List<Vector> args) {
        super(args);
    }

    public VectorGeneratorAdd(Vector ... args) {
        super(args);
    }

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public VectorGeneratorAdd(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory);
    }

    @Override
    protected Vector getNewValue() {
        Vector total = new Vector(0,0,0);
        for (VectorProvider arg : args) {
            total.add(arg.val());
        }
        return total;
    }

    @Override
    public VectorGeneratorAdd clone() {
        return new VectorGeneratorAdd(this);
    }
}
