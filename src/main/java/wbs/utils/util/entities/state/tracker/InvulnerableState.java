package wbs.utils.util.entities.state.tracker;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class InvulnerableState implements EntityState<Entity> {

    private boolean invulnerable = false;

    public InvulnerableState() {}
    public InvulnerableState(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    @Override
    public void captureState(Entity target) {
        invulnerable = target.isInvulnerable();
    }

    @Override
    public void restoreState(Entity target) {
        target.setInvulnerable(invulnerable);
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        return new HashSet<>();
    }
}
