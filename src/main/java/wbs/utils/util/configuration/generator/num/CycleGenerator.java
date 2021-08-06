package wbs.utils.util.configuration.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A generator that moves between two NumProviders in one direction
 * over a given period, where it takes period calls to {@link #refresh()} to
 * go form start to end
 */
public class CycleGenerator extends DoubleGenerator{

    private NumProvider start;
    private NumProvider end;
    private NumProvider period;

    private double progress;
    private double step;

    protected CycleGenerator() {}

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public CycleGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory);

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
        if (progress > 1) progress -= 1;

        return (end.val() - start.val()) * progress + start.val();
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        start.writeToConfig(section, path + ".start");
        end.writeToConfig(section, path + ".end");
        period.writeToConfig(section, path + ".period");
    }
}
