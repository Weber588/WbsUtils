package wbs.utils.util.entities.state.tracker;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

@SuppressWarnings("unused")
public class PotionEffectsState implements EntityState<LivingEntity> {

    @NotNull
    private final Collection<PotionEffect> effects = new LinkedList<>();

    public PotionEffectsState() {}
    public PotionEffectsState(@NotNull Collection<PotionEffect> effects) {
        this.effects.addAll(effects);
    }

    @Override
    public void captureState(LivingEntity target) {
        this.effects.clear();
        this.effects.addAll(target.getActivePotionEffects());
    }

    @Override
    public void restoreState(LivingEntity target) {
        target.getActivePotionEffects().forEach(
                effect -> target.removePotionEffect(effect.getType())
        );

        effects.forEach(target::addPotionEffect);
    }

    public @NotNull Collection<PotionEffect> getEffects() {
        return effects;
    }

    public void setEffects(@NotNull Collection<PotionEffect> effects) {
        this.effects.clear();
        this.effects.addAll(effects);
    }

    public void addEffect(@NotNull PotionEffect effect) {
        effects.add(effect);
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        return new HashSet<>();
    }
}
