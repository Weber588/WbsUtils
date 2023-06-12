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
 * An {@link EntityState} that captures a {@link Player}'s current saturation level.
 * @see Player#getSaturation()
 */
@SuppressWarnings("unused")
public class SaturationState implements EntityState<Player>, ConfigurationSerializable {

    private float saturation = 20;

    /**
     * Creates the state with the default value for a player.
     */
    public SaturationState() {}

    /**
     * @param saturation How much saturation the player has before they will start losing hunger points
     */
    public SaturationState(float saturation) {
        this.saturation = saturation;
    }

    @Override
    public void captureState(Player target) {
        saturation = target.getSaturation();
    }

    @Override
    public void restoreState(Player target) {
        target.setSaturation(saturation);
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        return new HashSet<>();
    }

    // Serialization
    private static final String SATURATION = "saturation";

    /**
     * Deserializer method for converting a {@link Map} into this object, for use in {@link EntityStateManager}
     * @param args The partially deserialized Map.
     * @return An instance of this class, deserialized from args.
     */
    public static SaturationState deserialize(Map<String, Object> args) {
        Object saturation = args.get(SATURATION);
        if (saturation instanceof Float) {
            return new SaturationState((float) saturation);
        }
        return new SaturationState();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(SATURATION, saturation);

        return map;
    }
}
