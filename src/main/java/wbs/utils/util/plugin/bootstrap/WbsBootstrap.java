package wbs.utils.util.plugin.bootstrap;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Keyed;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;
import wbs.utils.util.WbsFileUtil;
import wbs.utils.util.plugin.WbsPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@NullMarked
@SuppressWarnings({"UnstableApiUsage", "unused"})
public abstract class WbsBootstrap<T extends WbsPlugin> implements PluginBootstrap {
    public static <T extends WbsPlugin> WbsBootstrapper<T> getAnonymousInstance(BootstrapContext context, Class<T> clazz, Consumer<BootstrapContext> bootstrapper) {
        return getAnonymous(clazz, bootstrapper).apply(context);
    }
    public static <T extends WbsPlugin> Function<BootstrapContext, WbsBootstrapper<T>> getAnonymous(Class<T> clazz, Consumer<BootstrapContext> bootstrapper) {
        return context -> new WbsBootstrapper<>(context, clazz) {
            @Override
            public void bootstrap(BootstrapContext context) {
                bootstrapper.accept(context);
            }
        };
    }

    @Override
    public final void bootstrap(BootstrapContext context) {
        getHandlerBuilder().apply(context).bootstrap(context);
    }

    public abstract Function<BootstrapContext, WbsBootstrapper<T>> getHandlerBuilder();

    // Wrapping the bootstrap method in a way that the context is available at construction -- so it can be stored as a variable safely,
    // and referenced in utility methods
    public static abstract class WbsBootstrapper<T extends WbsPlugin> {
        private final Class<T> clazz;
        private final BootstrapContext context;

        public WbsBootstrapper(BootstrapContext context, Class<T> clazz) {
            this.context = context;
            this.clazz = clazz;
        }

        public BootstrapContext getContext() {
            return context;
        }

        public Class<T> getClazz() {
            return clazz;
        }

        protected void saveResourceFolder(String folderName, boolean replace) {
            WbsFileUtil.saveResourceFolder(context, clazz, folderName, replace);
        }


        protected <K extends Keyed> void registerCustomTags(RegistryKey<K> key,
                                                            Supplier<Set<CustomTag<K>>> tags) {
            LifecycleEventManager<@NotNull BootstrapContext> manager = context.getLifecycleManager();

            manager.registerEventHandler(LifecycleEvents.TAGS.preFlatten(key).newHandler(event ->
                    tags.get().stream().sorted(Comparator.comparingInt(CustomTag::priority)).forEach(tag ->
                            tag.register(event.registrar())
                    )
            ));
            manager.registerEventHandler(LifecycleEvents.TAGS.postFlatten(key).newHandler(event ->
                    tags.get().stream().sorted(Comparator.comparingInt(CustomTag::priority)).forEach(tag ->
                            tag.register(event.registrar())
                    )
            ));
        }

        public abstract void bootstrap(BootstrapContext context);
    }
}
