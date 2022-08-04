package wbs.utils.util.pluginhooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public final class SimplePlaceholder extends PlaceholderExpansion {
    private final JavaPlugin plugin;
    private final String author;
    private final BiFunction<OfflinePlayer, String, String> function;

    private static final Map<JavaPlugin, SimplePlaceholder> registered = new HashMap<>();

    SimplePlaceholder(JavaPlugin plugin, String author, BiFunction<OfflinePlayer, String, String> function) {
        this.plugin = plugin;
        this.author = author;
        this.function = function;
    }

    public String onRequest(OfflinePlayer player, @NotNull String params) {
        String result = function.apply(player, params);

        if (result == null && plugin instanceof WbsPlugin) {
            return parseWbsPluginPlaceholder((WbsPlugin) plugin, params);
        }

        return result;
    }

    @Override
    public @NotNull String getIdentifier() {
        return plugin.getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return author;
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getRequiredPlugin() {
        return plugin.getName();
    }

    @Override
    public boolean register() {
        boolean success = super.register();

        if (success) {
            if (registered.containsKey(plugin)) {
                registered.get(plugin).unregister();
            }

            registered.put(plugin, this);
        }

        return success;
    }

    private String parseWbsPluginPlaceholder(WbsPlugin plugin, String params) {
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
}
