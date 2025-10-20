package wbs.utils.util.configuration;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ConfigurableContext {
    private final Location location;
    @Nullable
    private final Entity entity;

    public ConfigurableContext(Location location) {
        this.entity = null;
        this.location = location;
    }
    public ConfigurableContext(Entity entity) {
        this.entity = entity;
        this.location = entity.getLocation();
    }

    public Location getLocation() {
        return location.clone();
    }

    public @Nullable Entity getEntity() {
        return entity;
    }

    public World getWorld() {
        return location.getWorld();
    }

    public Block getBlock() {
        return location.getBlock();
    }
}
