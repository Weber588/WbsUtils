package wbs.utils.util.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.plugin.WbsSettings;

@NullMarked
@FunctionalInterface
public interface ConfigConstructor<T> {
    @NotNull
    T construct(ConfigurationSection parent, String key, @Nullable WbsSettings settings, @Nullable String directory) throws InvalidConfigurationException;
}
