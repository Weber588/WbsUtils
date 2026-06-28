package wbs.utils.util.plugin;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.nio.file.Path;
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

		if (messageChar.length() == 1) {
			messageChar = "&" + messageChar;
		}
		if (highlightChar.length() == 1) {
			highlightChar = "&" + highlightChar;
		}
		if (errorChar.length() == 1) {
			errorChar = "&" + errorChar;
		}

		MiniMessage miniMessage = MiniMessage.miniMessage();
		Style defaultStyle = plugin.deserializeKnownFormats(messageChar + " ").style();
		Style highlightStyle = plugin.deserializeKnownFormats(highlightChar + " ").style();
		Style errorStyle = plugin.deserializeKnownFormats(errorChar + " ").style();

		plugin.setDisplays(
				newPrefix,
				defaultStyle,
				highlightStyle,
				errorStyle
		);
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
