package wbs.utils.util.providers.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A functional generator that accepts two NumProviders and
 * returns the first minus the second
 */
public class SubtractionGenerator extends BinaryFunctionalGenerator {

    /**
     * Clone constructor.
     * @param clone The object to clone from.
     */
    public SubtractionGenerator(SubtractionGenerator clone) {
        super(clone);
    }

    public SubtractionGenerator(NumProvider a, NumProvider b) {
        super(a, b);
    }
    public SubtractionGenerator(double a, double b) {
        super(a, b);
    }
    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public SubtractionGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory);
    }

    protected double getNewValue() {
        return a.val() - b.val();
    }

    @Override
    public SubtractionGenerator clone() {
        return new SubtractionGenerator(this);
    }
}
