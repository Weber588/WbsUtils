package wbs.utils.util.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;

import org.jetbrains.annotations.NotNull;

import wbs.utils.WbsUtils;
import wbs.utils.util.plugin.WbsMessenger;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.string.WbsStrings;

import java.util.*;

/**
 * A command executor that is run entirely by the subcommands added to it.
 * Automatically handles the initial command and passes the executor args
 * to the first layer of subcommands.
 * Automatically tabs to the labels (not aliases) of any added subcommands.
 */
public abstract class WbsCommand extends WbsMessenger implements TabExecutor {

    private final PluginCommand command;
    private final Map<String, WbsSubcommand> subcommandMap = new HashMap<>();
    private WbsSubcommand defaultCommand;

    public WbsCommand(WbsPlugin plugin, PluginCommand command) {
        super(plugin);
        this.command = command;

        command.setTabCompleter(this);
        command.setExecutor(this);
    }

    public boolean onCommandNoArgs(@NotNull CommandSender sender, String label) {
        sendMessage("Usage: &h/" + label + " <option>&r. Please choose from the following: &h" + getLabelsString(sender), sender);
        return true;
    }

    public List<String> getSubcommandLabels(CommandSender sender) {
        List<String> labelList = new LinkedList<>();
        for (WbsSubcommand subcommand : subcommandMap.values()) {
            if (sender.hasPermission(subcommand.getPermission())) {
                labelList.add(subcommand.getLabel());
            }
        }
        return labelList;
    }

    public String getLabelsString(CommandSender sender) {
        StringBuilder builder = new StringBuilder();

        int access = 0;
        for (WbsSubcommand subcommand : subcommandMap.values()) {
            if (sender.hasPermission(subcommand.getPermission())) {
                builder.append(subcommand.getLabel()).append(", ");
                access++;
            }
        }

        if (access == 0) return "";

        return builder.substring(0, builder.length() - 2);
    }

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return onCommandNoArgs(sender, label);
        }

        for (WbsSubcommand subcommand : subcommandMap.values()) {
            if (subcommand.getLabel().equalsIgnoreCase(args[0])) {
                return subcommand.onCommandCheckPermission(sender, label, args);
            }
        }

        // Only check aliases if main labels didn't work
        // This avoids an alias overriding another commands only label
        for (WbsSubcommand subcommand : subcommandMap.values()) {
            for (String alias : subcommand.getAliases()) {
                if (alias.equalsIgnoreCase(args[0])) {
                    return subcommand.onCommandCheckPermission(sender, label, args);
                }
            }
        }

        // No subcommand found; use defaultCommand
        if (runDefaultCommand(sender, label, args)) {
            return true;
        }

        sendMessage("Invalid option: &h" + args[0]
                        + "&r. Please choose from the following: &h" + getLabelsString(sender), sender);

        return true;
    }

    protected final boolean runDefaultCommand(CommandSender sender, String label, String[] args) {
        if (defaultCommand != null && checkPermission(sender, defaultCommand.getPermission())) {
            return defaultCommand.onCommandCheckPermission(sender, label, args);
        }
        return false;
    }

    @Override
    public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> choices = new ArrayList<>();

        int length = args.length;

        if (length == 1) {
            subcommandMap.values().forEach(subcommand -> {
                if (sender.hasPermission(subcommand.getPermission()))
                        choices.add(subcommand.getLabel());
            });
        } else {
            for (WbsSubcommand subcommand : subcommandMap.values()) {
                if (subcommand.getLabel().equalsIgnoreCase(args[0]) && sender.hasPermission(subcommand.getPermission())) {
                    List<String> tabs = subcommand.getTabCompletions(sender, label, args);
                    if (tabs != null) choices.addAll(tabs);
                }
            }
        }

        return WbsStrings.filterStartsWith(choices, args[length-1]);
    }

    public WbsCommand setDefaultCommand(WbsSubcommand defaultCommand) {
        this.defaultCommand = defaultCommand;
        return this;
    }

    public WbsCommand addSubcommand(WbsSubcommand subcommand, String permission) {
        if (subcommandMap.containsKey(subcommand.getLabel())) {
            WbsUtils.getInstance().logger.warning("Multiple subcommands attempted to register to " + command.getLabel());
            return this;
        }
        subcommandMap.put(subcommand.getLabel(), subcommand);
        if (permission != null) {
            subcommand.setPermission(permission);
        }
        return this;
    }

    public WbsCommand addSubcommand(WbsSubcommand subcommand) {
        return addSubcommand(subcommand, null);
    }
}
