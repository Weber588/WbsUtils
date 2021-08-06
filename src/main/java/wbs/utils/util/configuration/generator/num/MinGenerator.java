package wbs.utils.util.configuration.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A functional generator that takes more than two NumProviders
 * and returns the minimum value
 */
public class MinGenerator extends FunctionalGenerator {

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
}
