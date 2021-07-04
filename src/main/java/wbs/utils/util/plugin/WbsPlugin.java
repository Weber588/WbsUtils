package wbs.utils.util.plugin;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import wbs.utils.util.string.WbsStrings;

/**
 * Somewhat a utility class, use this
 * instead of extending JavaPlugin to gain
 * access to a bunch of messaging options
 * that allows a consistent layout throughout
 * the plugin.
 * @author Weber588
 */
public abstract class WbsPlugin extends JavaPlugin {

	public Logger logger = getLogger();


	public String dynamicColourise(String message) {
		message = message.replaceAll("&r", "" + colour); // Replace default with the main colour
		message = message.replaceAll("&h", "" + highlight); // Replace &h with the highlight colour
		message = message.replaceAll("&w", "" + errorColour); // Replace &w with the error colour
		message = message.replaceAll("&x", "" + errorColour); // Replace &x with the error colour
		message = WbsStrings.colourise(message);
		return message;
	}

	/**
	 * Send a formatted message with "&" colour codes,
	 * where "&w" becomes the configured error colour,
	 * "&h" becomes the configured highlight colour, and
	 * "&r" resets to the configured main colour.
	 * @param message The message to send
	 * @param sender The CommandSender to receive the message
	 */
	public void sendMessage(String message, CommandSender sender) {
		message = dynamicColourise(message);
		sender.sendMessage(prefix + ' ' +  colour + message);
	}
	
	/**
	 * Send a formatted message with no prefix and 
	 * formatted "&" colour codes, where
	 * "&w" becomes the configured error colour, and
	 * "&h" becomes the configured highlight colour
	 * @param message The message to send
	 * @param sender The CommandSender to receive the message
	 */
	public void sendMessageNoPrefix(String message, CommandSender sender) {
		message = dynamicColourise(message);
		sender.sendMessage(colour + message);
	}
	
	/**
	 * Send a formatted message to all online players.
	 * @see #sendMessage(String, CommandSender) sendMessage for formatting information
	 * @param message The message to broadcast
	 */
	public void broadcast(String message) {
		message = dynamicColourise(message);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(prefix + ' ' +  colour + message);
		}
	}
	
	/**
	 * Sends a formatted action bar to the specified player
	 * @see #sendMessage(String, CommandSender) sendMessage for formatting information
	 * @param message The action bar message to send
	 * @param player The player to receive the action bar
	 */
	public void sendActionBar(String message, Player player) {
		message = dynamicColourise(message);
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(colour + message).create());
	}
	
	public void broadcastActionBar(String message, double radius, Location loc) {
		World world = loc.getWorld();

		if (world == null)
			throw new IllegalArgumentException("Location had an invalid world.");

		for (Entity entity : world.getNearbyEntities(loc, 10, 10, 10)) {
			if (entity instanceof Player) {
				sendActionBar(message, (Player) entity);
			}
		}
	}
	
	private ChatColor colour = ChatColor.GREEN;
	private ChatColor highlight = ChatColor.BLUE;
	private ChatColor errorColour = ChatColor.RED;
	public String prefix;
	
	/**
	 * Set the displays used throughout the plugin for formatting
	 */
	public void setDisplays(String newPrefix, ChatColor newColour, ChatColor newHighlight, ChatColor newErrorColour) {
		prefix = ChatColor.translateAlternateColorCodes('&', newPrefix);
		colour = newColour;
		highlight = newHighlight;
		errorColour = newErrorColour;
	}

	@Override
	public abstract void onEnable();
	
}
