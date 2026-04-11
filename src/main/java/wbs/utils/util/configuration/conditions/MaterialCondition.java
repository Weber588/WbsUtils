package wbs.utils.util.configuration.conditions;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface MaterialCondition extends BlockCondition, ItemCondition, ConfigurableCondition {
    boolean testMaterial(Material material);

    @Override
    default boolean testBlock(Block block) {
        return testMaterial(block.getType());
    }

    @Override
    default boolean testItem(ItemStack item) {
        return testMaterial(item.getType());
    }
}
