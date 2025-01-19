package wbs.utils.util.plugin;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class WbsMessage {
    private final List<Component> components;

    public WbsMessage(List<Component> components) {
        this.components = components;
    }

    @SafeVarargs
    public final <T extends CommandSender> WbsMessage send(BiConsumer<CommandSender, Component> sendMethod, T... receivers) {
        List<CommandSender> receiverList = new LinkedList<>(Arrays.asList(receivers));
        return send(sendMethod, receiverList);
    }

    public WbsMessage send(BiConsumer<CommandSender, Component> sendMethod, Collection<? extends CommandSender> receivers) {
        Component component = Component.join(JoinConfiguration.builder().build(), components);
        for (CommandSender sender : receivers) {
            sendMethod.accept(sender, component);
        }

        return this;
    }


    @SafeVarargs
    public final <T extends CommandSender> WbsMessage send(T... receivers) {
        return send(Audience::sendMessage, receivers);
    }

    public WbsMessage send(Collection<? extends CommandSender> receivers) {
        return send(Audience::sendMessage, receivers);
    }

    @SafeVarargs
    public final <T extends CommandSender> WbsMessage sendActionBar(T... receivers) {
        return send(Audience::sendActionBar, receivers);
    }

    public WbsMessage sendActionBar(Collection<? extends CommandSender> receivers) {
        return send(Audience::sendActionBar, receivers);
    }

    public WbsMessage broadcast() {
        return send(Bukkit.getOnlinePlayers());
    }
    public WbsMessage broadcastActionBar() {
        return sendActionBar(Bukkit.getOnlinePlayers());
    }
}
