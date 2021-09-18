package wbs.utils.util.entities.state.tracker;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class HealthState implements EntityState<LivingEntity> {

    private double health = 20;

    public HealthState() {}
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

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        // Capture before potion effects are removed, and restore after they are. This allows absorption to maintain health.
        return new HashSet<>(Collections.singletonList(PotionEffectsState.class));
    }
}
