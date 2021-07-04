package wbs.utils.util.pluginhooks.region;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class GriefPreventionRegionHook implements WbsRegionHook {
    @Override
    public boolean canPlayerBuild(Location loc, Player player) {
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, true, playerData.lastClaim);
        if (claim != null) {
            return claim.allowBuild(player, player.getInventory().getItemInMainHand().getType()) == null;
        }

        return true;
    }

    @Override
    public boolean canDealDamage(Entity attacker, Entity victim) {
        if (!(attacker instanceof Player)) {
            return true;
        }

        Player player = (Player) attacker;

        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(victim.getLocation(), true, playerData.lastClaim);
        if (claim != null) {
            return claim.allowAccess(player) == null;
        }

        return true;
    }

    @Override
    public String getRequiredPlugin() {
        return "GriefPrevention";
    }
}
