package wbs.utils.util.plugin;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
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

/**
 * Contains all configuration settings that aren't universal to WbsPlugin
 * and provides methods to access them.
 * Includes methods for logging configuration errors and safely loading configs.
 * @author Weber588
 */
@NullMarked
@SuppressWarnings("unused")
public abstract class WbsAbstractSettings {
    protected ArrayList<String> errors = new ArrayList<>();

    public abstract Logger getLogger();
    public abstract ComponentLogger getComponentLogger();

    public abstract File getDataFolder();
    public abstract Path getDataPath();

    public abstract void saveResource(String resourcePath, boolean replace);

    /**
     * @return The errors from the last reload/initial load
     */
    public ArrayList<String> getErrors() {
        return errors;
    }

    /**
     * To be called during initialization,
     * and allows the configs to be reloaded
     * without restarting the server/reloading
     * the whole plugin.
     */
    public abstract void reload();

    protected abstract YamlConfiguration loadDefaultConfig(String configName);

    /**
     * Log an error both in console and in the errors list
     * that may be used to display errors in a command
     * @param error The error message
     * @param directory The directory in which the error took place
     */
    public void logError(String error, String directory) {
        errors.add("&c" + error + " &7(" + directory + ")");
        getLogger().warning(error + " (" + directory + ")");
    }

    // Taken from spigot code; needed to add to how it swallows config exceptions
    /**
     * A version of {@link YamlConfiguration#loadConfiguration(File)}
     * that also logs yaml parsing errors.
     * @param file The file to parse
     * @return The YamlConfiguration
     */
    protected YamlConfiguration loadConfigSafely(File file) {
        Validate.notNull(file, "File cannot be null");

        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (FileNotFoundException ex) {
            getLogger().log(Level.SEVERE, "File not found: " + file, ex);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
        } catch (InvalidConfigurationException ex) {
            getLogger().log(Level.SEVERE, "Cannot load " + file , ex);

            errors.add("&cYAML parsing error in file " + file.getName() + ". See console for details.");
        }

        return config;
    }

    /**
     * Runs an operation on a YamlConfiguration as defined in the delegate
     * after guaranteeing that the config is non-null and corresponding file
     * exists in the given plugins folder. Automatically logs errors, formatted with
     * the given dataName.
     * @param config Optionally, the configuration to write to and save. Leave as null to
     *               automatically load from the given
     * @param fileName The file to save to and read from if config is null
     * @param dataName The name of the thing being saved. For example, using "Player"
     *                 will make the file created message say "Player file created."
     * @param delegate The function to run on the YamlConfiguration before saving
     * @return The used YamlConfiguration; this is config if non-null.
     */
    @Nullable
    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected YamlConfiguration saveYamlData(@Nullable YamlConfiguration config,
                                             @NotNull String fileName,
                                             @NotNull String dataName,
                                             @NotNull Consumer<YamlConfiguration> delegate) {
        File file = new File(getDataFolder(), fileName);
        if (config == null) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                getLogger().info(WbsStrings.capitalize(dataName) + " file failed to create.");
                e.printStackTrace();
                return null;
            }
            getLogger().info(WbsStrings.capitalize(dataName) + " file created.");
            config = loadConfigSafely(file);
        }

        delegate.accept(config);

        try {
            config.save(file);
        } catch (IOException e) {
            getLogger().info(WbsStrings.capitalize(dataName) + " file failed to save.");
            e.printStackTrace();
            return config;
        }
        getLogger().info("Saved " + dataName + "s.");
        return config;
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
        File configFile = new File(getDataFolder(), path);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource(path, false);
        }

        return configFile;
    }
}
