package wbs.utils.util.configuration.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.configuration.VectorProvider;
import wbs.utils.util.plugin.WbsSettings;

public class VectorGeneratorMul extends VectorFunctionalGenerator {

    public VectorGeneratorMul(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory, 2);
    }

    @Override
    protected Vector getNewValue() {
        Vector total = new Vector(1, 1, 1);
        for (VectorProvider provider : args) {
            total.multiply(provider.val());
        }
        return total;
    }
}
