package wbs.utils.util.providers;

import org.bukkit.configuration.ConfigurationSection;

public interface DataProvider extends Refreshable {
    void writeToConfig(ConfigurationSection section, String path);
}
