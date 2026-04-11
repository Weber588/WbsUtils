package wbs.utils.util.configuration.conditions;

import org.bukkit.inventory.ItemStack;

public interface ItemCondition extends ConfigurableCondition {
    boolean testItem(ItemStack item);
}
