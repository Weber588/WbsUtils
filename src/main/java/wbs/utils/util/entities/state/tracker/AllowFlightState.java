package wbs.utils.util.entities.state.tracker;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class AllowFlightState implements EntityState<Player> {

    private boolean allowFlight = false;

    public AllowFlightState() {}
    public AllowFlightState(boolean allowFlight) {
        this.allowFlight = allowFlight;
    }

    @Override
    public void captureState(Player target) {
        allowFlight = target.getAllowFlight();
    }

    @Override
    public void restoreState(Player target) {
        target.setAllowFlight(allowFlight);
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        return new HashSet<>(Collections.singletonList(GameModeState.class));
    }
}
