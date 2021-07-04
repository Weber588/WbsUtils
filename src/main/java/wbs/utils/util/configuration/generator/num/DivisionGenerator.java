package wbs.utils.util.configuration.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.plugin.WbsSettings;

public class DivisionGenerator extends FunctionalGenerator {

    public DivisionGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory, 2, 2);
    }

    protected double getNewValue() {
        return args.get(0).val() / args.get(1).val();
    }
}
