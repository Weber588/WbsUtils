package wbs.utils.util.pluginhooks.region;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class WorldGuardRegionHook extends WbsRegionHook {
    @Override
    public boolean canPlayerBuild(Location loc, Player player) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        World world = localPlayer.getWorld();
        boolean canBypass = WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, world);
        if (canBypass) return true;

        com.sk89q.worldedit.util.Location checkLoc = BukkitAdapter.adapt(loc);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        return query.testState(checkLoc, localPlayer, Flags.BUILD);
    }

    @Override
    public boolean canDealDamage(Entity attacker, Entity victim) {
        if (attacker instanceof Player) {
            Player playerAttacker = (Player) attacker;

            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(playerAttacker);
            World world = localPlayer.getWorld();
            boolean canBypass = WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, world);
            if (canBypass) return true;

            com.sk89q.worldedit.util.Location checkLoc = BukkitAdapter.adapt(victim.getLocation());
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            return query.testState(checkLoc, localPlayer, Flags.MOB_DAMAGE);
        }

        return true;
    }

    @Override
    public String getRequiredPlugin() {
        return "WorldGuard";
    }
}
