package wbs.utils.util.providers.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.plugin.WbsSettings;
import wbs.utils.util.providers.Provider;

/**
 * A generator that returns a single double until {@link #refresh()} is called,
 * at which point it calculates a new value.
 */
public abstract class DoubleGenerator implements Provider {

    protected DoubleGenerator() {}

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public DoubleGenerator(ConfigurationSection section, WbsSettings settings, String directory) {}

    /**
     * Generate a new value to override the current one
     * @return The new value
     */
    protected abstract double getNewValue();

    /**
     * Save this generator in a config that can be read by its constructor
     * @param section The section to write to
     * @param path The field/path inside the given section
     */
    public abstract void writeToConfig(ConfigurationSection section, String path);

    private double value;

    /**
     * Generate a new value.
     */
    public final void refresh() {
        refreshInternal();
        value = getNewValue();
    }

    protected void refreshInternal() {}

    /**
     * @return The most recently generated value
     */
    public double getValue() {
        return value;
    }

    public abstract DoubleGenerator clone();
}
