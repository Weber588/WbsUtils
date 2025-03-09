package wbs.utils.util;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.PostFlattenTagRegistrar;
import io.papermc.paper.tag.PreFlattenTagRegistrar;
import io.papermc.paper.tag.TagEntry;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import wbs.utils.exceptions.InvalidConfigurationException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A util class for dealing with registry tags, primarily for bootstrapping them via config.
 */
@ApiStatus.Experimental
@SuppressWarnings({"UnstableApiUsage", "unused", "SameParameterValue"})
public final class WbsTagUtil {
    private WbsTagUtil() {}

    public static <T extends Keyed> CustomTag<T> createTag(@NotNull String defaultNamespace,
                                                           @NotNull ConfigurationSection section,
                                                           @NotNull String directory) throws InvalidConfigurationException {
        String stringTagKey = section.getString("tag-key", section.getName());

        NamespacedKey key = WbsKeyed.parseKey(stringTagKey, defaultNamespace);

        if (key == null) {
            throw new InvalidConfigurationException("Invalid key '%s'".formatted(stringTagKey), directory);
        }

        return null;
    }

    public static <T extends Keyed> void scheduleRegisterTags(@NotNull BootstrapContext context,
                                                              RegistryKey<T> key,
                                                              Set<CustomTag<T>> tags) {
        scheduleRegisterTags(context, key, tags, 100);
    }

    public static <T extends Keyed> void scheduleRegisterTags(@NotNull BootstrapContext context,
                                                              RegistryKey<T> key,
                                                              Set<CustomTag<T>> tags,
                                                              int priority) {
        LifecycleEventManager<@NotNull BootstrapContext> manager = context.getLifecycleManager();

        manager.registerEventHandler(
                LifecycleEvents.TAGS.preFlatten(key).newHandler(event ->
                        tags.forEach(tag -> tag.register(event.registrar()))
                ).priority(priority)
        );

        manager.registerEventHandler(
                LifecycleEvents.TAGS.postFlatten(key).newHandler(event ->
                        tags.forEach(tag -> tag.register(event.registrar()))
                ).priority(priority)
        );
    }

    public static class CustomTag<T extends Keyed> {
        private final TagKey<T> key;
        private Collection<TypedKey<T>> typedKeys;
        private Collection<TagEntry<T>> tagEntries;
        private boolean merge = true;

        public CustomTag(TagKey<T> key) {
            this.key = key;
        }

        public CustomTag(TagKey<T> key, Collection<TypedKey<T>> keys) {
            this.key = key;
            this.typedKeys = keys;
        }

        @SafeVarargs
        public CustomTag(TagKey<T> key, TypedKey<T> ... keys) {
            this(key, Arrays.asList(keys));
        }

        public CustomTag(TagKey<T> key, Collection<TypedKey<T>> keys, Collection<TagEntry<T>> tagEntries) {
            this.key = key;
            this.typedKeys = keys;
            this.tagEntries = tagEntries;
        }

        public CustomTag<T> setTypedKeys(Collection<TypedKey<T>> typedKeys) {
            this.typedKeys = typedKeys;
            return this;
        }

        public CustomTag<T> setTagEntries(Collection<TagEntry<T>> tagEntries) {
            this.tagEntries = tagEntries;
            return this;
        }

        public CustomTag<T> setTagEntryKeys(Collection<TagKey<T>> tagEntries) {
            this.tagEntries = tagEntries.stream().map(TagEntry::tagEntry).collect(Collectors.toSet());
            return this;
        }

        public CustomTag<T> setMerge(boolean merge) {
            this.merge = merge;
            return this;
        }

        private void register(PreFlattenTagRegistrar<T> registrar) {
            if (tagEntries == null || tagEntries.isEmpty()) {
                return;
            }

            if (registrar.hasTag(key) && merge) {
                registrar.addToTag(key, tagEntries);
            } else {
                registrar.setTag(key, tagEntries);
            }
        }

        private void register(PostFlattenTagRegistrar<T> registrar) {
            RegistryKey<T> registryKey = registrar.registryKey();

            if (registrar.hasTag(key) && merge) {
                registrar.addToTag(key, typedKeys);
            } else {
                registrar.setTag(key, typedKeys);
            }
        }
    }
}
