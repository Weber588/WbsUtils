package wbs.utils.util.configuration.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A generator that moves between two NumProviders over a given period,
 * where it takes period calls to {@link #refresh()} to go from min
 * to max, and then goes back from max to min over another period
 */
public class PingPongGenerator extends DoubleGenerator{

    private NumProvider min;
    private NumProvider max;
    private NumProvider period;

    private double step;
    private double progress;

    protected PingPongGenerator() {}

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
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
    protected void refreshInternal() {
        min.refresh();
        max.refresh();
        period.refresh();

        if (min.val() > max.val()) {
            NumProvider temp = min;
            min = max;
            max = temp;
        }

        step = 2 / period.val(); // 2 because 0-1 = min to max, 1-2 = max back to min
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
