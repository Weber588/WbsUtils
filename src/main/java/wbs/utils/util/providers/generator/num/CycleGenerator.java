package wbs.utils.util.providers.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A generator that moves between two NumProviders in one direction
 * over a given period, where it takes period calls to {@link #refresh()} to
 * go form start to end
 */
public class CycleGenerator extends DoubleGenerator{

    private final NumProvider start;
    private final NumProvider end;
    private final NumProvider period;

    private double progress;
    private double step;

    /**
     * Clone constructor.
     * @param clone The object to clone from.
     */
    public CycleGenerator(CycleGenerator clone) {
        start = new NumProvider(clone.start);
        end = new NumProvider(clone.end);
        period = new NumProvider(clone.period);

        progress = clone.progress;
        step = clone.step;
    }

    public CycleGenerator(double start, double end, double period, double initialProgress) {
        this.start = new NumProvider(start);
        this.end = new NumProvider(end);
        this.period = new NumProvider(period);

        progress = Math.abs(initialProgress);
        step = 1.0 / this.period.val();
    }

    public CycleGenerator(NumProvider start, NumProvider end, NumProvider period, double initialProgress) {
        this.start = start;
        this.end = end;
        this.period = period;

        progress = Math.abs(initialProgress);
        step = 1.0 / period.val();
    }

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public CycleGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        WbsConfigReader.requireNotNull(section, "start", settings, directory);
        WbsConfigReader.requireNotNull(section, "end", settings, directory);
        WbsConfigReader.requireNotNull(section, "period", settings, directory);

        start = new NumProvider(section, "start", settings, directory + "/start");
        end = new NumProvider(section, "end", settings, directory + "/end");
        period = new NumProvider(section, "period", settings, directory + "/period");

        progress = Math.abs(section.getDouble("initialProgress", 0));

        step = 1.0 / period.val();
    }

    @Override
    protected void refreshInternal() {
        start.refresh();
        end.refresh();
        period.refresh();

        step = 1.0 / period.val();
    }

    @Override
    protected double getNewValue() {
        progress += step;

        if (Double.isFinite(progress)) {
            progress %= 1;
        } else {
            progress = 0;
        }

        return (end.val() - start.val()) * progress + start.val();
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        start.writeToConfig(section, path + ".start");
        end.writeToConfig(section, path + ".end");
        period.writeToConfig(section, path + ".period");
    }

    @Override
    public CycleGenerator clone() {
        return new CycleGenerator(this);
    }
}
