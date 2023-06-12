package wbs.utils.util.entities.state.tracker;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;
import wbs.utils.util.entities.state.EntityStateManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An {@link EntityState} that captures an {@link Entity}'s current distance fallen, used
 * for damage calculations (among other uses).
 * @see Entity#getFallDistance()
 */
@SuppressWarnings("unused")
public class FallDistanceState implements EntityState<Entity>, ConfigurationSerializable {

    private float fallDistance;

    /**
     * Creates the state with the default value (0).
     */
    public FallDistanceState() {}

    /**
     * @param fallDistance How far the entity has fallen.
     */
    public FallDistanceState(float fallDistance) {
        this.fallDistance = fallDistance;
    }

    @Override
    public void captureState(Entity target) {
        fallDistance = target.getFallDistance();
    }

    @Override
    public void restoreState(Entity target) {
        target.setFallDistance(fallDistance);
    }

    /**
     * @return How far the entity has fallen.
     */
    public float getFallDistance() {
        return fallDistance;
    }

    /**
     * @param fallDistance How far the entity has fallen.
     */
    public void setFallDistance(float fallDistance) {
        this.fallDistance = fallDistance;
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        return new HashSet<>();
    }

    // Serialization
    private static final String FALL_DISTANCE = "fall-distance";

    /**
     * Deserializer method for converting a {@link Map} into this object, for use in {@link EntityStateManager}
     * @param args The partially deserialized Map.
     * @return An instance of this class, deserialized from args.
     */
    public static FallDistanceState deserialize(Map<String, Object> args) {
        Object fallDistance = args.get(FALL_DISTANCE);
        if (fallDistance instanceof Float) {
            return new FallDistanceState((float) fallDistance);
        }
        return new FallDistanceState();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(FALL_DISTANCE, fallDistance);

        return map;
    }
}
