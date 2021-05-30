package wbs.utils.util.configuration.generator;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.plugin.WbsSettings;

public class MinGenerator extends FunctionalGenerator {
    public MinGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory, 2);
    }

    @Override
    protected double getNewValue() {
        double min = Double.MAX_VALUE;
        for (NumProvider arg : args) {
            min = Math.min(min, arg.val());
        }
        return min;
    }
}
