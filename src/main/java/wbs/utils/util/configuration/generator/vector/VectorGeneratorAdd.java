package wbs.utils.util.configuration.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.VectorProvider;
import wbs.utils.util.plugin.WbsSettings;

import java.util.Arrays;
import java.util.List;

/**
 * A functional generator that accepts any number of arguments and returns their
 * sum
 */
public class VectorGeneratorAdd extends VectorFunctionalGenerator {

    public VectorGeneratorAdd(VectorProvider ... args) {
        this.args.addAll(Arrays.asList(args));
    }

    public VectorGeneratorAdd(List<Vector> args) {
        args.forEach(arg -> this.args.add(new VectorProvider(arg)));
    }

    public VectorGeneratorAdd(Vector ... args) {
        Arrays.stream(args).forEach(arg -> this.args.add(new VectorProvider(arg)));
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
}
