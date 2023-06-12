package wbs.utils.util.entities.state.tracker;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.WbsPlayerUtil;
import wbs.utils.util.entities.state.EntityState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An {@link EntityState} that captures a {@link Player}'s current XP level.
 * @see Player#getExp()
 */
@SuppressWarnings("unused")
public class XPState implements EntityState<Player>, ConfigurationSerializable {

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

    // Serialization
    private static final String XP = "xp";

    public static XPState deserialize(Map<String, Object> args) {
        Object xp = args.get(XP);
        if (xp instanceof Integer) {
            return new XPState((int) xp);
        }
        return new XPState();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(XP, xp);

        return map;
    }
}
