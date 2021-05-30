package wbs.utils.util.configuration.generator;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.plugin.WbsSettings;

public class ModuloGenerator extends FunctionalGenerator {
    public ModuloGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory, 2, 2);
    }

    @Override
    protected double getNewValue() {
        return args.get(0).val() % args.get(1).val();
    }
}
