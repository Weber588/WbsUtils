package wbs.utils.util.entities.state.tracker;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class FallDistanceState implements EntityState<Entity>, ConfigurationSerializable {

    private float fallDistance;
    
    public FallDistanceState() {}
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

    public float getFallDistance() {
        return fallDistance;
    }

    public void setFallDistance(float fallDistance) {
        this.fallDistance = fallDistance;
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        return new HashSet<>();
    }

    // Serialization
    private static final String FALL_DISTANCE = "fall-distance";

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
