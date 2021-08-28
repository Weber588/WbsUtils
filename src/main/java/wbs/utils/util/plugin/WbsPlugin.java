package wbs.utils.util.plugin;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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
import wbs.utils.util.pluginhooks.PlaceholderAPIWrapper;
import wbs.utils.util.string.WbsStrings;

/**
 * Somewhat a utility class, use this
 * instead of extending JavaPlugin to gain
 * access to a bunch of messaging options
 * that allows a consistent layout throughout
 * the plugin.
 * @author Weber588
 */
@SuppressWarnings("unused")
public abstract class WbsPlugin extends JavaPlugin {

	public Logger logger = getLogger();

	public List<String> colouriseAll(Collection<String> collection) {
		List<String> colourised = new LinkedList<>();

		collection.forEach(element -> colourised.add(dynamicColourise(element)));

		return colourised;
	}

	/**
	 * Colourize based on the configured plugin colours
	 * @param string The string to colourize
	 * @return The colourized string
	 */
	public String dynamicColourise(String string) {
		string = string.replaceAll("&r", "" + colour); // Replace default with the main colour
		string = string.replaceAll("&h", "" + highlight); // Replace &h with the highlight colour
		string = string.replaceAll("&w", "" + errorColour); // Replace &w with the error colour
		string = string.replaceAll("&x", "" + errorColour); // Replace &x with the error colour
		string = WbsStrings.colourise(string);
		return string;
	}

	/**
	 * Send a formatted message with "&amp;" colour codes,
	 * where "&amp;w" becomes the configured error colour,
	 * "&amp;h" becomes the configured highlight colour, and
	 * "&amp;r" resets to the configured main colour.
	 * @param message The message to send
	 * @param sender The CommandSender to receive the message
	 */
	public void sendMessage(String message, CommandSender sender) {
		message = dynamicColourise(message);
		sender.sendMessage(prefix + ' ' +  colour + message);
	}

	/**
	 * Same as {@link #sendMessage(String, CommandSender)}, but
	 * automatically fills placeholders with PlaceholderAPI if sender
	 * is a Player, and PlaceholderAPI is installed
	 * @param message The message to send after filling any placeholders
	 * @param sender The CommandSender to receive the message
	 */
	public void sendPlaceholderMessage(String message, CommandSender sender) {
		if (sender instanceof Player) {
			message = PlaceholderAPIWrapper.setPlaceholders((Player) sender, message);
		}
		message = dynamicColourise(message);
		sender.sendMessage(prefix + ' ' + colour + message);
	}
	
	/**
	 * Send a formatted message with no prefix and 
	 * formatted "&amp;" colour codes, where
	 * "&amp;w" becomes the configured error colour, and
	 * "&amp;h" becomes the configured highlight colour
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

	/**
	 * Sends an action bar to all players within a radius of a given location
	 * @param message The message to send
	 * @param radius The radius to send the action bar
	 * @param loc The location at which to center the broadcast
	 */
	public void broadcastActionBar(String message, double radius, Location loc) {
		World world = loc.getWorld();

		if (world == null)
			throw new IllegalArgumentException("Location had an invalid world.");

		for (Entity entity : world.getNearbyEntities(loc, radius, radius, radius)) {
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
	 * @param newPrefix The prefix to appear before standard messages
	 * @param newColour The new default plugin colour
	 * @param newHighlight The new highlight colour
	 * @param newErrorColour The new errors colour
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
