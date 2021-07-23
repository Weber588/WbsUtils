package wbs.utils;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import org.jetbrains.annotations.NotNull;
import wbs.utils.util.plugin.WbsMessenger;
import wbs.utils.util.pluginhooks.PluginHookManager;
import wbs.utils.util.pluginhooks.WbsRegionUtils;

public class UtilsCommand extends WbsMessenger implements TabExecutor {

	private final WbsUtils plugin;
	public UtilsCommand(WbsUtils plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		plugin.configure();
		sendMessage("Reloaded! See console for details.", sender);

		return true;
	}
	

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		return new LinkedList<>();
	}

}
