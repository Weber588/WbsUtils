package wbs.utils.util.pluginhooks;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.advancements.*;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAdvancements;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
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

    public static boolean sendToast(org.bukkit.inventory.ItemStack icon, Component message, io.papermc.paper.advancement.AdvancementDisplay.Frame displayType, Player player) {
        if (isActive()) {
            PacketEventsWrapper.sendToastUnsafe(icon, message, displayType, player);
            return true;
        }
        return false;
    }

    /**
     * @author doc (Discord) aka mrdoc.dev & Doc94.
     */
    private static void sendToastUnsafe(org.bukkit.inventory.ItemStack icon, Component title, io.papermc.paper.advancement.AdvancementDisplay.Frame displayType, Player player) {
        User user = getChannel(player.getUniqueId()).map(channel -> PacketEvents.getAPI().getProtocolManager().getUser(channel)).orElse(null);
        if (user == null) {
            WbsUtils.getInstance().getLogger().warning("Failed to send toast announcement to player " + player.getName() + " maybe a packetevents issue or the user is offline.");
            return;
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
                criteria,
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
    }

    private static Optional<Object> getChannel(UUID uuid) {
        try {
            return Optional.of(PacketEvents.getAPI().getProtocolManager().getChannel(uuid));
        } catch (NullPointerException ignored) {
            return Optional.empty();
        }
    }
}
