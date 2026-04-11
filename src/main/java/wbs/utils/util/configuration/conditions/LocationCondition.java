package wbs.utils.util.configuration.conditions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public interface LocationCondition extends EntityCondition<Entity>, BlockCondition {
    boolean testLocation(Location location);

    @Override
    default boolean testEntity(Entity entity) {
        return testLocation(entity.getLocation());
    }

    @Override
    default boolean testBlock(Block block) {
        return testLocation(block.getLocation());
    }
}
