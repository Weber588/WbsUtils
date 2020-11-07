package wbs.utils.util.pluginhooks;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.intellectualsites.plotsquared.plot.PlotSquared;
import com.github.intellectualsites.plotsquared.plot.config.Captions;
import com.github.intellectualsites.plotsquared.plot.object.Plot;
import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;
import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer.PlotPlayerConverter;
import com.github.intellectualsites.plotsquared.plot.util.Permissions;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import wbs.utils.WbsUtils;

public abstract class WbsRegionUtils {

	private WbsRegionUtils() {}

	public static boolean isConfigured = false;
	
	private static boolean isInstalled(String pluginName) {
		return Bukkit.getPluginManager().getPlugin(pluginName) != null;
	}

	private static boolean griefPreventionInstalled = false;
	
	private static boolean townyInstalled = false;
	private static Towny towny;
	
	private static boolean plotsquaredInstalled = false;
	
	private static boolean worldGuardInstalled = false;
	private static WorldGuardPlugin worldGuard;
	
	public static void configure() {
		if (isConfigured) {
			return;
		}
		WbsUtils.getInstance().logger.info("Configuring plugin hooks...");

		int hooksFound = 0;
		
		griefPreventionInstalled = isInstalled("GriefPrevention");
		if (griefPreventionInstalled) {
			WbsUtils.getInstance().logger.info("Successfully hooked into GriefPrevention!");
			hooksFound++;
		}

		townyInstalled = isInstalled("Towny");
		if (townyInstalled) {
			towny = (Towny) Bukkit.getServer().getPluginManager().getPlugin("Towny");
			WbsUtils.getInstance().logger.info("Successfully hooked into Towny!");
			hooksFound++;
		}
		
		plotsquaredInstalled = isInstalled("PlotSquared");
		if (plotsquaredInstalled) {
			WbsUtils.getInstance().logger.info("Successfully hooked into PlotSquared!");
			hooksFound++;
		}
		
		worldGuardInstalled = isInstalled("WorldGuard");
		if (worldGuardInstalled) {
			WbsUtils.getInstance().logger.info("Successfully hooked into WorldGuard!");
			worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
			hooksFound++;
		}
		
		isConfigured = true;
		WbsUtils.getInstance().logger.info("Successfully hooked into " + hooksFound + " plugins.");
	}
	
	public static boolean canBuildAt(Location loc, Player player) {
		configure();
		
		if (griefPreventionInstalled) {
			PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
			Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, true, playerData.lastClaim);
			if (claim != null) {
				if (claim.allowAccess(player) != null) {
					return false;
				}
			}
		}
		
		if (townyInstalled) {
			boolean bBuild = PlayerCacheUtil.getCachePermission(player, loc, loc.getBlock().getType(), TownyPermission.ActionType.BUILD);
			
			if (!bBuild) {
				return false;
			}
		}
		
		if (plotsquaredInstalled) {
			if (!canPlayerBuildPlot(player, loc)) {
				return false;
			}
		}
		
		if (worldGuardInstalled) { // why so convoluted oml
			LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
			World world = localPlayer.getWorld();
			boolean canBypass = WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, world);
			
			if (!canBypass) {
				com.sk89q.worldedit.util.Location checkLoc = BukkitAdapter.adapt(loc);
				RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
				RegionQuery query = container.createQuery();
				if (!query.testState(checkLoc, localPlayer, Flags.BUILD)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public static boolean canDealDamage(Entity attacker, Entity victim) {

		if (townyInstalled) {
		    if (CombatUtil.preventDamageCall(towny, attacker, victim)) {
		    	return false;
		    }
	    }
	    
		if (griefPreventionInstalled) {
			// TODO
		}
		
		if (plotsquaredInstalled) {
			if (attacker instanceof Player) {
				if (!canPlayerBuildPlot((Player) attacker, victim.getLocation())) {
					return false;
				}
			}
		}
		
		if (worldGuardInstalled) {
			if (attacker instanceof Player) {
				Player playerAttacker = (Player) attacker;
				
				LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(playerAttacker);
				World world = localPlayer.getWorld();
				boolean canBypass = WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, world);
				
				if (!canBypass) {
					com.sk89q.worldedit.util.Location checkLoc = BukkitAdapter.adapt(victim.getLocation());
					RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
					RegionQuery query = container.createQuery();
					if (!query.testState(checkLoc, localPlayer, Flags.MOB_DAMAGE)) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	private static boolean canPlayerBuildPlot(Player player, Location loc) {

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
	}
}
