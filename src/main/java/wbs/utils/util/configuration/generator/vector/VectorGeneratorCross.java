package wbs.utils.util.configuration.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.plugin.WbsSettings;

public class VectorGeneratorCross extends VectorFunctionalGenerator{

    public VectorGeneratorCross(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory, 2, 2);
    }

    @Override
    protected Vector getNewValue() {
        return args.get(0).val().crossProduct(args.get(1).val());
    }
}
