package wbs.utils.util.plugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Somewhat a utility class, this class
 * provides a few methods to redirect
 * messaging to the plugin messaging system.
 * Also provides some permission checking
 * methods commonly used in classes that
 * deal with player interaction.
 * @author Weber588
 */
public abstract class WbsMessenger {
	
	protected WbsPlugin plugin;
	public WbsMessenger(WbsPlugin plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * A method to check whether or not a command sender has a given permission.
	 * If the sender does not have the permission, they will get a default
	 * permission error message so it doesn't need to be handled in other methods.
	 * @param sender The command sender to check
	 * @param permission The permission to check
	 * @return True if the sender has permission
	 */
	protected boolean checkPermission(CommandSender sender, String permission) {
		if (permission == null || permission.equals("")) {
			return true;
		}
		if (!sender.hasPermission(permission)) {
			sendMessage("&wYou are lacking the permission node: &h" + permission, sender);
			return false;
		}
		return true;
	}

	/**
	 * Send a message with formatting handled.
	 * @param message The message to send
	 * @param sender The command sender who will receive the message
	 * @see WbsPlugin#sendMessage(String, CommandSender) sendMessage for formatting information
	 */
	protected void sendMessage(String message, CommandSender sender) {
		plugin.sendMessage(message, sender);
	}
	
	/**
	 * Same as {@link #sendMessage(String, CommandSender)} but without the prefix
	 */
	protected void sendMessageNoPrefix(String message, CommandSender sender) {
		plugin.sendMessageNoPrefix(message, sender);
	}
	
	/**
	 * Send a message to all players on the server, formatted automatically
	 * @param message The message to send
	 * @see WbsPlugin#sendMessage(String, CommandSender) sendMessage for formatting information
	 */
	protected void broadcast(String message) {
		plugin.broadcast(message);
	}
	
	/**
	 * Send an action bar to a player with formatting handled
	 * @param message The action bar message to send
	 * @param player The player to show the bar to
	 * @see WbsPlugin#sendMessage(String, CommandSender) sendMessage for formatting information
	 */
	protected void sendActionBar(String message, Player player) {
		plugin.sendActionBar(message, player);
	}
	
	protected void broadcastActionBar(String message, double radius, Location loc) {
		plugin.broadcastActionBar(message, radius, loc);
	}
	protected <T> int sendList(Set<T> set, int entriesPerPage, int page, CommandSender sender) {
		return sendList(Arrays.asList(set.toArray()), entriesPerPage, page, sender);
	}

	protected <T> int sendList(List<T> list, int entriesPerPage, int page, CommandSender sender) {
		int index = 0;
		int success = 0;
		for (T object : list) {
			if (index >= page * entriesPerPage) {
				if (index < (page+1) * entriesPerPage) {
					sendMessageNoPrefix("&h" + (index + 1) + ") &r" + object.toString(), sender);
					success++;
				} else {
					break;
				}
			}
			
			index++;
		}
		
		return success;
	}
	
	
	
	
}
