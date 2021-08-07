package wbs.utils.util.pluginhooks.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Represents a set of commonly used checks for other plugins.
 * Implementing classes represent a single plugin, and implement methods for
 * performing these standard checks on that plugin.
 */
public abstract class WbsRegionHook {

    /**
     * Check if the given player can build at the given
     * location.
     * @param loc The location to check
     * @param player The player to verify against
     * @return True if the player may build there, false otherwise
     */
    public abstract boolean canPlayerBuild(Location loc, Player player);

    /**
     * Can the attacker damage the victim according to
     * the associated plugin?
     * @param attacker The attacking entity
     * @param victim The entity being attacked
     * @return True if the attacker can damage the victim.
     */
    public abstract boolean canDealDamage(Entity attacker, Entity victim);

    /**
     * @return The name of the plugin to interact with
     */
    public abstract String getRequiredPlugin();

    /**
     * @return True if the correct plugin is enabled
     */
    public boolean enabled() {
        return Bukkit.getPluginManager().isPluginEnabled(getRequiredPlugin());
    }
}
