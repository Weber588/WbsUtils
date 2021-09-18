package wbs.utils.util.providers.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.plugin.WbsSettings;

import java.util.List;

/**
 * A functional generator that takes more than two NumProviders
 * and returns the maximum value
 */
public class MaxGenerator extends FunctionalGenerator {

    public MaxGenerator(NumProvider... args) {
        super(args);
    }

    public MaxGenerator(List<Double> args) {
        super(args);
    }

    public MaxGenerator(double ... args) {
        super(args);
    }

    public MaxGenerator(MaxGenerator clone) {
        super(clone);
    }

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public MaxGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory, 2);
    }

    @Override
    protected double getNewValue() {
        double max = Double.MIN_VALUE;
        for (NumProvider arg : args) {
            max = Math.max(max, arg.val());
        }
        return max;
    }

    @Override
    public MaxGenerator clone() {
        return new MaxGenerator(this);
    }
}
