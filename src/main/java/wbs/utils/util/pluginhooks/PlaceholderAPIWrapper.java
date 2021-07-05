package wbs.utils.util.pluginhooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import me.clip.placeholderapi.PlaceholderAPI;

@SuppressWarnings("unused")
public final class PlaceholderAPIWrapper {
	private PlaceholderAPIWrapper() {}

	private static PluginManager manager = Bukkit.getPluginManager();
	private static boolean isActive() {
		return manager.getPlugin("PlaceholderAPI") != null;
	}
	
	public static String setPlaceholders(Player player, String text) {
		if (isActive()) {
			return PlaceholderAPI.setPlaceholders(player, text);
		} else {
			return text;
		}
	}
}
