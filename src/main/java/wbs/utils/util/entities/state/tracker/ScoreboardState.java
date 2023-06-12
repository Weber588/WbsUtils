package wbs.utils.util.entities.state.tracker;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.entities.state.EntityState;

import java.util.HashSet;
import java.util.Set;

/**
 * An {@link EntityState} that captures which {@link Scoreboard} a {@link Player} is
 * currently viewing.
 * @see Player#getScoreboard()
 */
@SuppressWarnings("unused")
public class ScoreboardState implements EntityState<Player> {

    @Nullable
    private Scoreboard scoreboard;

    /**
     * Creates the state with no scoreboard configured.
     */
    public ScoreboardState() {}

    /**
     * @param scoreboard The scoreboard shown to the player.
     */
    public ScoreboardState(@Nullable Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public void captureState(Player target) {
        scoreboard = target.getScoreboard();
    }

    @Override
    public void restoreState(Player target) {
        if (scoreboard != null) {
            target.setScoreboard(scoreboard);
        }
    }

    /**
     * @return The scoreboard shown to the player.
     */
    public @Nullable Scoreboard getScoreboard() {
        return scoreboard;
    }

    /**
     * @param scoreboard The scoreboard shown to the player.
     */
    public void setScoreboard(@Nullable Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        return new HashSet<>();
    }
}
