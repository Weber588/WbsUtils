package wbs.utils.util.entities.state.tracker;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class FallDistanceState implements EntityState<Entity> {

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
}
