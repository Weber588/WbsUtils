package wbs.utils.util.configuration.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.plugin.WbsSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A type of DoubleGenerator that takes some number of NumProviders and
 * operates on them to determine the value
 */
public abstract class FunctionalGenerator extends DoubleGenerator {

    protected final List<NumProvider> args = new ArrayList<>();

    /**
     * Create this type of generator from a given config
     * @param section The section to read from
     * @param settings The settings to log errors against
     * @param directory The path taken through the section to reach this provider, for
     *                  logging purposes
     */
    public FunctionalGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
        this(section, settings, directory, 0, Integer.MAX_VALUE);
    }
    /**
     * Create this type of generator from a given config
     * @param section The section to read from
     * @param settings The settings to log errors against
     * @param directory The path taken through the section to reach this provider, for
     *                  logging purposes
     * @param minArgs The minimum number of arguments needed for this generator to work
     */
    public FunctionalGenerator(ConfigurationSection section, WbsSettings settings, String directory, int minArgs) {
        this(section, settings, directory, minArgs, Integer.MAX_VALUE);
    }

    /**
     * Create this type of generator from a given config
     * @param section The section to read from
     * @param settings The settings to log errors against
     * @param directory The path taken through the section to reach this provider, for
     *                  logging purposes
     * @param minArgs The minimum number of arguments needed for this generator to work
     * @param maxArgs The maximum number of arguments this generator is defined on
     */
    public FunctionalGenerator(ConfigurationSection section, WbsSettings settings, String directory, int minArgs, int maxArgs) {
        super(section, settings, directory);

        Set<String> keys = section.getKeys(false);

        if (keys.size() < minArgs) {
            settings.logError("This function requires at least " + minArgs + " args", directory);
            throw new InvalidConfigurationException();
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
    protected final void refreshInternal() {
        for (NumProvider arg : args) {
            arg.refresh();
        }
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        for (NumProvider arg : args) {
            arg.writeToConfig(section, path);
        }
    }
}
