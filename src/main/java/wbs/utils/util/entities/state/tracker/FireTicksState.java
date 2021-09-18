package wbs.utils.util.entities.state.tracker;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class FireTicksState implements EntityState<Entity> {

    private int fireTicks = 0;

    public FireTicksState() {}
    public FireTicksState(int fireTicks) {
        this.fireTicks = fireTicks;
    }

    @Override
    public void captureState(Entity target) {
        fireTicks = target.getFireTicks();
    }

    @Override
    public void restoreState(Entity target) {
        target.setFireTicks(fireTicks);
    }

    public int getFireTicks() {
        return fireTicks;
    }

    public void setFireTicks(int fireTicks) {
        this.fireTicks = fireTicks;
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        return new HashSet<>();
    }
}
