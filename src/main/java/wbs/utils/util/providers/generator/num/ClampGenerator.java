package wbs.utils.util.providers.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A generator that takes a NumProvider and clamps it between two
 * other NumProviders
 */
public class ClampGenerator extends DoubleGenerator {
    private NumProvider min;
    private NumProvider max;
    private final NumProvider value;

    /**
     * Clone constructor.
     * @param clone The object to clone from.
     */
    public ClampGenerator(ClampGenerator clone) {
        min = new NumProvider(clone.min);
        max = new NumProvider(clone.max);
        value = new NumProvider(clone.value);
    }

    public ClampGenerator(double min, double max, double value) {
        this.min = new NumProvider(min);
        this.max = new NumProvider(max);
        this.value = new NumProvider(value);
        enforceMinMax();
    }

    public ClampGenerator(NumProvider min, NumProvider max, NumProvider value) {
        this.min = min;
        this.max = max;
        this.value = value;
        enforceMinMax();
    }

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public ClampGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        WbsConfigReader.requireNotNull(section, "min", settings, directory);
        WbsConfigReader.requireNotNull(section, "max", settings, directory);
        WbsConfigReader.requireNotNull(section, "value", settings, directory);

        min = new NumProvider(section, "min", settings, directory + "/min");
        max = new NumProvider(section, "max", settings, directory + "/max");
        value = new NumProvider(section, "value", settings, directory + "/value");
        enforceMinMax();
    }

    private void enforceMinMax() {
        if (min.val() > max.val()) {
            NumProvider temp = min;
            min = max;
            max = temp;
        }
    }

    @Override
    protected void refreshInternal() {
        min.refresh();
        max.refresh();
        value.refresh();

        if (min.val() > max.val()) {
            NumProvider temp = min;
            min = max;
            max = temp;
        }
    }

    @Override
    protected double getNewValue() {
        return Math.max(min.val(), Math.min(max.val(), value.val()));
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        min.writeToConfig(section, path + ".min");
        max.writeToConfig(section, path + ".max");
        value.writeToConfig(section, path + ".period");
    }

    @Override
    public ClampGenerator clone() {
        return new ClampGenerator(this);
    }
}
