package wbs.utils.util.pluginhooks;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.function.BiFunction;

/**
 * Simple wrapper for PlaceholderAPI that allows plugins to safely
 * fill out placeholders without checking if that plugin is enabled
 */
@SuppressWarnings("unused")
public final class PlaceholderAPIWrapper {
	private PlaceholderAPIWrapper() {}

	private static final PluginManager manager = Bukkit.getPluginManager();
	public static boolean isActive() {
		Plugin papi = manager.getPlugin("PlaceholderAPI");
		return papi != null && papi.isEnabled();
	}
	
	public static String setPlaceholders(Player player, String text) {
		if (isActive()) {
			return PlaceholderAPI.setPlaceholders(player, text);
		} else {
			return text;
		}
	}

	public static boolean registerSimplePlaceholder(JavaPlugin plugin, String author, BiFunction<OfflinePlayer, String, String> function) {
		if (!isActive()) {
			return false;
		}

		SimplePlaceholder simplePlaceholder = new SimplePlaceholder(plugin, author, function);

		return simplePlaceholder.register();
	}
}
