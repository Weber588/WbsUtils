package wbs.utils.util.pluginhooks.region;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface WbsRegionHook {

    /**
     * Check if the given player can build at the given
     * location.
     * @param loc The location to check
     * @param player The player to verify against
     * @return True if the player may build there, false otherwise
     */
    boolean canPlayerBuild(Location loc, Player player);

    /**
     * Can the attacker damage the victim according to
     * the associated plugin?
     * @param attacker The attacking entity
     * @param victim The entity being attacked
     * @return True if the attacker can damage the victim.
     */
    boolean canDealDamage(Entity attacker, Entity victim);

    /**
     * @return The name of the plugin to interact with
     */
    String getRequiredPlugin();
}
