package wbs.utils.util.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.exceptions.CommandNotImplementedException;
import wbs.utils.util.plugin.WbsMessenger;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.string.WbsStrings;

import java.util.*;

/**
 * A "leaf" of a WbsCommand. WbsSubcommands are called from WbsCommands or
 * WbsCommandNodes to run their command.
 */
@SuppressWarnings("unused")
public abstract class WbsSubcommand extends WbsMessenger {

    @NotNull
    private final String label;
    private final Set<String> aliases = new HashSet<>();
    @NotNull
    private String permission = "";

    /**
     * @param plugin The plugin to use for messaging
     * @param label The label for this subcommand
     */
    public WbsSubcommand(@NotNull WbsPlugin plugin, @NotNull String label) {
        super(plugin);
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
        throw new CommandNotImplementedException();
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
        return onCommand(sender, label, args);
    }

    /**
     * Check this command's permission, and if successful, run the implementing class's onCommand
     * method
     * @param sender The sender that ran the command
     * @param label The alias used to run this command
     * @param args The arguments provided
     * @param start The index that was reached before running this command
     * @return false if the command failed unexpectedly, true otherwise
     */
    protected final boolean onCommandCheckPermission(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, int start) {
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
     * Sets the permission needed to use this subcommand
     * @param permission The permission that will be needed to use this subcommand
     * @return The same WbsSubcommand (for chaining)
     */
    @SuppressWarnings("UnusedReturnValue")
    public @NotNull WbsSubcommand setPermission(@NotNull String permission) {
        this.permission = permission;
        return this;
    }

    /**
     * @return The permission required to use this subcommand
     */
    public @NotNull String getPermission() {
        return permission;
    }

    /**
     * Add an alternative label to use instead of label.
     * The added alias does not appear in tabbing.
     * @param alias A new alias for this subcommand
     * @return The same WbsSubcommand (for chaining)
     */
    public WbsSubcommand addAlias(String alias) {
        aliases.add(alias);
        return this;
    }

    /**
     * Add multiple aliases for this subcommand
     * @param aliases Any number of Strings that this subcommand can be referenced by
     * @return The same WbsSubcommand (for chaining)
     */
    public WbsSubcommand addAliases(String ... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
        return this;
    }

    /**
     * Sets the list of aliases for this subcommand to the provided
     * value, removing all existing ones.
     * @param aliases The new aliases to be used for this subcommand.
     * @return The same WbsSubcommand (for chaining)
     */
    public WbsSubcommand setAliases(Set<String> aliases) {
        this.aliases.clear();
        this.aliases.addAll(aliases);
        return this;
    }

    /**
     * Returns a copy of the collection of aliases for this subcommand
     * @return A copy of the collection of aliases
     */
    public Collection<String> getAliases() {
        return new LinkedList<>(aliases);
    }

    /**
     * Check if the given String is an alias of this subcommand
     * @param check The String to check
     * @return True if the given String can be used to reference this subcommand,
     * via an alias or the label
     */
    public boolean isAliased(String check) {
        if (check.equalsIgnoreCase(label)) return true;

        for (String alias : aliases) {
            if (check.equalsIgnoreCase(alias)) return true;
        }

        return false;
    }

    /**
     * @return The label of this subcommand
     */
    public @NotNull String getLabel() {
        return label;
    }

    protected List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        return getTabCompletions(sender, label, args, 2);
    }

    protected List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, int start) {
        return new LinkedList<>();
    }
}
