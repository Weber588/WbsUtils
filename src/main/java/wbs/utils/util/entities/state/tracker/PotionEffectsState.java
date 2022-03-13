package wbs.utils.util.entities.state.tracker;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;

import java.util.*;

@SuppressWarnings("unused")
public class PotionEffectsState implements EntityState<LivingEntity>, ConfigurationSerializable {

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

    // Serialization
    private static final String POTION_EFFECTS = "potion-effects";

    public static PotionEffectsState deserialize(Map<String, Object> args) {
        Object potionEffects = args.get(POTION_EFFECTS);
        if (potionEffects instanceof Collection) {
            Collection<?> collection = (Collection<?>) potionEffects;

            List<PotionEffect> effects = new LinkedList<>();
            for (Object check : collection) {
                if (check instanceof PotionEffect) {
                    effects.add((PotionEffect) check);
                }
            }

            return new PotionEffectsState(effects);
        }
        return new PotionEffectsState();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(POTION_EFFECTS, effects);

        return map;
    }
}
