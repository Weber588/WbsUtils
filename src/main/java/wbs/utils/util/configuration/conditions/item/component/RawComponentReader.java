package wbs.utils.util.configuration.conditions.item.component;

import io.papermc.paper.datacomponent.DataComponentType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class RawComponentReader<T> implements ItemComponentReader<T> {
    private final DataComponentType.Valued<T> componentType;

    public RawComponentReader(DataComponentType.Valued<T> componentType) {
        this.componentType = componentType;
    }

    @Override
    @Nullable
    public T read(ItemStack item) {
        return item.getData(componentType);
    }
}
