package wbs.utils.util.entities.state.tracker;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.entities.state.EntityState;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class ScoreboardState implements EntityState<Player> {

    @Nullable
    private Scoreboard scoreboard;

    public ScoreboardState() {}
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

    public @Nullable Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(@Nullable Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        return new HashSet<>();
    }
}
