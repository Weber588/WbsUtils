package wbs.utils.util.entities.state.tracker;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class SaturationState implements EntityState<Player> {

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
}