package wbs.utils.util.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.exceptions.CommandNotImplementedException;
import wbs.utils.util.plugin.WbsMessenger;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.string.WbsStrings;

import java.util.*;

public abstract class WbsSubcommand extends WbsMessenger {
    @Nullable
    private final WbsSubcommand parent;

    @NotNull
    private final String label;
    private final Set<String> aliases = new HashSet<>();
    @NotNull
    private String permission = "";

    public WbsSubcommand(WbsPlugin plugin, String label) {
        this(plugin, null, label);
    }

    public WbsSubcommand(WbsPlugin plugin, @Nullable WbsSubcommand parent, @NotNull String label) {
        super(plugin);
        this.parent = parent;
        this.label = label;
    }

    /**
     * Run this subcommand for the given sender, starting at arg[start].
     * When this is called, the permission has already been checked.
     * @param sender The sender that ran the command
     * @param label The alias used to run this command
     * @param args The arguments provided
     * @return false if the command failed unexpectedly, true otherwise
     */
    protected boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        return onCommand(sender, label, args, 1);
    }

    /**
     * Run this subcommand for the given sender, starting at arg[start].
     * When this is called, the permission has already been checked.
     * @param sender The sender that ran the command
     * @param label The alias used to run this command
     * @param args The arguments provided
     * @return false if the command failed unexpectedly, true otherwise
     */
    protected boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, int start) {
        throw new CommandNotImplementedException();
    }

    /**
     * Check this command's permission, and if successful, run the implementing class's onCommand
     * method
     * @param sender The sender that ran the command
     * @param label The alias used to run this command
     * @param args The arguments provided
     * @return false if the command failed unexpectedly, true otherwise
     */
    public final boolean onCommandCheckPermission(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (checkPermission(sender, permission)) {
            return onCommand(sender, label, args);
        }
        return true;
    }
    /**
     * Check this command's permission, and if successful, run the implementing class's onCommand
     * method
     * @param sender The sender that ran the command
     * @param label The alias used to run this command
     * @param args The arguments provided
     * @return false if the command failed unexpectedly, true otherwise
     */
    public final boolean onCommandCheckPermission(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, int start) {
        if (checkPermission(sender, permission)) {
            return onCommand(sender, label, args, start);
        }
        return true;
    }

    protected final void sendUsage(String usage, CommandSender sender, String label, String[] args) {
        sendUsage(usage, sender, label, args, args.length);
    }

    protected final void sendUsage(String usage, CommandSender sender, String label, String[] args, int count) {
        sendMessage("Usage: &h/" + label + " " + WbsStrings.combineFirst(args, count, " ") + " " + usage, sender);
    }

    /**
     * Get the parent subcommand of this one, or null if this is the root.
     * @return The parent, or null if this is the root.
     */
    public @Nullable WbsSubcommand getParent() {
        return parent;
    }

    public @NotNull WbsSubcommand setPermission(@NotNull String permission) {
        this.permission = permission;
        return this;
    }
    public @NotNull String getPermission() {
        return permission;
    }

    public WbsSubcommand addAlias(String alias) {
        aliases.add(alias);
        return this;
    }
    public WbsSubcommand addAliases(String ... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
        return this;
    }
    public WbsSubcommand setAliases(Set<String> aliases) {
        this.aliases.clear();
        this.aliases.addAll(aliases);
        return this;
    }
    public Collection<String> getAliases() {
        return new LinkedList<>(aliases);
    }

    public boolean isAliased(String check) {
        if (check.equalsIgnoreCase(label)) return true;

        for (String alias : aliases) {
            if (check.equalsIgnoreCase(alias)) return true;
        }

        return false;
    }



    public @NotNull String getLabel() {
        return label;
    }

    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        return getTabCompletions(sender, label, args, 2);
    }

    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, int start) {
        return new LinkedList<>();
    }
}
