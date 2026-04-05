package wbs.utils.util;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public class WbsRegistryUtil {
    public static <T extends Keyed> boolean isTagged(T value, TagKey<T> tag) {
        RegistryKey<T> key = tag.registryKey();
        return RegistryAccess.registryAccess().getRegistry(key).getTag(tag).contains(TypedKey.create(key, value.getKey()));
    }

    public static Set<Tag<@NotNull ItemType>> getItemTagsWith(ItemStack itemStack) {
        return getItemTagsWith(itemStack.getType());
    }
    public static Set<Tag<@NotNull ItemType>> getItemTagsWith(Material material) {
        ItemType itemType = material.asItemType();

        if (itemType == null) {
            return new HashSet<>();
        }

        return getTagsWith(itemType, RegistryKey.ITEM);
    }

    public static Set<Tag<@NotNull BlockType>> getBlockTagsWith(Block block) {
        return getBlockTagsWith(block.getType());
    }
    public static Set<Tag<@NotNull BlockType>> getBlockTagsWith(Material material) {
        BlockType blockType = material.asBlockType();

        if (blockType == null) {
            return new HashSet<>();
        }

        return getTagsWith(blockType, RegistryKey.BLOCK);
    }

    public static <T extends Keyed> Set<Tag<@NotNull T>> getTagsWith(T value, RegistryKey<T> key) {
        Registry<@NotNull T> registry = RegistryAccess.registryAccess().getRegistry(key);

        Set<Tag<@NotNull T>> includedIn = new HashSet<>();

        for (Tag<@NotNull T> tag : registry.getTags()) {
            if (tag.contains(TypedKey.create(key, value.getKey()))) {
                includedIn.add(tag);
            }
        }

        return includedIn;
    }
}
