package wbs.utils.util.entities.state.tracker;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;

import java.util.*;

@SuppressWarnings("unused")
public class FlyingState implements EntityState<Player>, ConfigurationSerializable {

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

    // Serialization
    private static final String FLYING = "flying";

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
