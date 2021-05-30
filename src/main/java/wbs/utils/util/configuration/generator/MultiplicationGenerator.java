package wbs.utils.util.configuration.generator;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.plugin.WbsSettings;

public class MultiplicationGenerator extends FunctionalGenerator {

    public MultiplicationGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory, 2);
    }

    @Override
    protected double getNewValue() {
        double total = 1;
        for (NumProvider arg : args) {
            total *= arg.val();
        }
        return total;
    }
}
