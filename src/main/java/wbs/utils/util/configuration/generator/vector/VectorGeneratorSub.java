package wbs.utils.util.configuration.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.configuration.VectorProvider;
import wbs.utils.util.plugin.WbsSettings;

import java.util.Arrays;
import java.util.List;

/**
 * A functional generator that accepts exactly two VectorProviders and
 * returns the value of the first minus the value of the second
 */
public class VectorGeneratorSub extends VectorFunctionalGenerator {

    public VectorGeneratorSub(VectorProvider arg1, VectorProvider arg2) {
        args.add(arg1);
        args.add(arg2);
    }

    public VectorGeneratorSub(Vector arg1, Vector arg2) {
        args.add(new VectorProvider(arg1));
        args.add(new VectorProvider(arg2));
    }

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public VectorGeneratorSub(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory, 2, 2);
    }

    @Override
    protected Vector getNewValue() {
        return args.get(0).val().subtract(args.get(1).val());
    }
}
