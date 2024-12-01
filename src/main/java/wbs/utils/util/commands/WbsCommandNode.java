package wbs.utils.util.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An implementation of WbsSubcommand that simply passes the command to
 * a given subcommand, including tabbing, or provides default usage messages
 * when no args or incorrect args are given.
 */
@SuppressWarnings("unused")
public abstract class WbsCommandNode extends WbsSubcommand {
    /**
     * @param plugin The WbsPlugin
     * @param label The label for this node
     */
    public WbsCommandNode(WbsPlugin plugin, String label) {
        super(plugin, label);
    }

    private final Map<String, WbsSubcommand> children = new HashMap<>();

    /**
     * Adds a child subcommand
     * @param subcommand The child subcommand to pass to if the label is given
     */
    public void addChild(WbsSubcommand subcommand) {
        children.put(subcommand.getLabel(), subcommand);
    }

    /**
     * Adds a child permission and automatically sets the permission for that
     * subcommand
     * @param subcommand The child subcommand to pass to if the label is given
     * @param permission The permission to apply to the child subcommand
     */
    public void addChild(WbsSubcommand subcommand, String permission) {
        children.put(subcommand.getLabel(), subcommand);
        subcommand.setPermission(permission);
    }

    @Override
    protected boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, int start) {

        if (args.length > start) {
            for (WbsSubcommand subcommand : children.values()) {
                if (subcommand.getLabel().equalsIgnoreCase(args[start - 1 + getArgLength()])) {
                    return subcommand.onCommandCheckPermission(sender, label, args, start + getArgLength());
                }
            }

            // Only check aliases if main labels didn't work
            // This avoids an alias overriding another commands only label
            for (WbsSubcommand subcommand : children.values()) {
                for (String alias : subcommand.getAliases()) {
                    if (alias.equalsIgnoreCase(args[start - 1 + getArgLength()])) {
                        return subcommand.onCommandCheckPermission(sender, label, args, start + getArgLength());
                    }
                }
            }

            sendMessage("Invalid option: &h" + args[start - 1 + getArgLength()]
                    + "&r. Please choose from the following: &h" + getLabelsString(sender), sender);

        } else {
            if (getSubcommandLabels(sender).isEmpty()) {
                sendMessage("You don't have permission to use any subcommands.", sender);
                return true;
            }

            return onCommandNoArgs(sender, label, args, start);
        }

        return true;
    }

    /**
     * Theoretically should never be called, only being implemented here since the start version is implemented, and
     * implementing subclasses shouldn't be able to implement this (as they might expect it to be called).
     */
    @Override
    protected final boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        throw new IllegalStateException("This method should be unreachable. " +
                "If you're seeing this error message, please report this error.");
    }

    protected boolean onCommandNoArgs(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, int start) {
        // TODO: Make this support multi-arg nodes
        sendUsage("<arg>.&r Please choose from one of the following: &h" + getLabelsString(sender), sender, label, args);
        return true;
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, int start) {
        if (args.length == start - 1 + getArgLength()) {
            return children.values().stream().map(WbsSubcommand::getLabel).collect(Collectors.toList());
        } else {
            WbsSubcommand child = children.get(args[start - 2 + getArgLength()]);
            if (child == null) {
                return new LinkedList<>();
            }
            return child.getTabCompletions(sender, label, args, start + getArgLength());
        }
    }

    /**
     * Gets a list of all labels for subcommands usable by the given sender
     * @param sender The sender to filter by
     * @return The list of labels for subcommands usable by the sender
     */
    public List<String> getSubcommandLabels(CommandSender sender) {
        List<String> labelList = new LinkedList<>();
        for (WbsSubcommand subcommand : children.values()) {
            if (sender.hasPermission(subcommand.getPermission())) {
                labelList.add(subcommand.getLabel());
            }
        }
        return labelList;
    }

    private String getLabelsString(CommandSender sender) {
        StringBuilder builder = new StringBuilder();

        int access = 0;
        for (WbsSubcommand subcommand : children.values()) {
            if (sender.hasPermission(subcommand.getPermission())) {
                builder.append(subcommand.getLabel()).append(", ");
                access++;
            }
        }

        if (access == 0) return "";

        return builder.substring(0, builder.length() - 2);
    }

    /**
     * @return The number of arguments this node will process before handing off to the child
     */
    protected int getArgLength() {
        return 1;
    }

}
