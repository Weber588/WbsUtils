package wbs.utils.util.configuration.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.plugin.WbsSettings;

import java.util.List;

/**
 * A functional generator that takes any number of NumProviders and
 * returns the sum
 */
public class AdditionGenerator extends FunctionalGenerator {

    public AdditionGenerator(NumProvider ... args) {
        super(args);
    }

    public AdditionGenerator(List<Double> args) {
        super(args);
    }

    public AdditionGenerator(double ... args) {
        super(args);
    }

    public AdditionGenerator(AdditionGenerator clone) {
        super(clone);
    }

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public AdditionGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory, 2);
    }

    @Override
    protected double getNewValue() {
        double total = 0;
        for (NumProvider arg : args) {
            total += arg.val();
        }
        return total;
    }

    @Override
    public AdditionGenerator clone() {
        return new AdditionGenerator(this);
    }
}
