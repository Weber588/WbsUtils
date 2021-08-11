package wbs.utils.util.configuration.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A generator that moves between two NumProviders using a sine wave,
 * over a given period.
 */
public class PulseGenerator extends DoubleGenerator {

    private NumProvider min;
    private NumProvider max;
    private final NumProvider period;

    private double progress;
    private double step;

    public PulseGenerator(double min, double max, double period, double initialProgress) {
        this.min = new NumProvider(min);
        this.max = new NumProvider(max);
        this.period = new NumProvider(period);

        progress = Math.abs(initialProgress);
        step = 1.0 / this.period.val();
        enforceMinMax();
    }

    public PulseGenerator(NumProvider min, NumProvider max, NumProvider period, double initialProgress) {
        this.min = min;
        this.max = max;
        this.period = period;

        progress = Math.abs(initialProgress);
        step = 1.0 / this.period.val();
        enforceMinMax();
    }

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public PulseGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory);

        WbsConfigReader.requireNotNull(section, "min", settings, directory);
        WbsConfigReader.requireNotNull(section, "max", settings, directory);
        WbsConfigReader.requireNotNull(section, "period", settings, directory);

        min = new NumProvider(section, "min", settings, directory + "/min");
        max = new NumProvider(section, "max", settings, directory + "/max");
        period = new NumProvider(section, "period", settings, directory + "/period");

        progress = Math.abs(section.getDouble("initialProgress", 0));

        enforceMinMax();

        step = 1 / period.val();
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
        period.refresh();

        if (min.val() > max.val()) {
            NumProvider temp = min;
            min = max;
            max = temp;
        }

        step = 1 / period.val();
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
