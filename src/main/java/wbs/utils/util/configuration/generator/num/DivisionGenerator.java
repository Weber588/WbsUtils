package wbs.utils.util.configuration.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.plugin.WbsSettings;

import java.util.List;

/**
 * A functional generator that takes exactly two NumProviders and divides
 * the first by the second
 */
public class DivisionGenerator extends BinaryFunctionalGenerator {

    public DivisionGenerator(DivisionGenerator clone) {
        super(clone);
    }

    public DivisionGenerator(NumProvider a, NumProvider b) {
        super(a, b);
    }
    public DivisionGenerator(double a, double b) {
        super(a, b);
    }

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public DivisionGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory);
    }

    protected double getNewValue() {
        return a.val() / b.val();
    }

    @Override
    public DivisionGenerator clone() {
        return new DivisionGenerator(this);
    }
}
