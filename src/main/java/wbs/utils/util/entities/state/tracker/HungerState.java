package wbs.utils.util.entities.state.tracker;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class HungerState implements EntityState<Player>, ConfigurationSerializable {

    private int hunger = 20;

    public HungerState() {}
    public HungerState(int hunger) {
        this.hunger = hunger;
    }

    @Override
    public void captureState(Player target) {
        hunger = target.getFoodLevel();
    }

    @Override
    public void restoreState(Player target) {
        target.setFoodLevel(hunger);
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        return new HashSet<>();
    }

    // Serialization
    private static final String HUNGER = "hunger";

    public static HungerState deserialize(Map<String, Object> args) {
        Object hunger = args.get(HUNGER);
        if (hunger instanceof Integer) {
            return new HungerState((int) hunger);
        }
        return new HungerState();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(HUNGER, hunger);

        return map;
    }
}
