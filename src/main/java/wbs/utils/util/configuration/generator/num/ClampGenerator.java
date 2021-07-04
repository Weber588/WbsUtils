package wbs.utils.util.configuration.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

// This is technically a functional generator, but args have special meanings for this
public class ClampGenerator extends DoubleGenerator {
    private NumProvider min;
    private NumProvider max;
    private NumProvider value;

    public ClampGenerator() {}

    public ClampGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory);

        WbsConfigReader.requireNotNull(section, "min", settings, directory);
        WbsConfigReader.requireNotNull(section, "max", settings, directory);
        WbsConfigReader.requireNotNull(section, "value", settings, directory);

        min = new NumProvider(section, "min", settings, directory + "/min");
        max = new NumProvider(section, "max", settings, directory + "/max");
        value = new NumProvider(section, "value", settings, directory + "/value");

        if (min.val() > max.val()) {
            NumProvider temp = min;
            min = max;
            max = temp;
        }
    }

    @Override
    public void refresh() {
        min.refresh();
        max.refresh();
        value.refresh();

        if (min.val() > max.val()) {
            NumProvider temp = min;
            min = max;
            max = temp;
        }

        super.refresh();
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
}
