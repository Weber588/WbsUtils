package wbs.utils.util.plugin;

import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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
import wbs.utils.util.pluginhooks.PlaceholderAPIWrapper;
import wbs.utils.util.string.WbsStrings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
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

	private ChatColor colour = ChatColor.GREEN;
	private ChatColor highlight = ChatColor.BLUE;
	private ChatColor errorColour = ChatColor.RED;
	public String prefix;

	@Override
	public abstract void onEnable();

	/**
	 * Set the displays used throughout the plugin for formatting
	 * @param newPrefix The prefix to appear before standard messages
	 * @param newColour The new default plugin colour
	 * @param newHighlight The new highlight colour
	 * @param newErrorColour The new errors colour
	 */
	public void setDisplays(String newPrefix, ChatColor newColour, ChatColor newHighlight, ChatColor newErrorColour) {
		prefix = ChatColor.translateAlternateColorCodes('&', newPrefix);
		colour = newColour;
		highlight = newHighlight;
		errorColour = newErrorColour;
	}

	public ChatColor getColour() {
		return colour;
	}
	public TextColor getTextColour() {
		return TextColor.color(colour.asBungee().getColor().getRGB());
	}

	public ChatColor getHighlight() {
		return highlight;
	}
	public TextColor getTextHighlightColour() {
		return TextColor.color(highlight.asBungee().getColor().getRGB());
	}

	public ChatColor getErrorColour() {
		return errorColour;
	}
	public TextColor getTextErrorColour() {
		return TextColor.color(errorColour.asBungee().getColor().getRGB());
	}

	/**
	 * Colourize based on the configured plugin colours
	 * @param string The string to colourize
	 * @return The colourized string
	 */
	public String dynamicColourise(String string) {
		string = string.replaceAll("&r", "" + colour); // Replace default with the main colour
		string = string.replaceAll("&h", "" + highlight); // Replace &h with the highlight colour
		string = string.replaceAll("&w", "" + errorColour); // Replace &w with the error colour
		string = string.replaceAll("&x", "" + errorColour); // Replace &x with the error colour
		string = WbsStrings.colourise(string);
		return string;
	}

	public List<String> colouriseAll(Collection<String> collection) {
		List<String> colourised = new LinkedList<>();

		collection.forEach(element -> colourised.add(dynamicColourise(element)));

		return colourised;
	}

	@NotNull
	public TextComponent formatAsTextComponent(@NotNull String message) {
		message = dynamicColourise(colour + message);
		BaseComponent[] components = TextComponent.fromLegacyText(message);

		return new TextComponent(components);
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
		message = dynamicColourise(message);
		sender.sendMessage(prefix + ' ' +  colour + message);
	}

	/**
	 * Get a {@link MessageBuilder} that allows a message to be built using plugin-specific
	 * formatting, and supports chat events.
	 * @param message The base message to appear after the prefix.
	 * @param sender The CommandSender to receive the message
	 * @deprecated Use {@link #buildMessage(String)} and pass sender in when
	 * sending
	 * @return The message builder.
	 */
	@Deprecated
	public MessageBuilder buildMessage(String message, CommandSender sender) {
		return new MessageBuilder(prefix, sender).append(" " + colour + message);
	}

	/**
	 * Get a {@link MessageBuilder} that allows a message to be built using plugin-specific
	 * formatting, and supports chat events.
	 * @param message The base message to appear after the prefix.
	 * @return The message builder.
	 */
	public WbsMessageBuilder buildMessage(String message) {
		return new WbsMessageBuilder(this, prefix).append(" " + colour + message);
	}

	/**
	 * Get a {@link MessageBuilder} that allows a message to be built using plugin-specific
	 * formatting, and supports chat events.
	 * @param message The message to appear with the plugin's default colour.
	 * @param sender The CommandSender to receive the message
	 * @deprecated Use {@link #buildMessageNoPrefix(String)} and pass sender in when
	 * sending
	 * @return The message builder.
	 */
	@Deprecated
	public MessageBuilder buildMessageNoPrefix(String message, CommandSender sender) {
		return new MessageBuilder(colour + message, sender);
	}

	/**
	 * Get a {@link MessageBuilder} that allows a message to be built using plugin-specific
	 * formatting, and supports chat events.
	 * @param message The message to appear with the plugin's default colour.
	 * @return The message builder.
	 */
	public WbsMessageBuilder buildMessageNoPrefix(String message) {
		return new WbsMessageBuilder(this,colour + message);
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
		message = dynamicColourise(message);
		sender.sendMessage(colour + message);
	}
	
	/**
	 * Send a formatted message to all online players.
	 * @see #sendMessage(String, CommandSender) sendMessage for formatting information
	 * @param message The message to broadcast
	 */
	public void broadcast(String message) {
		message = dynamicColourise(message);
		for (Player player : Bukkit.getOnlinePlayers()) {
			sendMessage(message, player);
		}
	}
	
	/**
	 * Sends a formatted action bar to the specified player
	 * @see #sendMessage(String, CommandSender) sendMessage for formatting information
	 * @param message The action bar message to send
	 * @param player The player to receive the action bar
	 */
	public void sendActionBar(String message, Player player) {
		message = dynamicColourise(message);
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(colour + message).create());
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

	/**
	 * @deprecated Use {@link WbsMessageBuilder} and {@link WbsMessage} instead.
	 */
	@Deprecated
	public class MessageBuilder {
		private final List<TextComponent> components = new LinkedList<>();
		@SuppressWarnings("NotNullFieldNotInitialized") // initialized via the append method
		@NotNull
		private TextComponent mostRecent;
		private final List<CommandSender> recipients = new LinkedList<>();
		public MessageBuilder(String message) {
			append(message);
		}
		public MessageBuilder(String message, CommandSender sender) {
			this(message);
			recipients.add(sender);
		}

		public MessageBuilder addRecipient(CommandSender ... senders) {
			recipients.addAll(Arrays.asList(senders));
			return this;
		}

		public MessageBuilder append(String string) {
			return append(formatAsTextComponent(string));
		}
		public MessageBuilder appendRaw(String string) {
			return append(new TextComponent(string));
		}
		public MessageBuilder append(TextComponent text) {
			mostRecent = text;
			components.add(text);
			return this;
		}

		public MessageBuilder prepend(String string) {
			return prepend(formatAsTextComponent(string));
		}
		public MessageBuilder prependRaw(String string) {
			return prepend(new TextComponent(string));
		}
		public MessageBuilder prepend(TextComponent text) {
			mostRecent = text;
			components.add(0, text);
			return this;
		}

		public MessageBuilder onHover(HoverEvent onHover) {
			mostRecent.setHoverEvent(onHover);
			return this;
		}
		public MessageBuilder addHoverText(String string) {
			Text text = new Text(new TextComponent[] { formatAsTextComponent(string) });
			return addHoverText(text);
		}
		public MessageBuilder addHoverTextRaw(String string) {
			Text text = new Text(string);
			return addHoverText(text);
		}
		public MessageBuilder addHoverText(Text text) {
			HoverEvent onHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, text);
			return onHover(onHover);
		}

		public MessageBuilder onClick(ClickEvent onClick) {
			mostRecent.setClickEvent(onClick);
			return this;
		}
		public MessageBuilder addClickCommand(String command) {
			ClickEvent onClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
			return onClick(onClick);
		}
		public MessageBuilder addClickCommandSuggestion(String command) {
			ClickEvent onClick = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
			return onClick(onClick);
		}

		public TextComponent[] getComponentArray() {
			return components.toArray(new TextComponent[0]);
		}

		@SafeVarargs
		public final <T extends CommandSender> void send(T... receivers) {
			List<CommandSender> receiverList = new LinkedList<>(Arrays.asList(receivers));
			send(receiverList);
		}

		public void send(Collection<? extends CommandSender> receivers) {
			TextComponent[] componentArray = getComponentArray();
			for (CommandSender sender : receivers) {
				sender.spigot().sendMessage(componentArray);
			}
		}

		public void send() {
			send(recipients);
		}

		public void broadcast() {
			TextComponent[] componentArray = getComponentArray();
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.spigot().sendMessage(componentArray);
			}
		}
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

	public <T> int getAsync(@NotNull Supplier<T> getter, @NotNull Consumer<T> consumer) {
		return runAsync(() -> {
			T obj = getter.get();
			runSync(() -> consumer.accept(obj));
		});
	}

	public <E extends Event, T> WbsEventUtils.EventHandlerMethod<E> getFromEvent(Class<E> eventClass, Predicate<E> eventFilter, @NotNull Consumer<E> onEvent) {
		return getFromEvent(eventClass, eventFilter, onEvent, 1);
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
	public <E extends Event> WbsEventUtils.EventHandlerMethod<E> getFromEvent(Class<E> eventClass, Predicate<E> eventFilter, @NotNull Consumer<E> onEvent, int maxUses) {
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

		WbsEventUtils.register(this, eventClass, listener);

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

	public void saveResourceFolder(String folderName, boolean replace) {
		WbsFileUtil.saveResourceFolder(this, folderName, replace);
    }
}
