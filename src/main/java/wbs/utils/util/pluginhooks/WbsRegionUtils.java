package wbs.utils.util.pluginhooks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import wbs.utils.util.pluginhooks.region.GriefPreventionRegionHook;
import wbs.utils.util.pluginhooks.region.TownyRegionHook;
import wbs.utils.util.pluginhooks.region.WbsRegionHook;
import wbs.utils.util.pluginhooks.region.WorldGuardRegionHook;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public final class WbsRegionUtils {
	private WbsRegionUtils() {}

	private static final Set<WbsRegionHook> hooks = new HashSet<>();

	static {
		hooks.addAll(Arrays.asList(
				new TownyRegionHook(),
				new WorldGuardRegionHook(),
				new GriefPreventionRegionHook()
		));
	}

	public static boolean canBuildAt(Location loc, Player player) {
		boolean canBuild = true;
		PluginManager pm = Bukkit.getPluginManager();

		for (WbsRegionHook hook : hooks) {
			if (pm.isPluginEnabled(hook.getRequiredPlugin())) {
				canBuild &= hook.canPlayerBuild(loc, player);
			}
		}

		return canBuild;
	}

	public static boolean canDealDamage(Entity attacker, Entity victim) {
		boolean canDamage = true;
		PluginManager pm = Bukkit.getPluginManager();

		for (WbsRegionHook hook : hooks) {
			if (pm.isPluginEnabled(hook.getRequiredPlugin())) {
				canDamage &= hook.canDealDamage(attacker, victim);
			}
		}

		return canDamage;
	}
}
