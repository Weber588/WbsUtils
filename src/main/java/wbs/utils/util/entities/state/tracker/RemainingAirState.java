package wbs.utils.util.entities.state.tracker;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;
import wbs.utils.util.entities.state.EntityStateManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An {@link EntityState} that captures a {@link LivingEntity}'s remaining air, used to determine
 * when it will start drowning.
 * @see LivingEntity#getHealth()
 */
public class RemainingAirState implements EntityState<LivingEntity>, ConfigurationSerializable {

    // 15 seconds of air by default for a player
    private int remainingAir = 15 * 20;

    /**
     * Creates the state with the default value for a player.
     */
    public RemainingAirState() {}

    /**
     * @param remainingAir How much air, in ticks, the entity has before it will start drowning.
     */
    public RemainingAirState(int remainingAir) {
        this.remainingAir = remainingAir;
    }

    @Override
    public void captureState(LivingEntity target) {
        remainingAir = target.getRemainingAir();
    }

    @Override
    public void restoreState(LivingEntity target) {
        target.setRemainingAir(remainingAir);
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        return new HashSet<>();
    }

    // Serialization
    private static final String REMAINING_AIR = "remaining-air";

    /**
     * Deserializer method for converting a {@link Map} into this object, for use in {@link EntityStateManager}
     * @param args The partially deserialized Map.
     * @return An instance of this class, deserialized from args.
     */
    public static RemainingAirState deserialize(Map<String, Object> args) {
        Object remainingAir = args.get(REMAINING_AIR);
        if (remainingAir instanceof Integer) {
            return new RemainingAirState((int) remainingAir);
        }
        return new RemainingAirState();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(REMAINING_AIR, remainingAir);

        return map;
    }
}
