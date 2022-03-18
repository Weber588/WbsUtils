package wbs.utils.util.entities.state;

import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.entity.LivingEntity;
import wbs.utils.util.entities.state.tracker.HealthState;
import wbs.utils.util.entities.state.tracker.PotionEffectsState;

@DelegateDeserialization(SavedEntityState.class)
public class SavedLivingEntityState<T extends LivingEntity> extends SavedEntityState<T> {
    @Override
    public void trackAll() {
        super.trackAll();
        trackIfAbsent(new HealthState());
        trackIfAbsent(new PotionEffectsState());
    }
}
