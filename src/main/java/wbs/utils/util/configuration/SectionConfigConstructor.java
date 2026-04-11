package wbs.utils.util.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.plugin.WbsSettings;

@NullMarked
@FunctionalInterface
public interface SectionConfigConstructor<T> extends ConfigConstructor<T> {
    T construct(ConfigurationSection section, @Nullable WbsSettings settings, @Nullable String directory);

    @Override
    default T construct(ConfigurationSection parent, String key, @Nullable WbsSettings settings, @Nullable String directory) {
        ConfigurationSection section = parent.getConfigurationSection(key);

        if (section == null) {
            throw new InvalidConfigurationException("Must be a section.", directory + "/key");
        }

        return construct(section, settings, directory);
    }
}
