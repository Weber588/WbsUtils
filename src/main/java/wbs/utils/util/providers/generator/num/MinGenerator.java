package wbs.utils.util.providers.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.plugin.WbsSettings;

import java.util.List;

/**
 * A functional generator that takes more than two NumProviders
 * and returns the minimum value
 */
public class MinGenerator extends FunctionalGenerator {

    public MinGenerator(NumProvider... args) {
        super(args);
    }

    public MinGenerator(List<Double> args) {
        super(args);
    }

    public MinGenerator(double ... args) {
        super(args);
    }

    /**
     * Clone constructor.
     * @param clone The object to clone from.
     */
    public MinGenerator(MinGenerator clone) {
        super(clone);
    }

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public MinGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory, 2);
    }

    @Override
    protected double getNewValue() {
        double min = Double.MAX_VALUE;
        for (NumProvider arg : args) {
            min = Math.min(min, arg.val());
        }
        return min;
    }

    @Override
    public MinGenerator clone() {
        return new MinGenerator(this);
    }
}
