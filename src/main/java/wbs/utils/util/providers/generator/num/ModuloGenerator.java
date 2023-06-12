package wbs.utils.util.providers.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A functional generator that takes exactly two NumProviders and
 * returns the first value under the modulo of the second
 */
public class ModuloGenerator extends BinaryFunctionalGenerator {

    /**
     * Clone constructor.
     * @param clone The object to clone from.
     */
    public ModuloGenerator(ModuloGenerator clone) {
        super(clone);
    }

    public ModuloGenerator(NumProvider a, NumProvider b) {
        super(a, b);
    }
    public ModuloGenerator(double a, double b) {
        super(a, b);
    }

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public ModuloGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory);
    }

    @Override
    protected double getNewValue() {
        return a.val() % b.val();
    }

    @Override
    public ModuloGenerator clone() {
        return new ModuloGenerator(this);
    }
}
