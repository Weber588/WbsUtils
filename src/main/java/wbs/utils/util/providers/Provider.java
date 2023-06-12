package wbs.utils.util.providers;

import org.bukkit.configuration.ConfigurationSection;

public interface Provider extends Refreshable {
    /**
     * Save this provider in a config (that can often by read by its constructor)
     * @param section The section to write to
     * @param path The field/path inside the given section
     */
    void writeToConfig(ConfigurationSection section, String path);
}
