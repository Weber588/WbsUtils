package wbs.utils.util.pluginhooks.region;

import com.plotsquared.bukkit.BukkitPlatform;
import com.plotsquared.bukkit.util.BukkitEntityUtil;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.permissions.Permission;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.util.Permissions;
import com.plotsquared.core.util.PlayerManager;
import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlotSquaredRegionHook extends WbsRegionHook {
    @Override
    public boolean canPlayerBuild(Location loc, Player player) {
        PlayerManager<? extends PlotPlayer<Player>, ? extends Player> playerManager = BukkitPlatform.getPlugin(BukkitPlatform.class).playerManager();
        PlotPlayer<?> plotPlayer = playerManager.getPlayer(player.getUniqueId());

        com.plotsquared.core.location.Location checkLocation = BukkitUtil.adapt(loc);

        PlotArea area = checkLocation.getPlotArea();
        if (area == null) {
            return true;
        }

        Plot plot = checkLocation.getPlot();

        if (plot == null) {
            return Permissions.hasPermission(plotPlayer, Permission.PERMISSION_ADMIN_BUILD_ROAD, false);
        } else {
            if (!plot.getTrusted().contains(plotPlayer.getUUID())) {
                if (!plot.getOwners().contains(plotPlayer.getUUID())) {
                    if (!plot.getMembers().contains(plotPlayer.getUUID())) {
                        return Permissions.hasPermission(plotPlayer, Permission.PERMISSION_ADMIN_BUILD_OTHER, false);
                    } else { // They are a member but not trusted or owner, an owner must be online
                        boolean isOwnerOnline = false;
                        for (UUID ownerUUID : plot.getOwners()) {
                            PlotPlayer<? extends Player> owner = playerManager.getPlayer(ownerUUID);

                            if (owner.getPlatformPlayer().isOnline()) {
                                isOwnerOnline = true;
                                break;
                            }
                        }

                        return isOwnerOnline;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean canDealDamage(Entity attacker, Entity victim) {
        return BukkitEntityUtil.entityDamage(attacker, victim);
    }

    @Override
    public String getRequiredPlugin() {
        return "PlotSquared";
    }

    @Override
    public boolean enabled() {
        boolean enabled = super.enabled();

        if (enabled) {
            enabled = PlotSquared.get().getVersion().version[0] >= 6;
        }

        return enabled;
    }
}
