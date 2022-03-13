package wbs.utils.util.entities.state.tracker;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class FireTicksState implements EntityState<Entity>, ConfigurationSerializable {

    private int fireTicks = 0;

    public FireTicksState() {}
    public FireTicksState(int fireTicks) {
        this.fireTicks = fireTicks;
    }

    @Override
    public void captureState(Entity target) {
        fireTicks = target.getFireTicks();
    }

    @Override
    public void restoreState(Entity target) {
        target.setFireTicks(fireTicks);
    }

    public int getFireTicks() {
        return fireTicks;
    }

    public void setFireTicks(int fireTicks) {
        this.fireTicks = fireTicks;
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        return new HashSet<>();
    }

    // Serialization
    private static final String FIRE_TICKS = "fire-ticks";

    public static FireTicksState deserialize(Map<String, Object> args) {
        Object fireTicks = args.get(FIRE_TICKS);
        if (fireTicks instanceof Integer) {
            return new FireTicksState((int) fireTicks);
        }
        return new FireTicksState();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(FIRE_TICKS, fireTicks);

        return map;
    }
}
