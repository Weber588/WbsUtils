package wbs.utils.util.configuration.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.VectorProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * A type of VectorGenerator that takes some number of VectorProviders and
 * operates on them to determine the value
 */
public abstract class VectorFunctionalGenerator extends VectorGenerator {

    protected final List<VectorProvider> args = new ArrayList<>();

    public VectorFunctionalGenerator() {}

    /**
     * Create this type of generator from a given config
     * @param section The section to read from
     * @param settings The settings to log errors against
     * @param directory The path taken through the section to reach this provider, for
     *                  logging purposes
     */
    public VectorFunctionalGenerator(ConfigurationSection section, WbsSettings settings, String directory) {
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
    public VectorFunctionalGenerator(ConfigurationSection section, WbsSettings settings, String directory, int minArgs) {
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
    public VectorFunctionalGenerator(ConfigurationSection section, WbsSettings settings, String directory, int minArgs, int maxArgs) {
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
            ConfigurationSection argSection = WbsConfigReader.getRequiredSection(section, key, settings, directory);
            VectorProvider a = new VectorProvider(argSection, settings, directory + "/" + key);
            args.add(a);
        }
    }

    @Override
    public final void refreshInternal() {
        for (VectorProvider arg : args) {
            arg.refresh();
        }
    }

    @Override
    public final void writeToConfig(ConfigurationSection section, String path) {
        for (VectorProvider arg : args) {
            arg.writeToConfig(section, path);
        }
    }
}
