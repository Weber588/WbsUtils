package wbs.utils.util.plugin;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import wbs.utils.util.string.WbsStrings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@NullMarked
@SuppressWarnings("unused")
public abstract class WbsSettings extends WbsAbstractSettings {
	protected WbsPlugin plugin;
	@Deprecated
	protected Logger logger;
	protected WbsSettings(WbsPlugin plugin) {
		this.plugin = plugin;
		logger = plugin.getLogger();
	}

	@Override
	protected YamlConfiguration loadDefaultConfig(String configName) {
		File configFile = new File(plugin.getDataFolder(), configName);
		if (!configFile.exists()) {
			plugin.saveResource(configName, false);
		}

		YamlConfiguration config = loadConfigSafely(genConfig(configName));
		loadMessageFormat(config);

		return config;
	}

	protected void loadMessageFormat(YamlConfiguration config) {
		String defaultPrefix = "[" + plugin.getName()  + "]";
		String messageChar = config.getString("message-colour", "a");
		String highlightChar = config.getString("highlight-colour", "b");
		String errorChar = config.getString("error-colour", "c");

        String newPrefix = config.getString("message-prefix", defaultPrefix);
        ChatColor newColour = ChatColor.getByChar(messageChar);
        ChatColor newHighlight = ChatColor.getByChar(highlightChar);
        ChatColor newErrorColour = ChatColor.getByChar(errorChar);
        plugin.setDisplays(newPrefix, newColour, newHighlight, newErrorColour);
	}

	/**
	 * Retrieve a file from the plugin data folder. If
	 * it does not exist, it will be created from the
	 * jar
	 * @param path The path of the file within the plugins data folder
	 * @return The new file, or the file that was present at the path.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	protected File genConfig(String path) {
		File configFile = new File(plugin.getDataFolder(), path);
        if (!configFile.exists()) {
        	configFile.getParentFile().mkdirs();
            plugin.saveResource(path, false);
        }

        return configFile;
	}

	@Override
	public Logger getLogger() {
		return plugin.getLogger();
	}

	@Override
	public ComponentLogger getComponentLogger() {
		return plugin.getComponentLogger();
	}

	@Override
	public File getDataFolder() {
		return plugin.getDataFolder();
	}

	@Override
	public Path getDataPath() {
		return plugin.getDataPath();
	}

	@Override
	public void saveResource(String resourcePath, boolean replace) {
		plugin.saveResource(resourcePath, replace);
	}
}
