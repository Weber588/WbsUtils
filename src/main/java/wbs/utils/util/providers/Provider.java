package wbs.utils.util.providers;

import org.bukkit.configuration.ConfigurationSection;

public interface Provider extends Refreshable {
    void writeToConfig(ConfigurationSection section, String path);
}
