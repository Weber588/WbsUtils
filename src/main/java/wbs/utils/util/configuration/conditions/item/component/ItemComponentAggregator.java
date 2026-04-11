package wbs.utils.util.configuration.conditions.item.component;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.plugin.WbsSettings;

import java.util.LinkedList;
import java.util.List;

public abstract class ItemComponentAggregator<T> implements ItemComponentReader<T> {
    private final List<ItemComponentReader<T>> aggregators = new LinkedList<>();

    public ItemComponentAggregator(ConfigurationSection section, @Nullable WbsSettings settings, @Nullable String directory) {

    }
}
