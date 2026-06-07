package wbs.utils.util.pluginhooks.hooks;

import com.google.common.collect.Iterables;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.pluginhooks.PluginHookManager;
import wbs.utils.util.pluginhooks.PluginHookWrapper;

import java.util.Collection;
import java.util.Optional;

import static io.papermc.paper.advancement.AdvancementDisplay.*;

/**
 * Utilities for the PacketEvents API
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public interface PacketEventsWrapper extends PluginHookWrapper {
    String PLUGIN_NAME = "packetevents";
    static @NotNull Optional<PacketEventsWrapper> get() {
        return Optional.ofNullable(PluginHookManager.getHook(PacketEventsWrapper.class, PLUGIN_NAME));
    }

    static boolean isActive() {
        return get().isPresent();
    }

    void updateTickRate(int tickRate, Player ... players);
    default void updateTickRate(int tickRate, Collection<Player> players) {
        updateTickRate(tickRate, players.toArray(Player[]::new));
    }

    void removeEntity(Entity entity, Player ... players);
    default void removeEntity(Entity entity, Collection<Player> players) {
        removeEntity(entity, players.toArray(Player[]::new));
    }

    void teleportEntity(Entity entity, Location bukkitLocation, Player ... players);
    default void teleportEntity(Entity entity, Location bukkitLocation, Collection<Player> players) {
        teleportEntity(entity, bukkitLocation, players.toArray(Player[]::new));
    }

    void updateEntityPosition(Entity entity, Player ... players);
    default void updateEntityPosition(Entity entity, Collection<Player> players) {
        updateEntityPosition(entity, players.toArray(Player[]::new));
    }

    void updateEntity(Entity entity, Player ... players);
    default void updateEntity(Entity entity, Collection<Player> players) {
        updateEntity(entity, players.toArray(Player[]::new));
    }

    void showFakeEntity(Entity entity, Player ... players);
    default void showFakeEntity(Entity entity, Collection<Player> players) {
        showFakeEntity(entity, players.toArray(Player[]::new));
    }

    void sendGameModeChange(GameMode bukkitGameMode, Player ... players);
    default void sendGameModeChange(GameMode bukkitGameMode, Collection<Player> players) {
        sendGameModeChange(bukkitGameMode, players.toArray(Player[]::new));
    }

    void sendToast(ItemStack icon, Component message, Frame displayType, Player ... players);
    default void sendToast(ItemStack icon, Component message, Frame displayType, Collection<Player> players) {
        sendToast(icon, message, displayType, players.toArray(Player[]::new));
    }
}
