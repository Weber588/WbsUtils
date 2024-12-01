package wbs.utils;

import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityStateManager;
import wbs.utils.util.particles.WbsParticleEffect;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.pluginhooks.PluginHookManager;
import wbs.utils.util.pluginhooks.VaultWrapper;

import java.util.Arrays;
import java.util.List;

/**
 * The base plugin, a simple implementation of {@link WbsPlugin}.
 */
public class WbsUtils extends WbsPlugin {
	
	private static WbsUtils instance = null;
	public static WbsUtils getInstance() {
		return instance;
	}

	private boolean isLoaded = false;
	
	public WbsUtils() {
		instance = this;
	}

	@Override
	public void onLoad() {
		EntityStateManager.registerNativeDeserializers();

		isLoaded = true;
	}

	@Override
	public void onEnable() {
		EntityStateManager.registerConfigurableClasses();

		WbsParticleEffect.setPlugin(this);
		UtilsCommand command = new UtilsCommand(this);

		LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
		manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
			final Commands commands = event.registrar();
			commands.register(
					Commands.literal("utilsreload")
							.executes(ctx -> {
								String[] args = ctx.getInput().split(" ");
								command.onCommand(ctx.getSource().getSender(),
										null,
										args[0],
										Arrays.copyOfRange(args, 1, args.length)
								);
								return Command.SINGLE_SUCCESS;
							})
							.build(),
					"Reload dependencies of WbsUtils.",
					List.of("ureload")
			);
		});

		// TODO: Actually add a config omg
		setDisplays("&8[&7WbsUtils&8]", ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED);

		configure();
	}

	public void configure() {
		PluginHookManager.isConfigured = false;
		PluginHookManager.configure();

		VaultWrapper.isConfigured = false;
		VaultWrapper.configure();
	}

    @Override
    public void onDisable() {
    	
    }

	public boolean isLoaded() {
		return isLoaded;
	}
}
