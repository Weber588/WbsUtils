package wbs.utils.util.configuration.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.plugin.WbsSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class FunctionalGenerator extends DoubleGenerator {

    protected final List<NumProvider> args = new ArrayList<>();

    public FunctionalGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        this(section, settings, directory, 0, Integer.MAX_VALUE);
    }
    public FunctionalGenerator(ConfigurationSection section, WbsSettings settings, String directory, int minArgs) {
        this(section, settings, directory, minArgs, Integer.MAX_VALUE);
    }

    public FunctionalGenerator(ConfigurationSection section, WbsSettings settings, String directory, int minArgs, int maxArgs) {
        super(section, settings, directory);

        Set<String> keys = section.getKeys(false);

        if (keys.size() < minArgs) {
            settings.logError("This function requires at least " + minArgs + " args", directory);
        } else if (keys.size() > maxArgs) {
            settings.logError("This function accepts a maximum of " + maxArgs + " args", directory);
            throw new InvalidConfigurationException();
        }

        for (String key : section.getKeys(false)) {
            NumProvider a = new NumProvider(section, key, settings, directory + "/" + key);
            args.add(a);
        }
    }

    @Override
    public void refresh() {
        for (NumProvider arg : args) {
            arg.refresh();
        }

        super.refresh();
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        for (NumProvider arg : args) {
            arg.writeToConfig(section, path);
        }
    }
}
