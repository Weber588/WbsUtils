package wbs.utils.util.configuration.generator;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

public class PingPongGenerator extends DoubleGenerator{

    private NumProvider min;
    private NumProvider max;
    private NumProvider period;

    private double step;
    private double progress;

    protected PingPongGenerator() {}

    public PingPongGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
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

        step = 2 / period.val(); // 2 because 0-1 = min to max, 1-2 = max back to min
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

        step = 2 / period.val(); // 2 because 0-1 = min to max, 1-2 = max back to min

        super.refresh();
    }

    @Override
    protected double getNewValue() {
        progress += step;
        if (progress > 2) progress -= 2;

        if (progress <= 1) {
            return (max.val() - min.val()) * progress + min.val();
        } else {
            return (max.val() - min.val()) * (2 - progress) + min.val();
        }
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        min.writeToConfig(section, path + ".min");
        max.writeToConfig(section, path + ".max");
        period.writeToConfig(section, path + ".period");
    }

}
