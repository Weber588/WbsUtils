package wbs.utils.util.plugin;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class WbsMessage {
    private final List<TextComponent> components;

    public WbsMessage(List<TextComponent> components) {
        this.components = components;
    }

    public TextComponent[] getComponentArray() {
        return components.toArray(new TextComponent[0]);
    }

    @SafeVarargs
    public final <T extends CommandSender> WbsMessage send(T... receivers) {
        List<CommandSender> receiverList = new LinkedList<>(Arrays.asList(receivers));
        return send(receiverList);
    }

    public WbsMessage send(Collection<? extends CommandSender> receivers) {
        TextComponent[] componentArray = getComponentArray();
        for (CommandSender sender : receivers) {
            sender.spigot().sendMessage(componentArray);
        }

        return this;
    }

    public WbsMessage broadcast() {
        return send(Bukkit.getOnlinePlayers());
    }
}
