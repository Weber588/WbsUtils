package wbs.utils.util.plugin.bootstrap;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;
import org.slf4j.event.Level;
import wbs.utils.util.WbsFileUtil;
import wbs.utils.util.plugin.WbsAbstractSettings;
import wbs.utils.util.plugin.WbsPlugin;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public abstract class WbsBootstrapSettings<T extends WbsPlugin> extends WbsAbstractSettings {
    protected final BootstrapContext context;
    private final Class<T> clazz;

    public WbsBootstrapSettings(BootstrapContext context, Class<T> clazz) {
        this.context = context;
        this.clazz = clazz;
    }

    @Override
    public Logger getLogger() {
        return new BootstrapLogger();
    }

    public BootstrapContext getContext() {
        return context;
    }

    @Override
    public ComponentLogger getComponentLogger() {
        return context.getLogger();
    }

    @Override
    public File getDataFolder() {
        return context.getDataDirectory().toFile();
    }

    @Override
    public Path getDataPath() {
        return context.getDataDirectory();
    }

    @Override
    public void saveResource(String resourcePath, boolean replace) {
        WbsFileUtil.saveResource(context, clazz, resourcePath, replace);
    }

    private class BootstrapLogger extends Logger {
        protected BootstrapLogger() {
            super(clazz.getCanonicalName(), null);

            this.setLevel(java.util.logging.Level.ALL);
        }

        public void log(@NotNull LogRecord logRecord) {
            String levelString = logRecord.getLevel().getName();

            Level level = switch (levelString.toLowerCase()) {
                case "severe" -> Level.ERROR;
                case "warning" -> Level.WARN;
                case "fine", "finer", "finest" -> Level.DEBUG;
                case "all" -> Level.TRACE;
                default -> Level.INFO;
            };

            ComponentLogger logger = context.getLogger();
            switch (level) {
                case ERROR -> {
                    logger.error(logRecord.getMessage());
                }
                case WARN -> {
                    logger.warn(logRecord.getMessage());
                }
                case INFO -> {
                    logger.info(logRecord.getMessage());
                }
                case DEBUG -> {
                    logger.debug(logRecord.getMessage());
                }
                case TRACE -> {
                    logger.trace(logRecord.getMessage());
                }
            }
        }
    }

    @Override
    protected YamlConfiguration loadDefaultConfig(String configName) {
        File configFile = new File(getDataFolder(), configName);
        if (!configFile.exists()) {
            saveResource(configName, false);
        }

        return this.loadConfigSafely(this.genConfig(configName));
    }
}
