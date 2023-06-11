package wbs.utils.util.plugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class WbsMessageBuilder {
    @NotNull
    private final WbsPlugin plugin;

    private final List<TextComponent> components = new LinkedList<>();

    @SuppressWarnings("NotNullFieldNotInitialized") // initialized via the append method
    @NotNull
    private TextComponent mostRecent;

    public WbsMessageBuilder(@NotNull WbsPlugin plugin, String message) {
        this.plugin = plugin;
        append(message);
    }

    public WbsMessageBuilder append(String string) {
        return append(plugin.formatAsTextComponent(string));
    }
    public WbsMessageBuilder appendRaw(String string) {
        return append(new TextComponent(string));
    }
    public WbsMessageBuilder append(TextComponent text) {
        mostRecent = text;
        components.add(text);
        return this;
    }

    public WbsMessageBuilder prepend(String string) {
        return prepend(plugin.formatAsTextComponent(string));
    }
    public WbsMessageBuilder prependRaw(String string) {
        return prepend(new TextComponent(string));
    }
    public WbsMessageBuilder prepend(TextComponent text) {
        mostRecent = text;
        components.add(0, text);
        return this;
    }

    public WbsMessageBuilder onHover(HoverEvent onHover) {
        mostRecent.setHoverEvent(onHover);
        return this;
    }
    public WbsMessageBuilder addHoverText(String string) {
        Text text = new Text(new TextComponent[] { plugin.formatAsTextComponent(string) });
        return addHoverText(text);
    }
    public WbsMessageBuilder addHoverTextRaw(String string) {
        Text text = new Text(string);
        return addHoverText(text);
    }
    public WbsMessageBuilder addHoverText(Text text) {
        HoverEvent onHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, text);
        return onHover(onHover);
    }

    public WbsMessageBuilder onClick(ClickEvent onClick) {
        mostRecent.setClickEvent(onClick);
        return this;
    }
    public WbsMessageBuilder addClickCommand(String command) {
        ClickEvent onClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
        return onClick(onClick);
    }
    public WbsMessageBuilder addClickCommandSuggestion(String command) {
        ClickEvent onClick = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
        return onClick(onClick);
    }

    /**
     * Set the formatting of the most recently added component to the formatting that would
     * be created from the legacy text provided, taking into account custom plugin codes.
     * @param legacyFormatting Legacy formatting text, such as "&amp;1" for blue or "&amp;o" for italics.
     */
    public WbsMessageBuilder setFormatting(String legacyFormatting) {
        TextComponent legacyComponent = plugin.formatAsTextComponent(legacyFormatting + "_");
        BaseComponent lastComponent = legacyComponent.getExtra().get(legacyComponent.getExtra().size() - 1);
        mostRecent.setColor(lastComponent.getColor());
        mostRecent.copyFormatting(lastComponent, ComponentBuilder.FormatRetention.ALL, true);
        return this;
    }
    public WbsMessageBuilder setColour(ChatColor colour) {
        mostRecent.setColor(colour);
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
}
