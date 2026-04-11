package wbs.utils.util.configuration.conditions.item.component;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ItemComponentReader<T> {
    @Nullable
    T read(ItemStack item);
}
