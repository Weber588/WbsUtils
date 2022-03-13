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
public class SaturationState implements EntityState<Player>, ConfigurationSerializable {

    private float saturation = 20;

    public SaturationState() {}
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
