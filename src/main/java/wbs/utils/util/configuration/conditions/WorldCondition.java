package wbs.utils.util.configuration.conditions;

import org.bukkit.Location;
import org.bukkit.World;

public interface WorldCondition extends LocationCondition, ConfigurableCondition {
    boolean testWorld(World world);

    @Override
    default boolean testLocation(Location location) {
        return testWorld(location.getWorld());
    }
}
