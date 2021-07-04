package wbs.utils.util.configuration.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.plugin.WbsSettings;

public class AdditionGenerator extends FunctionalGenerator {

    public AdditionGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory, 2);
    }

    @Override
    protected double getNewValue() {
        double total = 0;
        for (NumProvider arg : args) {
            total += arg.val();
        }
        return total;
    }
}
