package wbs.utils.util.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class WbsMessage {
    private final List<Component> components;

    public WbsMessage(List<Component> components) {
        this.components = components;
    }
    @SafeVarargs
    public final <T extends CommandSender> WbsMessage send(T... receivers) {
        List<CommandSender> receiverList = new LinkedList<>(Arrays.asList(receivers));
        return send(receiverList);
    }

    public WbsMessage send(Collection<? extends CommandSender> receivers) {
        Component component = Component.join(JoinConfiguration.builder().build(), components);
        for (CommandSender sender : receivers) {
            sender.sendMessage(component);
        }

        return this;
    }

    public WbsMessage broadcast() {
        return send(Bukkit.getOnlinePlayers());
    }
}
