package wbs.utils.util.pluginhooks;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.advancements.*;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.protocol.world.chunk.LightData;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChangeGameMode;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import wbs.utils.WbsUtils;

import java.util.*;

/**
 * Utilities for the PacketEvents API
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public final class PacketEventsWrapper {
    private PacketEventsWrapper() {}

    public static boolean isActive() {
        return PluginHookManager.isPacketEventsInstalled();
    }

    public static boolean updateTickRate(Player player, int tickRate) {
        if (isActive()) {
            return updateTickRateUnsafe(player, tickRate);
        }
        return false;
    }

    private static boolean updateTickRateUnsafe(Player player, int tickRate) {
        WrapperPlayServerTickingState packet = new WrapperPlayServerTickingState(tickRate, false);

        User user = getUser(player);
        if (user == null) {
            return false;
        }

        user.sendPacket(packet);
        return true;
    }

    public static boolean removeEntity(Player player, Entity entity) {
        if (isActive()) {
            return removeEntityUnsafe(player, entity);
        }
        return false;
    }

    private static boolean removeEntityUnsafe(Player player, Entity entity) {
        EntityType entityType = SpigotConversionUtil.fromBukkitEntityType(entity.getType());
        Location location = SpigotConversionUtil.fromBukkitLocation(entity.getLocation());
        List<EntityData<?>> metadata = SpigotConversionUtil.getEntityMetadata(entity);
        WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(entity.getEntityId());

        User user = getUser(player);
        if (user == null) {
            return false;
        }

        user.sendPacket(packet);
        return true;
    }

    public static boolean updateEntity(Player player, Entity entity) {
        if (isActive()) {
            return updateEntityUnsafe(player, entity);
        }
        return false;
    }

    private static boolean updateEntityUnsafe(Player player, Entity entity) {
        EntityType entityType = SpigotConversionUtil.fromBukkitEntityType(entity.getType());
        Location location = SpigotConversionUtil.fromBukkitLocation(entity.getLocation());
        List<EntityData<?>> metadata = SpigotConversionUtil.getEntityMetadata(entity);
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(entity.getEntityId(), metadata);

        User user = getUser(player);
        if (user == null) {
            return false;
        }

        user.sendPacket(packet);
        return true;
    }

    public static boolean showFakeEntity(Player player, Entity entity) {
        if (isActive()) {
            return showFakeEntityUnsafe(player, entity);
        }
        return false;
    }

    private static boolean showFakeEntityUnsafe(Player player, Entity entity) {
        EntityType entityType = SpigotConversionUtil.fromBukkitEntityType(entity.getType());
        Location location = SpigotConversionUtil.fromBukkitLocation(entity.getLocation());
        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(entity.getEntityId(), entity.getUniqueId(), entityType, location, location.getYaw(), 0, null);

        User user = getUser(player);
        if (user == null) {
            return false;
        }

        user.sendPacket(packet);

        return updateEntityUnsafe(player, entity);
    }

    public static boolean sendGameModeChange(Player player, org.bukkit.GameMode gameMode) {
        if (isActive()) {
            return sendGameModeChangeUnsafe(player, gameMode);
        }
        return false;
    }

    private static boolean sendGameModeChangeUnsafe(Player player, org.bukkit.GameMode bukkitGameMode) {
        GameMode gameMode = SpigotConversionUtil.fromBukkitGameMode(bukkitGameMode);
        WrapperPlayServerChangeGameState packet = new WrapperPlayServerChangeGameState(WrapperPlayServerChangeGameState.Reason.CHANGE_GAME_MODE, gameMode.getId());

        User user = getUser(player);
        if (user == null) {
            return false;
        }
        user.sendPacket(packet);
        return true;
    }

    public static boolean sendToast(org.bukkit.inventory.ItemStack icon, Component message, io.papermc.paper.advancement.AdvancementDisplay.Frame displayType, Player player) {
        if (isActive()) {
            return sendToastUnsafe(icon, message, displayType, player);
        }
        return false;
    }

    /**
     * @author doc (Discord) aka mrdoc.dev & Doc94.
     */
    private static boolean sendToastUnsafe(org.bukkit.inventory.ItemStack icon, Component title, io.papermc.paper.advancement.AdvancementDisplay.Frame displayType, Player player) {
        User user = getUser(player);
        if (user == null) {
            return false;
        }

        String criterionName = "trigger";
        List<String> criteria = Collections.singletonList(criterionName);
        List<List<String>> requirements = Collections.singletonList(criteria);

        ItemStack itemStack = SpigotConversionUtil.fromBukkitItemStack(icon);

        final AdvancementDisplay advancementDisplay = new AdvancementDisplay(
                title,
                Component.empty(),
                itemStack,
                AdvancementType.valueOf(displayType.name()),
                null,
                true,
                false,
                0.0f,
                0.0f
        );

        final ResourceLocation resourceLocation = ResourceLocation.minecraft(UUID.randomUUID().toString());

        Advancement advancement = new Advancement(
                null,
                advancementDisplay,
                requirements,
                false
        );

        List<AdvancementHolder> advancementHolders = Collections.singletonList(
                new AdvancementHolder(resourceLocation, advancement)
        );

        Map<String, AdvancementProgress.CriterionProgress> progressMap = new HashMap<>();
        progressMap.put(criterionName, new AdvancementProgress.CriterionProgress(System.currentTimeMillis()));

        AdvancementProgress progress = new AdvancementProgress(progressMap);

        WrapperPlayServerUpdateAdvancements showPacket = new WrapperPlayServerUpdateAdvancements(
                false,
                advancementHolders,
                Collections.emptySet(),
                Collections.singletonMap(resourceLocation, progress),
                true
        );

        user.sendPacket(showPacket);

        WrapperPlayServerUpdateAdvancements removePacket = new WrapperPlayServerUpdateAdvancements(
                false,
                Collections.emptyList(),
                Collections.singleton(resourceLocation),
                Collections.emptyMap(),
                false
        );

        user.sendPacket(removePacket);
        return true;
    }

    private static @Nullable User getUser(Player player) {
        User user = getChannel(player.getUniqueId())
                .map(channel -> PacketEvents.getAPI().getProtocolManager().getUser(channel))
                .orElse(null);

        if (user == null) {
            WbsUtils.getInstance().getLogger().warning("Failed to send packet to player " + player.getName() + " maybe a packetevents issue or the user is offline.");
        }

        return user;
    }

    private static Optional<Object> getChannel(UUID uuid) {
        try {
            return Optional.of(PacketEvents.getAPI().getProtocolManager().getChannel(uuid));
        } catch (NullPointerException ignored) {
            return Optional.empty();
        }
    }
}
