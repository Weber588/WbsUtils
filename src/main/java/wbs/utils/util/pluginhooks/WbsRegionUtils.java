package wbs.utils.util.pluginhooks;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import wbs.utils.util.pluginhooks.region.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A static class that centralizes calls to {@link WbsRegionHook}s, allowing
 * all installed supported plugins to be queried at once
 */
@SuppressWarnings("unused")
public final class WbsRegionUtils {
	private WbsRegionUtils() {}

	private static final Set<WbsRegionHook> hooks = new HashSet<>();

	static {
		hooks.addAll(Arrays.asList(
				new TownyRegionHook(),
				new WorldGuardRegionHook(),
				new GriefPreventionRegionHook(),
				new PlotSquaredRegionHook()
		));
	}

	/**
	 * Check if the given player can build in the given location
	 * @param loc The location to check
	 * @param player The player to check for
	 * @return True if the player can build at that location
	 */
	public static boolean canBuildAt(Location loc, Player player) {
		boolean canBuild = true;

		for (WbsRegionHook hook : hooks) {
			if (hook.enabled()) {
				canBuild &= hook.canPlayerBuild(loc, player);
			}
		}

		return canBuild;
	}

	/**
	 * Check if the attacking entity is allowed to damage the victim
	 * @param attacker The entity attempting to deal damage
	 * @param victim The entity being damaged
	 * @return True if the attacker can damage the victim
	 */
	public static boolean canDealDamage(Entity attacker, Entity victim) {
		boolean canDamage = true;

		for (WbsRegionHook hook : hooks) {
			if (hook.enabled()) {
				canDamage &= hook.canDealDamage(attacker, victim);
			}
		}

		return canDamage;
	}
}
