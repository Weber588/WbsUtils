package wbs.utils.util.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.serializers.bungee.BungeeComponentSerializer;

import java.util.Collection;
import java.util.LinkedList;

@SuppressWarnings("unused")
public class WbsMessageBuilder {
    @NotNull
    private final WbsPlugin plugin;

    private final LinkedList<Component> components = new LinkedList<>();

    @SuppressWarnings("NotNullFieldNotInitialized") // initialized via the append method
    @NotNull
    private Component mostRecent;


    private static Component fromBungee(@SuppressWarnings("deprecation") BaseComponent ... bungeeComponent) {
        return BungeeComponentSerializer.get().deserialize(bungeeComponent);
    }
    @SuppressWarnings("deprecation")
    private static TextComponent fromBungeeText(net.md_5.bungee.api.chat.TextComponent bungeeComponent) {
        return (TextComponent) fromBungee(bungeeComponent);
    }

    public WbsMessageBuilder(@NotNull WbsPlugin plugin, String message) {
        this.plugin = plugin;
        append(message);
    }

    public WbsMessageBuilder append(String string) {
        return append(plugin.formatAsTextComponent(string));
    }
    public WbsMessageBuilder appendRaw(String string) {
        return append(Component.text(string));
    }
    public WbsMessageBuilder append(Component text) {
        mostRecent = text;
        components.add(text);
        return this;
    }
    public WbsMessageBuilder append(@SuppressWarnings("deprecation") net.md_5.bungee.api.chat.TextComponent text) {
        return append(fromBungeeText(text));
    }

    public WbsMessageBuilder prepend(String string) {
        return prepend(plugin.formatAsTextComponent(string));
    }
    public WbsMessageBuilder prependRaw(String string) {
        return prepend(Component.text(string));
    }
    public WbsMessageBuilder prepend(Component text) {
        mostRecent = text;
        components.add(0, text);
        return this;
    }
    public WbsMessageBuilder prepend(@SuppressWarnings("deprecation") net.md_5.bungee.api.chat.TextComponent text) {
        return prepend(fromBungeeText(text));
    }

    // Components are immutable -- after updating it, this should be called to replace the most recent element, too
    private void updateMostRecent() {
        components.removeLast();
        components.add(mostRecent);
    }

    public WbsMessageBuilder onHover(HoverEvent<?> onHover) {
        mostRecent = mostRecent.hoverEvent(onHover);
        updateMostRecent();
        return this;
    }
    public WbsMessageBuilder onHover(HoverEventSource<?> source) {
        mostRecent = mostRecent.hoverEvent(source.asHoverEvent());
        updateMostRecent();
        return this;
    }
    @SuppressWarnings("deprecation")
    public WbsMessageBuilder onHover(net.md_5.bungee.api.chat.HoverEvent onHover) {
        return onHover(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, fromBungee(onHover.getValue())));
    }
    public WbsMessageBuilder addHoverText(String string) {
        return onHover(HoverEvent.showText(fromBungeeText(plugin.formatAsTextComponent(string))));
    }
    public WbsMessageBuilder addHoverTextRaw(String string) {
        return addHoverText(Component.text(string));
    }
    public WbsMessageBuilder addHoverText(Component component) {
        return onHover(component);
    }

    @SuppressWarnings("deprecation")
    public WbsMessageBuilder onClick(net.md_5.bungee.api.chat.ClickEvent onClick) {
        ClickEvent.Action action = switch (onClick.getAction()) {
            case OPEN_URL -> ClickEvent.Action.OPEN_URL;
            case OPEN_FILE -> ClickEvent.Action.OPEN_FILE;
            case RUN_COMMAND -> ClickEvent.Action.RUN_COMMAND;
            case SUGGEST_COMMAND -> ClickEvent.Action.SUGGEST_COMMAND;
            case CHANGE_PAGE -> ClickEvent.Action.CHANGE_PAGE;
            case COPY_TO_CLIPBOARD -> ClickEvent.Action.COPY_TO_CLIPBOARD;
        };
        return onClick(ClickEvent.clickEvent(action, onClick.getValue()));
    }
    public WbsMessageBuilder onClick(ClickEvent onClick) {
        mostRecent = mostRecent.clickEvent(onClick);
        updateMostRecent();
        return this;
    }
    public WbsMessageBuilder addClickCommand(String command) {
        ClickEvent onClick = ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command);
        return onClick(onClick);
    }
    public WbsMessageBuilder addClickCommandSuggestion(String command) {
        ClickEvent onClick = ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
        return onClick(onClick);
    }

    /**
     * Set the formatting of the most recently added component to the formatting that would
     * be created from the legacy text provided, taking into account custom plugin codes.
     * @param legacyFormatting Legacy formatting text, such as "&amp;1" for blue or "&amp;o" for italics.
     */
    public WbsMessageBuilder setFormatting(String legacyFormatting) {
        TextComponent legacyComponent = fromBungeeText(plugin.formatAsTextComponent(legacyFormatting));

        mostRecent = mostRecent.color(legacyComponent.color());
        mostRecent = mostRecent.mergeStyle(legacyComponent);
        updateMostRecent();
        return this;
    }
    @SuppressWarnings("deprecation")
    public WbsMessageBuilder setColour(ChatColor colour) {
        mostRecent = mostRecent.color(TextColor.color(colour.getColor().getRGB()));
        updateMostRecent();
        return this;
    }

    public WbsMessage build() {
        return new WbsMessage(components);
    }

    @SafeVarargs
    public final <T extends CommandSender> WbsMessage send(T... receivers) {
        return new WbsMessage(components).send(receivers);
    }

    public WbsMessage send(Collection<? extends CommandSender> receivers) {
        return new WbsMessage(components).send(receivers);
    }

    public WbsMessage broadcast() {
        return new WbsMessage(components).broadcast();
    }

    public Component toComponent() {
        return Component.join(JoinConfiguration.builder().build(), components);
    }
}
