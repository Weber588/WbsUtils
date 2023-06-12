package wbs.utils.util.entities.state.tracker;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;
import wbs.utils.util.entities.state.EntityStateManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An {@link EntityState} that captures a {@link Player}'s current hunger level.
 * @see Player#getFoodLevel()
 */
@SuppressWarnings("unused")
public class HungerState implements EntityState<Player>, ConfigurationSerializable {

    private int hunger = 20;

    /**
     * Creates the state with the default value (20).
     */
    public HungerState() {}

    /**
     * @param hunger The amount of hunger points (where the player has 20 by default).
     */
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

    /**
     * Deserializer method for converting a {@link Map} into this object, for use in {@link EntityStateManager}
     * @param args The partially deserialized Map.
     * @return An instance of this class, deserialized from args.
     */
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
