package wbs.utils.util.pluginhooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.plugin.java.JavaPlugin;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

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

	private static final Map<JavaPlugin, SimplePlaceholder> registered = new HashMap<>();
	
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

		if (registered.containsKey(plugin)) {
			registered.get(plugin).unregister();
		}

		SimplePlaceholder simplePlaceholder = new SimplePlaceholder(plugin, author) {
			@Override
			public String onRequest(OfflinePlayer player, String params) {
				String result = function.apply(player, params);

				if (result == null && plugin instanceof WbsPlugin) {
					return parseWbsPluginPlaceholder((WbsPlugin) plugin, player, params);
				}

				return result;
			}
		};

		registered.put(plugin, simplePlaceholder);

		return simplePlaceholder.register();
	}

	private static String parseWbsPluginPlaceholder(WbsPlugin plugin, OfflinePlayer player, String params) {
		if (params.equalsIgnoreCase("prefix")) {
			return plugin.prefix;
		}

		if (params.equalsIgnoreCase("formatting_colour")) {
			return plugin.getColour().toString();
		}

		if (params.equalsIgnoreCase("formatting_highlight")) {
			return plugin.getHighlight().toString();
		}

		if (params.equalsIgnoreCase("formatting_error")) {
			return plugin.getErrorColour().toString();
		}

		return null;
	}

	private static abstract class SimplePlaceholder extends PlaceholderExpansion {
		private final JavaPlugin plugin;
		private final String author;

		private SimplePlaceholder(JavaPlugin plugin, String author) {
			this.plugin = plugin;
			this.author = author;
		}

		public abstract String onRequest(OfflinePlayer player, String params);

		@Override
		public String getIdentifier() {
			return plugin.getName();
		}

		@Override
		public String getAuthor() {
			return author;
		}

		@Override
		public String getVersion() {
			return "1.0.0";
		}

		@Override
		public String getRequiredPlugin() {
			return plugin.getName();
		}
	}
}
