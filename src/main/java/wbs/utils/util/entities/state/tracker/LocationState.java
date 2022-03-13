package wbs.utils.util.entities.state.tracker;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.entities.state.EntityState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class LocationState implements EntityState<Entity>, ConfigurationSerializable {

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

    // Serialization
    private static final String LOCATION = "location";

    public static LocationState deserialize(Map<String, Object> args) {
        Object location = args.get(LOCATION);
        if (location instanceof Location) {
            return new LocationState((Location) location);
        }
        return new LocationState();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(LOCATION, location);

        return map;
    }
}
