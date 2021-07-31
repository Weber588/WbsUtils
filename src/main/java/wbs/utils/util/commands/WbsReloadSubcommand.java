package wbs.utils.util.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.plugin.WbsSettings;

import java.util.List;

/**
 * A specific implementation of WbsSubcommand that's commonly used
 * in plugins that use WbsSettings to track configs.
 * Implementing classes simply return their implementation of WbsSettings
 * and it's reloaded, sending a prompt to the using player.
 */
@SuppressWarnings("unused")
public abstract class WbsReloadSubcommand extends WbsSubcommand {
    public WbsReloadSubcommand(WbsPlugin plugin) {
        super(plugin, "reload");
    }

    @Override
    protected boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        WbsSettings settings = getSettings();

        settings.reload();

        List<String> errors = settings.getErrors();
        if (errors.isEmpty()) {
            sendMessage("&aReload successful!", sender);
            return true;
        } else {
            sendMessage("&wThere were " + errors.size() + " config errors. Do &h/magic errors&w to see them.", sender);
        }

        return true;
    }

    /**
     * @return The WbsSettings for the specific plugin that implements this.
     */
    protected abstract WbsSettings getSettings();
}

