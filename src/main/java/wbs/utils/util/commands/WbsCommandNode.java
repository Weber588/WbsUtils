package wbs.utils.util.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public abstract class WbsCommandNode extends WbsSubcommand {
    public WbsCommandNode(WbsPlugin plugin, String label) {
        super(plugin, label);
    }

    public Map<String, WbsSubcommand> children = new HashMap<>();

    public void addChild(WbsSubcommand subcommand) {
        children.put(subcommand.getLabel(), subcommand);
    }

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
            // TODO: Make this support multi-arg nodes
            sendUsage("<arg>.&r Please choose from one of the following: &h" + getLabelsString(sender), sender, label, args);
        }

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
