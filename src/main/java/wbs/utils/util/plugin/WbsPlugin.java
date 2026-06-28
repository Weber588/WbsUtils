package wbs.utils.util.plugin;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.google.common.base.Strings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.WbsEventUtils;
import wbs.utils.util.WbsFileUtil;
import wbs.utils.util.commands.brigadier.WbsErrorsSubcommand;
import wbs.utils.util.commands.brigadier.WbsReloadSubcommand;
import wbs.utils.util.plugin.bootstrap.WbsBootstrapSettings;
import wbs.utils.util.pluginhooks.PlaceholderAPIWrapper;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Somewhat a utility class, use this
 * instead of extending JavaPlugin to gain
 * access to a bunch of messaging options
 * that allows a consistent layout throughout
 * the plugin.
 * @author Weber588
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class WbsPlugin extends JavaPlugin {

	@Deprecated(forRemoval = true)
	public Logger logger = getLogger();
	@Deprecated(forRemoval = true)
	public PluginManager pluginManager = Bukkit.getPluginManager();

	protected void registerListener(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, this);
	}

	private Style defaultStyle = Style.style(NamedTextColor.GREEN);
	private Style highlightStyle = Style.style(NamedTextColor.BLUE);
	private Style errorStyle = Style.style(NamedTextColor.RED);
	private Component prefix;

	@Override
	public abstract void onEnable();

	/**
	 * Set the displays used throughout the plugin for formatting
	 * @param newPrefix The prefix to appear before standard messages
	 * @param defaultStyle The new default plugin colour
	 * @param newHighlight The new highlight colour
	 * @param newError The new errors colour
	 */
	public void setDisplays(Component newPrefix, Style defaultStyle, Style newHighlight, Style newError) {
		prefix = newPrefix;
		this.defaultStyle = defaultStyle;
		highlightStyle = newHighlight;
		errorStyle = newError;
	}
	public void setDisplays(String newPrefix, Style defaultStyle, Style newHighlight, Style newError) {
		setDisplays(dynamicColourise(newPrefix), defaultStyle, newHighlight, newError);
	}
	public void setDisplays(String newPrefix, TextColor defaultStyle, TextColor newHighlight, TextColor newError) {
		setDisplays(newPrefix, Style.style(defaultStyle), Style.style(newHighlight), Style.style(newError));
	}

	public Component getPrefix() {
		return prefix;
	}

	public Style getDefaultStyle() {
		return defaultStyle;
	}

	public Style getHighlightStyle() {
		return highlightStyle;
	}

	public Style getErrorStyle() {
		return errorStyle;
	}

	public String toMiniMessageFormat(String string) {
		MiniMessage miniMessage = MiniMessage.miniMessage();

		return miniMessage.serialize(deserializeKnownFormats(string));
	}

	public Component deserializeKnownFormats(String string) {
		MiniMessage miniMessage = MiniMessage.miniMessage();
		LegacyComponentSerializer legacy = LegacyComponentSerializer.legacySection()
				.toBuilder()
				.hexColors()
				.build();
		LegacyComponentSerializer legacyAmpersand = LegacyComponentSerializer.legacyAmpersand();

		Component asComponent = legacy.deserialize(string);
		asComponent = legacyAmpersand.deserialize(legacyAmpersand.serialize(asComponent));
		asComponent = miniMessage.deserialize(miniMessage.serialize(asComponent));

		return asComponent;
	}

	public Component dynamicColourise(String string) {
		string = applyLegacyColor(string, "&r", defaultStyle);
		string = applyLegacyColor(string, "&h", highlightStyle);
		string = applyLegacyColor(string, "&w", errorStyle);
		string = applyLegacyColor(string, "&x", errorStyle);

		return deserializeKnownFormats(string);
	}
	public Component getFormattedMessage(String string) {
		return Component.empty().append(dynamicColourise(string).applyFallbackStyle(defaultStyle));
	}
	public Component getPrefixedMessage(String string) {
		return Component.empty().append(prefix).append(Component.text(" ")).append(getFormattedMessage(string));
	}

	private String applyLegacyColor(String string, String match, Style style) {
		TextColor defaultColor = style.color();
		if (defaultColor != null) {
			string = string.replaceAll(match, "&" + defaultColor.asHexString()); // Replace default with the main colour
		}
		return string;
	}

	public List<Component> colouriseAll(Collection<String> collection) {
		List<Component> colourised = new LinkedList<>();

		collection.forEach(element -> colourised.add(getFormattedMessage(element)));

		return colourised;
	}

	/**
	 * Send a formatted message with "&amp;" colour codes,
	 * where "&amp;w" becomes the configured error colour,
	 * "&amp;h" becomes the configured highlight colour, and
	 * "&amp;r" resets to the configured main colour.
	 * @param message The message to send
	 * @param sender The CommandSender to receive the message
	 */
	public void sendMessage(String message, CommandSender sender) {
		sender.sendMessage(getPrefixedMessage(message));
	}

	/**
	 * Get a {@link WbsMessageBuilder} that allows a message to be built using plugin-specific
	 * formatting, and supports chat events.
	 * @param message The base message to appear after the prefix.
	 * @return The message builder.
	 */
	public WbsMessageBuilder buildMessage(String message) {
		return new WbsMessageBuilder(this, prefix).append(dynamicColourise(message).applyFallbackStyle(defaultStyle));
	}

	/**
	 * Get a {@link WbsMessageBuilder} that allows a message to be built using plugin-specific
	 * formatting, and supports chat events.
	 * @param message The base message to appear after the prefix.
	 * @return The message builder.
	 */
	public WbsMessageBuilder buildMessage(Component message) {
		return new WbsMessageBuilder(this, prefix).append(message.applyFallbackStyle(defaultStyle));
	}

	/**
	 * Get a {@link WbsMessageBuilder} that allows a message to be built using plugin-specific
	 * formatting, and supports chat events.
	 * @param message The message to appear with the plugin's default colour.
	 * @return The message builder.
	 */
	public WbsMessageBuilder buildMessageNoPrefix(String message) {
		return new WbsMessageBuilder(this, dynamicColourise(message).applyFallbackStyle(defaultStyle));
	}

	/**
	 * Get a {@link WbsMessageBuilder} that allows a message to be built using plugin-specific
	 * formatting, and supports chat events.
	 * @param message The message to appear with the plugin's default colour.
	 * @return The message builder.
	 */
	public WbsMessageBuilder buildMessageNoPrefix(Component message) {
		return new WbsMessageBuilder(this,"").append(message.applyFallbackStyle(defaultStyle));
	}

	/**
	 * Same as {@link #sendMessage(String, CommandSender)}, but
	 * automatically fills placeholders with PlaceholderAPI if sender
	 * is a Player, and PlaceholderAPI is installed
	 * @param message The message to send after filling any placeholders
	 * @param sender The CommandSender to receive the message
	 */
	public void sendPlaceholderMessage(String message, CommandSender sender) {
		if (sender instanceof Player) {
			message = PlaceholderAPIWrapper.setPlaceholders((Player) sender, message);
		}
		sendMessage(message, sender);
	}
	
	/**
	 * Send a formatted message with no prefix and 
	 * formatted "&amp;" colour codes, where
	 * "&amp;w" becomes the configured error colour, and
	 * "&amp;h" becomes the configured highlight colour
	 * @param message The message to send
	 * @param sender The CommandSender to receive the message
	 */
	public void sendMessageNoPrefix(String message, CommandSender sender) {
		sender.sendMessage(dynamicColourise(message).applyFallbackStyle(defaultStyle));
	}
	
	/**
	 * Send a formatted message to all online players.
	 * @see #sendMessage(String, CommandSender) sendMessage for formatting information
	 * @param message The message to broadcast
	 */
	public void broadcast(String message) {
		Component component = getPrefixedMessage(message);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(component);
		}
	}
	
	/**
	 * Sends a formatted action bar to the specified player
	 * @see #sendMessage(String, CommandSender) sendMessage for formatting information
	 * @param message The action bar message to send
	 * @param player The player to receive the action bar
	 */
	public void sendActionBar(String message, Player player) {
		player.sendActionBar(getFormattedMessage(message));
	}

	/**
	 * Sends an action bar to all players within a radius of a given location
	 * @param message The message to send
	 * @param radius The radius to send the action bar
	 * @param loc The location at which to center the broadcast
	 */
	public void broadcastActionBar(String message, double radius, Location loc) {
		World world = loc.getWorld();

		if (world == null)
			throw new IllegalArgumentException("Location had an invalid world.");

		for (Entity entity : world.getNearbyEntities(loc, radius, radius, radius)) {
			if (entity instanceof Player) {
				sendActionBar(message, (Player) entity);
			}
		}
	}

	protected WbsReloadSubcommand getReloadCommand() {
		return WbsReloadSubcommand.getStatic(this, getSettings());
	}
	protected WbsErrorsSubcommand getErrorsCommand() {
		return WbsErrorsSubcommand.getStatic(this, getSettings());
	}

	public WbsSettings getSettings() {
		return null;
	}

	public WbsBootstrapSettings<?> getBoostrapSettings() {
		return null;
	}

	/**
	 * Runs a block of code asynchronously using a BukkitRunnable, and
	 * returns the task Id. Once the task finishes, the callback runnable
	 * is run in the main thread.
	 * @param runnable The runnable to execute asynchronously
	 * @param callback The runnable to execute synchronously on the main thread
	 *                 after the async runnable executes
	 * @return The Id of the task created
	 */
	public int runAsync(@NotNull Runnable runnable, @NotNull Runnable callback) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				runnable.run();
				runSync(callback);
			}
		}.runTaskAsynchronously(this).getTaskId();
	}

	/**
	 * Runs a block of code asynchronously using a BukkitRunnable, and
	 * returns the task Id.
	 * @param runnable The runnable to execute asynchronously
	 * @return The Id of the task created
	 */
	public int runAsync(@NotNull Runnable runnable) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				runnable.run();
			}
		}.runTaskAsynchronously(this).getTaskId();
	}

	/**
	 * Run a block of code in the main thread on the next tick,
	 * where it's safe to do minecraft related operations.
	 * @param runnable The block of code to run
	 * @return The Id of the task created
	 */
	public int runSync(@NotNull Runnable runnable) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				runnable.run();
			}
		}.runTask(this).getTaskId();
	}

	/**
	 * Run a block of code in the main thread at the end of the current tick.
	 * @param runnable The block of code to run
	 */
	public void runAtEndOfTick(@NotNull Runnable runnable) {
		getFromEvent(ServerTickEndEvent.class, event -> true, event -> runnable.run());
	}

	public <T> int getAsync(@NotNull Supplier<T> getter, @NotNull Consumer<T> consumer) {
		return runAsync(() -> {
			T obj = getter.get();
			runSync(() -> consumer.accept(obj));
		});
	}

	public <E extends Event, T> WbsEventUtils.EventHandlerMethod<E> getFromEvent(Class<E> eventClass, Predicate<E> eventFilter, @NotNull Consumer<E> onEvent) {
		return getFromEvent(eventClass, eventFilter, onEvent, 1);
	}

	public <E extends Event, T> WbsEventUtils.EventHandlerMethod<E> getFromEvent(Class<E> eventClass, Predicate<E> eventFilter, @NotNull Consumer<E> onEvent, int maxUses) {
		return getFromEvent(eventClass, eventFilter, onEvent,  maxUses, EventPriority.NORMAL);
	}

	public <E extends Event, T> WbsEventUtils.EventHandlerMethod<E> getFromEvent(Class<E> eventClass, Predicate<E> eventFilter, @NotNull Consumer<E> onEvent, EventPriority priority) {
		return getFromEvent(eventClass, eventFilter, onEvent, 1, priority);
	}

	/**
	 * Registers a temporary event under this plugin, and accepts {@param maxUses} events that match {@param eventFilter}
	 * before unregistering the listener.
	 * @param eventClass The event to be listened to
	 * @param eventFilter A filter for events that should be considered a single use -- this filter must be met {@param maxUses} times
	 *                    for the event to unregister
	 * @param onEvent The handler for what to do when the event matches {@param eventFilter}.
	 * @param maxUses How many times the event should be accepted (matching @{param eventFilter}) before unregistering.
	 * @param <E> The event type to be listened to. Must be an event class that can be registered to, by having a getHandlerList method.
	 * @return The listener used for registration, so {@link HandlerList#unregister(Listener)} can be used to cancel early.
	 */
	public <E extends Event> WbsEventUtils.EventHandlerMethod<E> getFromEvent(Class<E> eventClass, Predicate<E> eventFilter, @NotNull Consumer<E> onEvent, int maxUses, EventPriority priority) {
		HandlerList handlerList;

		try {
			Method getHandlerList = eventClass.getMethod("getHandlerList");
            handlerList = (HandlerList) getHandlerList.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        WbsEventUtils.EventHandlerMethod<E> listener = new WbsEventUtils.EventHandlerMethod<>() {
			int uses = maxUses;
            @Override
            public void handle(E event) {
				if (eventFilter.test(event)) {
					onEvent.accept(event);
					uses--;
					if (uses <= 0) {
						handlerList.unregister(this);
					}
				}
            }
        };

		WbsEventUtils.register(this, eventClass, listener, priority);

		return listener;
	}

	public int runLater(@NotNull Runnable runnable, long ticksLater) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				runnable.run();
			}
		}.runTaskLater(this, ticksLater).getTaskId();
	}

	public int runLaterAsync(@NotNull Runnable runnable, long ticksLater) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				runnable.run();
			}
		}.runTaskLaterAsynchronously(this, ticksLater).getTaskId();
	}

	public int runTimer(@NotNull Consumer<BukkitRunnable> consumer, long delay, long interval) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				consumer.accept(this);
			}
		}.runTaskTimer(this, delay, interval).getTaskId();
	}

	public int runTimerNTimes(@NotNull Consumer<BukkitRunnable> consumer, int times, long delay, long interval) {
		return new BukkitRunnable() {
			int age = 0;

			@Override
			public void run() {
				if (age >= times) {
					cancel();
					return;
				}

				consumer.accept(this);

				age++;
			}
		}.runTaskTimer(this, delay, interval).getTaskId();
	}

	public int runLaterAsync(Consumer<BukkitRunnable> consumer, long delay, long interval) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				consumer.accept(this);
			}
		}.runTaskTimerAsynchronously(this, delay, interval).getTaskId();
	}

	// Overriding this to prevent the pointless warnings when replace is false and the file already exists.
	@Override
	public void saveResource(String resourcePath, boolean replace) {
        if (Strings.isNullOrEmpty(resourcePath)) {
			throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

		if (!replace) {
			resourcePath = resourcePath.replace('\\', '/');

			File outFile = new File(this.getDataFolder(), resourcePath);

			if (outFile.exists()) {
				return;
			}
		}

        super.saveResource(resourcePath, replace);
    }

	public void saveResourceFolder(String folderName, boolean replace) {
		WbsFileUtil.saveResourceFolder(this, folderName, replace);
    }
}
