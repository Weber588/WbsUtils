package wbs.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import wbs.utils.util.particles.WbsParticleEffect;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.pluginhooks.PluginHookManager;

public class WbsUtils extends WbsPlugin {
	
	private static WbsUtils instance = null;
	public static WbsUtils getInstance() {
		return instance;
	}
	
	public WbsUtils() {
		instance = this;
	}

	@Override
	public void onEnable() {
		WbsParticleEffect.setPlugin(this);

		PluginCommand reloadCommand = getCommand("utilsreload");
		assert reloadCommand != null;
		reloadCommand.setExecutor(new UtilsCommand(this));

		// TODO: Actually add a config omg
		setDisplays("&8[&7WbsUtils&8]", ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED);

		PluginHookManager.isConfigured = false;
		PluginHookManager.configure();
	}

    @Override
    public void onDisable() {
    	
    }
}
