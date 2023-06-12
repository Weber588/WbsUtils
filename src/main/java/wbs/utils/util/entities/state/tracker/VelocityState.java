package wbs.utils.util.entities.state.tracker;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.entities.state.EntityState;

import java.util.*;

/**
 * An {@link EntityState} that captures an {@link Entity}'s current velocity.
 * @see Entity#getVelocity()
 */
@SuppressWarnings("unused")
public class VelocityState implements EntityState<Entity>, ConfigurationSerializable {

    @Nullable
    private Vector velocity;

    public VelocityState() {}
    public VelocityState(@NotNull Vector velocity) {
        this.velocity = velocity;
    }

    @Override
    public void captureState(Entity target) {
        velocity = target.getVelocity();
    }

    @Override
    public void restoreState(Entity target) {
        if (velocity != null) {
            target.setVelocity(velocity);
        }
    }

    public @Nullable Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(@Nullable Vector velocity) {
        this.velocity = velocity;
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        // Teleporting clears velocity, so restore afterwards
        return new HashSet<>(Collections.singletonList(LocationState.class));
    }

    // Serialization
    private static final String VELOCITY = "velocity";

    public static VelocityState deserialize(Map<String, Object> args) {
        Object velocity = args.get(VELOCITY);
        if (velocity instanceof Vector) {
            return new VelocityState((Vector) velocity);
        }
        return new VelocityState();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(VELOCITY, velocity);

        return map;
    }
}
