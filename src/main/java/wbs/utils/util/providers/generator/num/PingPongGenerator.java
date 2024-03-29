package wbs.utils.util.providers.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.providers.NumProvider;
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
    private final NumProvider period;

    private double step;
    private double progress;

    /**
     * Clone constructor.
     * @param clone The object to clone from.
     */
    public PingPongGenerator(PingPongGenerator clone) {
        min = new NumProvider(clone.min);
        max = new NumProvider(clone.max);
        period = new NumProvider(clone.period);

        progress = clone.progress;
        step = clone.step;
    }

    public PingPongGenerator(double min, double max, double period, double initialProgress) {
        this.min = new NumProvider(min);
        this.max = new NumProvider(max);
        this.period = new NumProvider(period);

        progress = Math.abs(initialProgress);
        step = 1.0 / this.period.val();
        enforceMinMax();
    }

    public PingPongGenerator(NumProvider min, NumProvider max, NumProvider period, double initialProgress) {
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
    public PingPongGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        WbsConfigReader.requireNotNull(section, "min", settings, directory);
        WbsConfigReader.requireNotNull(section, "max", settings, directory);
        WbsConfigReader.requireNotNull(section, "period", settings, directory);

        min = new NumProvider(section, "min", settings, directory + "/min");
        max = new NumProvider(section, "max", settings, directory + "/max");
        period = new NumProvider(section, "period", settings, directory + "/period");

        progress = Math.abs(section.getDouble("initialProgress", 0));

        enforceMinMax();

        step = 2 / period.val(); // 2 because 0-1 = min to max, 1-2 = max back to min
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

        step = 2 / period.val(); // 2 because 0-1 = min to max, 1-2 = max back to min
    }

    @Override
    protected double getNewValue() {
        progress += step;

        if (Double.isFinite(progress)) {
            progress %= 2;
        } else {
            progress = 0;
        }

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

    @Override
    public PingPongGenerator clone() {
        return new PingPongGenerator(this);
    }
}
