package wbs.utils.util.entities.state.tracker;

import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.entities.state.EntityState;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class VelocityState implements EntityState<Entity> {

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
}
