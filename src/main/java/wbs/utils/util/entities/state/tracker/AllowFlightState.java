package wbs.utils.util.entities.state.tracker;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;

import java.util.*;

/**
 * An {@link EntityState} that captures a {@link Player}'s ability to fly, but not
 * whether they were flying when the state was captured.
 * @see Player#getAllowFlight()
 */
@SuppressWarnings("unused")
public class AllowFlightState implements EntityState<Player>, ConfigurationSerializable {
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

    // Serialization
    private static final String ALLOW_FLIGHT = "allow-flight";

    public static AllowFlightState deserialize(Map<String, Object> args) {
        Object allowFlight = args.get(ALLOW_FLIGHT);
        if (allowFlight instanceof Boolean) {
            return new AllowFlightState(allowFlight == Boolean.TRUE);
        }
        return new AllowFlightState();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(ALLOW_FLIGHT, allowFlight);

        return map;
    }
}
