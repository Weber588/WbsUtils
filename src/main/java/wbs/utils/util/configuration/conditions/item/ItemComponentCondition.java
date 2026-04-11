package wbs.utils.util.configuration.conditions.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.configuration.conditions.ItemCondition;
import wbs.utils.util.configuration.conditions.item.component.ItemComponentReader;
import wbs.utils.util.configuration.conditions.item.component.ItemComponentReaderManager;
import wbs.utils.util.plugin.WbsSettings;

///```yaml
/// component:
///     operation: compare
///     comparison: >
///     compare-value: 5
///     component-type: "minecraft:max_damage"
///
/// ```
public class ItemComponentCondition implements ItemCondition {


    private Operation operation;
    @Nullable
    private Comparison comparison;
    private final ItemComponentReader<?> reader;

    public ItemComponentCondition(ConfigurationSection parent, String key, @Nullable WbsSettings settings, @Nullable String directory) {


        reader = ItemComponentReaderManager.buildReader(parent, key, settings, directory);

        DataComponentTypes.ATTRIBUTE_MODIFIERS
    }

    @Override
    public boolean testItem(ItemStack item) {
        return false;
    }

    public enum Operation {
        PRESENT,
        NOT_PRESENT,
        COMPARE
    }

    public enum Comparison {
        PRESENT,
        VALUE_GREATER_THAN,
        VALUE_EQUAL,
        VALUE_LESS_THAN,
    }
}
