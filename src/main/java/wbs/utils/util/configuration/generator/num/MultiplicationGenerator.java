package wbs.utils.util.configuration.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.plugin.WbsSettings;

import java.util.List;

/**
 * A functional generator that takes more than two NumProviders
 * and returns their product
 */
public class MultiplicationGenerator extends FunctionalGenerator {

    public MultiplicationGenerator(NumProvider... args) {
        super(args);
    }

    public MultiplicationGenerator(List<Double> args) {
        super(args);
    }

    public MultiplicationGenerator(double ... args) {
        super(args);
    }

    public MultiplicationGenerator(MultiplicationGenerator clone) {
        super(clone);
    }

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public MultiplicationGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory, 2);
    }

    @Override
    protected double getNewValue() {
        double total = 1;
        for (NumProvider arg : args) {
            total *= arg.val();
        }
        return total;
    }

    @Override
    public MultiplicationGenerator clone() {
        return new MultiplicationGenerator(this);
    }
}
