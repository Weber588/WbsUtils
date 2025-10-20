package wbs.utils.util.plugin.bootstrap;


import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.PostFlattenTagRegistrar;
import io.papermc.paper.tag.PreFlattenTagRegistrar;
import io.papermc.paper.tag.TagEntry;
import org.bukkit.Keyed;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CustomTag<T extends Keyed> {
    private final TagKey<T> key;
    private int priority = 0;
    private @Nullable Collection<TypedKey<T>> typedKeys;
    private @Nullable Collection<TagEntry<T>> tagEntries;

    @SafeVarargs
    private static <T extends Keyed> CustomTag<T> getKeyTag(TagKey<T> key, TypedKey<T>... keys) {
        CustomTag<T> tag = new CustomTag<>(key);

        tag.typedKeys = List.of(keys);

        return tag;
    }

    private CustomTag(TagKey<T> key) {
        this.key = key;
    }

    private CustomTag(TagKey<T> key, Collection<TypedKey<T>> keys) {
        this.key = key;
        this.typedKeys = keys;
    }

    @SafeVarargs
    private CustomTag(TagKey<T> key, TypedKey<T>... keys) {
        this(key, Arrays.asList(keys));
    }

    private CustomTag(TagKey<T> key, Collection<TypedKey<T>> keys, Collection<TagEntry<T>> tagEntries) {
        this.key = key;
        this.typedKeys = keys;
        this.tagEntries = tagEntries;
    }

    public int priority() {
        return priority;
    }

    public CustomTag<T> priority(int priority) {
        this.priority = priority;
        return this;
    }

    public void register(PreFlattenTagRegistrar<T> registrar) {
        if (tagEntries == null || tagEntries.isEmpty()) {
            return;
        }

        if (registrar.hasTag(key)) {
            registrar.addToTag(key, tagEntries);
        } else {
            registrar.setTag(key, tagEntries);
        }
    }

    public void register(PostFlattenTagRegistrar<T> registrar) {
        if (registrar.hasTag(key) && typedKeys != null) {
            registrar.addToTag(key, typedKeys);
        } else if (typedKeys != null) {
            registrar.setTag(key, typedKeys);
        }
    }
}