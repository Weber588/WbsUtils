package wbs.utils.util.configuration.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A functional generator that takes exactly two NumProviders and divides
 * the first by the second
 */
public class DivisionGenerator extends FunctionalGenerator {

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public DivisionGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory, 2, 2);
    }

    protected double getNewValue() {
        return args.get(0).val() / args.get(1).val();
    }
}
