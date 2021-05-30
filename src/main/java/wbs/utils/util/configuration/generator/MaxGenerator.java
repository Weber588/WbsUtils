package wbs.utils.util.configuration.generator;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.plugin.WbsSettings;

public class MaxGenerator extends FunctionalGenerator {

    public MaxGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory, 2);
    }

    @Override
    protected double getNewValue() {
        double max = Double.MIN_VALUE;
        for (NumProvider arg : args) {
            max = Math.max(max, arg.val());
        }
        return max;
    }
}
