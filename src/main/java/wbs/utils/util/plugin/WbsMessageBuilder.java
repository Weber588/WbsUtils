package wbs.utils.util.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

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

    public WbsMessageBuilder(@NotNull WbsPlugin plugin, String message) {
        this.plugin = plugin;
        append(message);
    }
    public WbsMessageBuilder(@NotNull WbsPlugin plugin, Component message) {
        this.plugin = plugin;
        append(message);
    }

    public WbsMessageBuilder append(String string) {
        return append(plugin.getFormattedMessage(string));
    }
    public WbsMessageBuilder appendRaw(String string) {
        return append(Component.text(string));
    }
    public WbsMessageBuilder append(Component text) {
        text = text.applyFallbackStyle(plugin.getDefaultStyle());
        mostRecent = text;
        components.add(text);
        return this;
    }

    public WbsMessageBuilder prepend(String string) {
        return prepend(plugin.getFormattedMessage(string));
    }
    public WbsMessageBuilder prependRaw(String string) {
        return prepend(Component.text(string));
    }
    public WbsMessageBuilder prepend(Component text) {
        mostRecent = text;
        components.addFirst(text);
        return this;
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
    public WbsMessageBuilder addHoverText(String string) {
        return onHover(HoverEvent.showText(plugin.getFormattedMessage(string)));
    }
    public WbsMessageBuilder addHoverTextRaw(String string) {
        return addHoverText(Component.text(string));
    }
    public WbsMessageBuilder addHoverText(Component component) {
        return onHover(component);
    }

    public WbsMessageBuilder onClick(ClickEvent<?> onClick) {
        mostRecent = mostRecent.clickEvent(onClick);
        updateMostRecent();
        return this;
    }
    public WbsMessageBuilder addClickCommand(String command) {
        ClickEvent<?> onClick = ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, ClickEvent.Payload.string(command));
        return onClick(onClick);
    }
    public WbsMessageBuilder addClickCommandSuggestion(String command) {
        ClickEvent<?> onClick = ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, ClickEvent.Payload.string(command));
        return onClick(onClick);
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
