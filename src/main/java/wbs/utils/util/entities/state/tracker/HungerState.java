package wbs.utils.util.entities.state.tracker;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class HungerState implements EntityState<Player> {

    private int hunger = 20;

    public HungerState() {}
    public HungerState(int hunger) {
        this.hunger = hunger;
    }

    @Override
    public void captureState(Player target) {
        hunger = target.getFoodLevel();
    }

    @Override
    public void restoreState(Player target) {
        target.setFoodLevel(hunger);
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        return new HashSet<>();
    }
}
