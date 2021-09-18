package wbs.utils.util.entities.state;

import org.bukkit.entity.Player;
import wbs.utils.util.entities.state.tracker.*;

@SuppressWarnings("unused")
public class SavedPlayerState<T extends Player> extends SavedLivingEntityState<T> {
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
