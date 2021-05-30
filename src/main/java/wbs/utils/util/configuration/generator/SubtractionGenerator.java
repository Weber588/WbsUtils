package wbs.utils.util.configuration.generator;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

public class SubtractionGenerator extends FunctionalGenerator {

    public SubtractionGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory, 2, 2);
    }

    protected double getNewValue() {
        return args.get(0).val() - args.get(1).val();
    }
}
