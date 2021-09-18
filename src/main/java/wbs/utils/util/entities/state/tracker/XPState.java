package wbs.utils.util.entities.state.tracker;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.WbsPlayerUtil;
import wbs.utils.util.entities.state.EntityState;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class XPState implements EntityState<Player> {

    int xp;

    public XPState() {}
    public XPState(int xp) {
        this.xp = xp;
    }

    @Override
    public void captureState(Player target) {
        xp = WbsPlayerUtil.getExp(target);
    }

    @Override
    public void restoreState(Player target) {
        WbsPlayerUtil.setExp(target, xp);
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        return new HashSet<>();
    }
}
