package wbs.utils.util.configuration.conditions;

import org.bukkit.block.Block;

public interface BlockCondition extends ConfigurableCondition {
    boolean testBlock(Block block);
}
