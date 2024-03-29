package wbs.utils.util.pluginhooks.region;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import wbs.utils.util.pluginhooks.PluginHookManager;

public class TownyRegionHook extends WbsRegionHook {

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final Towny towny;
    public TownyRegionHook() {
        towny = PluginHookManager.getTowny();
    }

    @Override
    public boolean canPlayerBuild(Location loc, Player player) {
        return PlayerCacheUtil.getCachePermission(player,
                loc,
                loc.getBlock().getType(),
                TownyPermission.ActionType.BUILD);
    }

    @Override
    public boolean canDealDamage(Entity attacker, Entity victim) {
        return CombatUtil.preventDamageCall(attacker, victim, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
    }

    @Override
    public String getRequiredPlugin() {
        return "Towny";
    }
}
