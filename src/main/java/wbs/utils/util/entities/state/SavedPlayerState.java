package wbs.utils.util.entities.state;

import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.entity.Player;
import wbs.utils.util.entities.state.tracker.*;

@DelegateDeserialization(SavedEntityState.class)
@SuppressWarnings("unused")
public class SavedPlayerState extends SavedLivingEntityState<Player> {
    @Override
    public void trackAll() {
        super.trackAll();
        trackIfAbsent(new AllowFlightState());
        trackIfAbsent(new FlyingState());
        trackIfAbsent(new GameModeState());
        trackIfAbsent(new HungerState());
        trackIfAbsent(new InventoryState());
        trackIfAbsent(new SaturationState());
        trackIfAbsent(new ScoreboardState());
        trackIfAbsent(new XPState());
    }
}
