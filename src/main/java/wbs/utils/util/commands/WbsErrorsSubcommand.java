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
 * and it may be used to read errors in a paged format
 */
@SuppressWarnings("unused")
public abstract class WbsErrorsSubcommand extends WbsSubcommand {
    /**
     * @param plugin The plugin to use for messaging
     */
    public WbsErrorsSubcommand(WbsPlugin plugin) {
        super(plugin, "errors");
    }

    /**
     * @return The number of errors on each page this implementation shows
     */
    protected int getEntriesPerPage() {
        return 5;
    }

    @Override
    protected boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        WbsSettings settings = getSettings();
        List<String> errors = settings.getErrors();

        if (errors.isEmpty()) {
            sendMessage("&aThere were no errors in the last reload.", sender);
            return true;
        }
        int page = 1;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sendMessage("Usage: &h/" + label + " errors [page]", sender);
                return true;
            }
        }
        page--;

        int entriesPerPage = getEntriesPerPage();

        int pages = errors.size() / entriesPerPage;
        if (errors.size() % entriesPerPage != 0) {
            pages++;
        }
        sendMessage("Displaying page " + (page+1) + "/" + pages + ":", sender);
        int index = 1;
        for (String error : errors) {
            if (index > page * entriesPerPage && index <= (page + 1) * (entriesPerPage)) {
                sendMessage("&6" + index + ") " + error, sender);
            }
            index++;
        }
        return true;
    }

    protected abstract WbsSettings getSettings();
}
