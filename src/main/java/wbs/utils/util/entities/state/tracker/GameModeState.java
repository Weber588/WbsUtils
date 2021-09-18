package wbs.utils.util.entities.state.tracker;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.entities.state.EntityState;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class GameModeState implements EntityState<Player> {

    @Nullable
    private GameMode mode;

    public GameModeState() {}
    public GameModeState(@Nullable GameMode mode) {
        this.mode = mode;
    }

    @Override
    public void captureState(Player target) {
        mode = target.getGameMode();
    }

    @Override
    public void restoreState(Player target) {
        if (mode != null) {
            target.setGameMode(mode);
        }
    }

    public @Nullable GameMode getMode() {
        return mode;
    }

    public void setMode(@Nullable GameMode mode) {
        this.mode = mode;
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        // As some world/region plugins manage game mode on teleport, restore after & capture before loc change.
        return new HashSet<>(Collections.singletonList(LocationState.class));
    }
}
