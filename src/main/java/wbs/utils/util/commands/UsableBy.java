package wbs.utils.util.commands;

/**
 * Who can use a given command
 */
public enum UsableBy {
    /**
     * Any {@link org.bukkit.command.CommandSender}.
     */
    ANY,
    /**
     * A {@link org.bukkit.entity.Player}.
     */
    PLAYER,
    /**
     * A {@link org.bukkit.command.ConsoleCommandSender}.
     */
    CONSOLE,
    /**
     * A {@link org.bukkit.command.BlockCommandSender}.
     */
    BLOCK
}
