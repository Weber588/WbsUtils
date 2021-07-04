package wbs.utils.util.configuration.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.plugin.WbsSettings;

public class VectorGeneratorSub extends VectorFunctionalGenerator {

    public VectorGeneratorSub(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory, 2, 2);
    }

    @Override
    protected Vector getNewValue() {
        return args.get(0).val().subtract(args.get(1).val());
    }
}
