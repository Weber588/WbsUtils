package wbs.utils.util.entities.state.tracker;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.entities.state.EntityState;
import wbs.utils.util.entities.state.EntityStateManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An {@link EntityState} that captures an {@link Entity}'s current location.
 * @see Entity#getLocation()
 */
@SuppressWarnings("unused")
public class LocationState implements EntityState<Entity>, ConfigurationSerializable {

    @Nullable
    private Location location;

    /**
     * Creates the state with no location tracked.
     */
    public LocationState() {}

    /**
     * @param location The entity's location.
     */
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

    /**
     * @return The entity's location.
     */
    public @Nullable Location getLocation() {
        return location;
    }

    /**
     * @param location The entity's location.
     */
    public void setLocation(@Nullable Location location) {
        this.location = location;
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        return new HashSet<>();
    }

    // Serialization
    private static final String LOCATION = "location";

    /**
     * Deserializer method for converting a {@link Map} into this object, for use in {@link EntityStateManager}
     * @param args The partially deserialized Map.
     * @return An instance of this class, deserialized from args.
     */
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
