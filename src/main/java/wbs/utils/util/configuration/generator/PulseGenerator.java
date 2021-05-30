package wbs.utils.util.configuration.generator;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

public class PulseGenerator extends DoubleGenerator{

    private NumProvider min;
    private NumProvider max;
    private NumProvider period;

    private double progress;
    private double step;

    public PulseGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory);

        WbsConfigReader.requireNotNull(section, "min", settings, directory);
        WbsConfigReader.requireNotNull(section, "max", settings, directory);
        WbsConfigReader.requireNotNull(section, "period", settings, directory);

        min = new NumProvider(section, "min", settings, directory + "/min");
        max = new NumProvider(section, "max", settings, directory + "/max");
        period = new NumProvider(section, "period", settings, directory + "/period");

        progress = Math.abs(section.getDouble("initialProgress", 0));

        if (min.val() > max.val()) {
            NumProvider temp = min;
            min = max;
            max = temp;
        }

        step = 1 / period.val();
    }

    @Override
    public void refresh() {
        min.refresh();
        max.refresh();
        period.refresh();

        if (min.val() > max.val()) {
            NumProvider temp = min;
            min = max;
            max = temp;
        }

        step = 1 / period.val();

        super.refresh();
    }

    @Override
    protected double getNewValue() {
        progress += step;

        progress %= 1;

        return (Math.sin(progress * 2 * Math.PI) / 2 + 0.5) * (max.val() - min.val()) + min.val();
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        min.writeToConfig(section, path + ".min");
        max.writeToConfig(section, path + ".max");
        period.writeToConfig(section, path + ".period");
    }
}
