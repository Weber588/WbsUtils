package wbs.utils.util.plugin.bootstrap;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;
import wbs.utils.util.WbsFileUtil;
import wbs.utils.util.plugin.WbsAbstractSettings;
import wbs.utils.util.plugin.WbsPlugin;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Level;
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

            this.setLevel(Level.ALL);
        }

        public void log(@NotNull LogRecord logRecord) {
            logRecord.setMessage(clazz.getCanonicalName() + logRecord.getMessage());
            super.log(logRecord);
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
