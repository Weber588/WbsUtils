package wbs.utils.util;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Keyed;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public class WbsRegistryUtil {
    public static <T extends Keyed> boolean isTagged(T value, TagKey<T> tag) {
        RegistryKey<T> key = tag.registryKey();
        return RegistryAccess.registryAccess().getRegistry(key).getTag(tag).contains(TypedKey.create(key, value.getKey()));
    }
}
