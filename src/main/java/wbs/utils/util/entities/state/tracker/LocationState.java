package wbs.utils.util.entities.state.tracker;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.entities.state.EntityState;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class LocationState implements EntityState<Entity> {

    @Nullable
    private Location location;

    public LocationState() {}
    public LocationState(@Nullable Location location) {
        this.location = location;
    }

    @Override
    public void captureState(Entity target) {
        location = target.getLocation();
    }

    @Override
    public void restoreState(Entity target) {
        if (location != null) {
            target.teleport(location);
        }
    }

    public @Nullable Location getLocation() {
        return location;
    }

    public void setLocation(@Nullable Location location) {
        this.location = location;
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        return new HashSet<>();
    }
}
