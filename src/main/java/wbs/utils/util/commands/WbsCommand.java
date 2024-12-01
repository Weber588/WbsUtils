package wbs.utils.util.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
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
@SuppressWarnings({"unused", "UnstableApiUsage"})
public abstract class WbsCommand extends WbsMessenger implements TabExecutor {

    private PluginCommand command;
    private String label;
    private final Map<String, WbsSubcommand> subcommandMap = new HashMap<>();
    private WbsSubcommand defaultCommand;

    /**
     * @param plugin The WbsPlugin this command should be registered to
     * @param command The PluginCommand this command represents, as defined in
     *                the plugin.yml of the given WbsPlugin
     */
    public WbsCommand(WbsPlugin plugin, @NotNull PluginCommand command) {
        super(plugin);
        this.command = command;
        this.label = command.getLabel();

        command.setTabCompleter(this);
        command.setExecutor(this);
    }
    /**
     * @param plugin The WbsPlugin this command should be registered to
     * @param label The label, i.e. the typed command name, that this command uses when registered.
     */
    public WbsCommand(WbsPlugin plugin, @NotNull String label) {
        super(plugin);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * A method that gets run when no arguments are provided.
     * Overrideable, but defaults to a dynamic usage message that lists the
     * subcommands usable by the sender.
     * @param sender The sender performing this command
     * @param label The label they used to get this command
     * @return Whether or not the command was successful
     */
    public boolean onCommandNoArgs(@NotNull CommandSender sender, String label) {
        if (getSubcommandLabels(sender).isEmpty()) {
            sendMessage("You don't have permission to use any subcommands.", sender);
            return true;
        }
        sendMessage("Usage: &h/" + label + " <option>&r. Please choose from the following: &h" + getLabelsString(sender), sender);
        return true;
    }

    /**
     * Gets a list of all labels for subcommands usable by the given sender
     * @param sender The sender to filter by
     * @return The list of labels for subcommands usable by the sender
     */
    public List<String> getSubcommandLabels(CommandSender sender) {
        List<String> labelList = new LinkedList<>();
        for (WbsSubcommand subcommand : subcommandMap.values()) {
            if (sender.hasPermission(subcommand.getPermission())) {
                labelList.add(subcommand.getLabel());
            }
        }
        return labelList;
    }

    /**
     * Gets a human readable list of all labels for subcommands usable by the given sender
     * @param sender The command sender to filter by
     * @return A human readable list of subcommand labels
     */
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
    public final boolean onCommand(@NotNull CommandSender sender, @Nullable Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return onCommandNoArgs(sender, label);
        }

        for (WbsSubcommand subcommand : subcommandMap.values()) {
            if (subcommand.getLabel().equalsIgnoreCase(args[0])) {
                return subcommand.onCommandCheckPermission(sender, label, args, 1);
            }
        }

        // Only check aliases if main labels didn't work
        // This avoids an alias overriding another commands only label
        for (WbsSubcommand subcommand : subcommandMap.values()) {
            for (String alias : subcommand.getAliases()) {
                if (alias.equalsIgnoreCase(args[0])) {
                    return subcommand.onCommandCheckPermission(sender, label, args, 1);
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
            return defaultCommand.onCommandCheckPermission(sender, label, args, 1);
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

    /**
     * Set the command to run if an invalid argument is passed in as
     * the first argument
     * @param defaultCommand The default command
     * @return The same WbsCommand (for chaining)
     */
    public WbsCommand setDefaultCommand(WbsSubcommand defaultCommand) {
        this.defaultCommand = defaultCommand;
        return this;
    }

    /**
     * Add a subcommand under a given permission
     * @param subcommand The subcommand to add
     * @param permission The permission to automatically set for the subcommand
     * @return The same WbsCommand (for chaining)
     */
    public WbsCommand addSubcommand(WbsSubcommand subcommand, String permission) {
        if (subcommandMap.containsKey(subcommand.getLabel())) {
            WbsUtils.getInstance().getLogger().warning("Multiple subcommands attempted to register to " + getLabel());
            return this;
        }
        subcommandMap.put(subcommand.getLabel(), subcommand);
        if (permission != null) {
            subcommand.setPermission(permission);
        }
        return this;
    }

    /**
     * Add a subcommand without specifying a permission
     * @param subcommand The subcommand to add
     * @return The same WbsCommand (for chaining)
     */
    public WbsCommand addSubcommand(WbsSubcommand subcommand) {
        return addSubcommand(subcommand, null);
    }
}
