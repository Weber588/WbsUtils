package wbs.utils.util.configuration.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A generator that returns a random value between two NumProviders
 */
public class RandomGenerator extends DoubleGenerator {

    private NumProvider min, max;

    public RandomGenerator(double min, double max) {
        this.min = new NumProvider(min);
        this.max = new NumProvider(max);

        enforceMinMax();
    }

    public RandomGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory);

        WbsConfigReader.requireNotNull(section, "min", settings, directory);
        WbsConfigReader.requireNotNull(section, "max", settings, directory);

        min = new NumProvider(section, "min", settings, directory);
        max = new NumProvider(section, "max", settings, directory);

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
    }

    @Override
    protected double getNewValue() {
        return Math.random() * (max.val() - min.val()) + min.val();
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        min.writeToConfig(section, path + ".min");
        max.writeToConfig(section, path + ".max");
    }

}
