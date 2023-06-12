package wbs.utils.util.entities.state.tracker;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;
import wbs.utils.util.entities.state.EntityStateManager;

import java.util.*;

/**
 * An {@link EntityState} that captures whether or not an {@link Player} is currently
 * flying.
 * @see Player#isFlying()
 */
@SuppressWarnings("unused")
public class FlyingState implements EntityState<Player>, ConfigurationSerializable {

    private boolean flying = false;

    /**
     * Creates the state with the default value (false).
     */
    public FlyingState() {}

    /**
     * @param flying Whether or not the player is flying.
     */
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

    // Serialization
    private static final String FLYING = "flying";

    /**
     * Deserializer method for converting a {@link Map} into this object, for use in {@link EntityStateManager}
     * @param args The partially deserialized Map.
     * @return An instance of this class, deserialized from args.
     */
    public static FlyingState deserialize(Map<String, Object> args) {
        Object flying = args.get(FLYING);
        if (flying instanceof Boolean) {
            return new FlyingState(flying == Boolean.TRUE);
        }
        return new FlyingState();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(FLYING, flying);

        return map;
    }
}
