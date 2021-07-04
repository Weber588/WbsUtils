package wbs.utils.util.configuration.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.plugin.WbsSettings;

public class AbsGenerator extends DoubleGenerator {

    private NumProvider value;

    private AbsGenerator() {}

    public AbsGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory);

        // Kind of hacky way of creating a num provider on itself
        value = new NumProvider(section.getParent(), section.getName(), settings, directory);
    }

    @Override
    public void refresh() {
        value.refresh();
        super.refresh();
    }

    @Override
    protected double getNewValue() {
        return Math.abs(value.val());
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        value.writeToConfig(section, path);
    }


}
