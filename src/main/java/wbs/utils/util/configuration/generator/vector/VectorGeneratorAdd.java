package wbs.utils.util.configuration.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.VectorProvider;
import wbs.utils.util.plugin.WbsSettings;

public class VectorGeneratorAdd extends VectorFunctionalGenerator {

    public VectorGeneratorAdd(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory);
    }

    @Override
    protected Vector getNewValue() {
        Vector total = new Vector(0,0,0);
        for (VectorProvider arg : args) {
            total.add(arg.val());
        }
        return total;
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {

    }
}
