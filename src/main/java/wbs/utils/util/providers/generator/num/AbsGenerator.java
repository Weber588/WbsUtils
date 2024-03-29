package wbs.utils.util.providers.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A simple generator that takes a NumProvider and returns its absolute value
 */
public class AbsGenerator extends DoubleGenerator {

    private final NumProvider value;

    /**
     * @param value A static value to be used to create an (also) static {@link NumProvider},
     *              which this generator operates on.
     */
    public AbsGenerator(double value) {
        this.value = new NumProvider(value);
    }

    /**
     * @param value A {@link NumProvider} to operate on.
     */
    public AbsGenerator(NumProvider value) {
        this.value = value;
    }

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public AbsGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        // Kind of hacky way of creating a num provider on itself
        value = new NumProvider(section.getParent(), section.getName(), settings, directory);
    }

    @Override
    protected void refreshInternal() {
        value.refresh();
    }

    @Override
    protected double getNewValue() {
        return Math.abs(value.val());
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        value.writeToConfig(section, path);
    }

    @Override
    public AbsGenerator clone() {
        return new AbsGenerator(new NumProvider(value));
    }
}
