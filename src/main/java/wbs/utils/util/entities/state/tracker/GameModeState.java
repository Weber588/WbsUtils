package wbs.utils.util.entities.state.tracker;

import org.bukkit.GameMode;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.entities.state.EntityState;

import java.util.*;

/**
 * An {@link EntityState} that captures the game mode a {@link Player} is currently in.
 * @see Player#getGameMode()
 */
@SuppressWarnings("unused")
public class GameModeState implements EntityState<Player>, ConfigurationSerializable {

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

    // Serialization
    private static final String GAME_MODE = "game-mode";

    public static GameModeState deserialize(Map<String, Object> args) {
        Object mode = args.get(GAME_MODE);
        if (mode instanceof Integer) {
            return new GameModeState(GameMode.values()[(int) mode]);
        }
        return new GameModeState();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        if (mode != null) {
            map.put(GAME_MODE, mode.ordinal());
        }

        return map;
    }
}
