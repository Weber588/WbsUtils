package wbs.utils.util.entities.state.tracker;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class FlyingState implements EntityState<Player> {

    private boolean flying = false;

    public FlyingState() {}
    public FlyingState(boolean flying) {
        this.flying = flying;
    }

    @Override
    public void captureState(Player target) {
        flying = target.isFlying();
    }

    @Override
    public void restoreState(Player target) {
        target.setFlying(flying);
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        return new HashSet<>(Arrays.asList(LocationState.class, GameModeState.class));
    }
}
