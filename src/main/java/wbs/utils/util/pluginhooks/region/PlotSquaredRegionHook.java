package wbs.utils.util.pluginhooks.region;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

// TODO: Find a way to support PSv6 without preventing Java 8 support
public class PlotSquaredRegionHook implements WbsRegionHook {
    @Override
    public boolean canPlayerBuild(Location loc, Player player) {
        return true;
        /*

		org.bukkit.World world = loc.getWorld();

		if (world == null)
			throw new IllegalArgumentException("Location had an invalid world.");

		PlotPlayer plotPlayer = PlotPlayer.wrap(player);

		com.github.intellectualsites.plotsquared.plot.object.Location checkLocation =
				new com.github.intellectualsites.plotsquared.plot.object.
				Location(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 0, 0);

		Plot plot = checkLocation.getPlot();

		if (plot == null) {
			return Permissions.hasPermission(plotPlayer,
					Captions.PERMISSION_ADMIN_BUILD_ROAD, false);
		} else {
			if (!plot.getTrusted().contains(plotPlayer.getUUID())) {
				if (!plot.getOwners().contains(plotPlayer.getUUID())) {
					if (!plot.getMembers().contains(plotPlayer.getUUID())) {
						return Permissions.hasPermission(plotPlayer,
								Captions.PERMISSION_ADMIN_BUILD_OTHER, false);
					} else { // They are a member but not trusted or owner, an owner must be online
						boolean isOwnerOnline = false;
						for (UUID ownerUUID : plot.getOwners()) {
							PlotPlayer owner = PlotPlayer.wrap(ownerUUID);

							if (owner.isOnline()) {
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
         */
    }

    @Override
    public boolean canDealDamage(Entity attacker, Entity victim) {
        return true;
    }

    @Override
    public String getRequiredPlugin() {
        return "PlotSquared";
    }
}
