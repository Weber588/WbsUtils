package wbs.utils.util.entities.state.tracker;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;
import wbs.utils.util.entities.state.EntityStateManager;

import java.util.*;

/**
 * An {@link EntityState} that captures a {@link LivingEntity}'s current health.
 * @see LivingEntity#getHealth()
 */
@SuppressWarnings("unused")
public class HealthState implements EntityState<LivingEntity>, ConfigurationSerializable {

    private double health = 20;

    /**
     * Creates the state with the default value (20).
     */
    public HealthState() {}

    /**
     * @param health The amount of health (in half hearts)
     */
    public HealthState(double health) {
        this.health = health;
    }

    @Override
    public void captureState(LivingEntity target) {
        health = target.getHealth();
    }

    @Override
    public void restoreState(LivingEntity target) {
        target.setHealth(health);
    }

    /**
     * @return The amount of health (in half hearts)
     */
    public double getHealth() {
        return health;
    }

    /**
     * @param health The amount of health (in half hearts)
     */
    public void setHealth(double health) {
        this.health = health;
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        // Capture before potion effects are removed, and restore after they are. This allows absorption to maintain health.
        return new HashSet<>(Collections.singletonList(PotionEffectsState.class));
    }

    // Serialization
    private static final String HEALTH = "health";

    /**
     * Deserializer method for converting a {@link Map} into this object, for use in {@link EntityStateManager}
     * @param args The partially deserialized Map.
     * @return An instance of this class, deserialized from args.
     */
    public static HealthState deserialize(Map<String, Object> args) {
        Object health = args.get(HEALTH);
        if (health instanceof Double) {
            return new HealthState((double) health);
        }
        return new HealthState();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(HEALTH, health);

        return map;
    }
}
