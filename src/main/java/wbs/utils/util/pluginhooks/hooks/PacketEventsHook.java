package wbs.utils.util.pluginhooks.hooks;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.protocol.advancements.*;
import com.github.retrooper.packetevents.protocol.entity.EntityPositionData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.settings.PacketEventsSettings;
import com.github.retrooper.packetevents.util.TimeStampMode;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.MainHand;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.WbsUtils;
import wbs.utils.util.WbsEntities;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.entities.WbsEntityUtil;
import wbs.utils.util.entities.WbsPlayerUtil;
import wbs.utils.util.pluginhooks.PluginHook;

import java.util.*;

public final class PacketEventsHook extends PluginHook implements PacketEventsWrapper {
    public PacketEventsHook() {
        super(PLUGIN_NAME);
    }

    @Override
    public void fullStackTrace(boolean enabled) {
        PacketEventsSettings settings = PacketEvents.getAPI().getSettings();

        //noinspection UnstableApiUsage
        settings.fullStackTrace(enabled);
    }

    static @Nullable User getUser(Player player) {
        User user = getChannel(player.getUniqueId())
                .map(channel -> PacketEvents.getAPI().getProtocolManager().getUser(channel))
                .orElse(null);

        if (user == null) {
            WbsUtils.getInstance().getLogger().warning("Failed to send packet to player " + player.getName() + " maybe a packetevents issue or the user is offline.");
        }

        return user;
    }

    static Optional<Object> getChannel(UUID uuid) {
        try {
            return Optional.ofNullable(PacketEvents.getAPI().getProtocolManager().getChannel(uuid));
        } catch (NullPointerException ignored) {
            return Optional.empty();
        }
    }

    static void sendPacket(PacketWrapper<?> packet, Player ... players) {
        for (Player player : players) {
            sendPacket(player, packet);
        }
    }

    static void sendPacket(Player player, PacketWrapper<?> packet) {
        User user = getUser(player);
        if (user == null) {
            return;
        }

        user.sendPacket(packet);
    }

    public void updateTickRate(int tickRate, Player ... players) {
        WrapperPlayServerTickingState packet = new WrapperPlayServerTickingState(tickRate, false);

        sendPacket(packet, players);
    }

    public void removeEntity(Entity entity, Player ... players) {
        WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(entity.getEntityId());

        sendPacket(packet, players);
    }

    public void teleportEntity(Entity entity, org.bukkit.Location bukkitLocation, Player ... players) {
        Location location = SpigotConversionUtil.fromBukkitLocation(bukkitLocation);
        WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport(entity.getEntityId(), location, entity.isOnGround());

        sendPacket(packet, players);
    }


    public void updateEntityPosition(Entity entity, Player ... players) {
        Vector velocity = entity.getVelocity();
        EntityPositionData entityPositionData = new EntityPositionData(
                new Vector3d(entity.getX(), entity.getY(), entity.getZ()),
                new Vector3d(velocity.getX(), velocity.getY(), velocity.getZ()),
                entity.getYaw(),
                entity.getPitch()
        );
        WrapperPlayServerEntityPositionSync packet = new WrapperPlayServerEntityPositionSync(entity.getEntityId(), entityPositionData, entity.isOnGround());

        sendPacket(packet, players);
    }

    public void updateEntity(Entity entity, Player ... players) {
        List<EntityData<?>> metadata = SpigotConversionUtil.getEntityMetadata(entity);
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(entity.getEntityId(), metadata);

        sendPacket(packet, players);
    }

    public void showFakeEntity(Entity entity, Player ... players) {
        EntityType entityType = SpigotConversionUtil.fromBukkitEntityType(entity.getType());
        Location location = SpigotConversionUtil.fromBukkitLocation(entity.getLocation());

        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(entity.getEntityId(), entity.getUniqueId(), entityType, location, location.getYaw(), 0, null);

        sendPacket(packet, players);

        updateEntity(entity, players);
    }

    public void sendGameModeChange(org.bukkit.GameMode bukkitGameMode, Player ... players) {
        GameMode gameMode = SpigotConversionUtil.fromBukkitGameMode(bukkitGameMode);
        WrapperPlayServerChangeGameState packet = new WrapperPlayServerChangeGameState(WrapperPlayServerChangeGameState.Reason.CHANGE_GAME_MODE, gameMode.getId());

        sendPacket(packet, players);
    }


    /**
     * @author doc (Discord) aka mrdoc.dev & Doc94.
     */
    public void sendToast(org.bukkit.inventory.ItemStack icon, Component message, io.papermc.paper.advancement.AdvancementDisplay.Frame displayType, Player ... players) {
        String criterionName = "trigger";
        List<String> criteria = Collections.singletonList(criterionName);
        List<List<String>> requirements = Collections.singletonList(criteria);

        ItemStack itemStack = SpigotConversionUtil.fromBukkitItemStack(icon);

        final AdvancementDisplay advancementDisplay = new AdvancementDisplay(
                message,
                Component.empty(),
                itemStack,
                AdvancementType.valueOf(displayType.name()),
                null,
                true,
                false,
                0.0f,
                0.0f
        );

        @Subst("key")
        String key = UUID.randomUUID().toString();
        final ResourceLocation resourceLocation = ResourceLocation.minecraft(key);

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

        sendPacket(showPacket, players);

        WrapperPlayServerUpdateAdvancements removePacket = new WrapperPlayServerUpdateAdvancements(
                false,
                Collections.emptyList(),
                Collections.singleton(resourceLocation),
                Collections.emptyMap(),
                false
        );

        sendPacket(removePacket, players);
    }

    @Override
    public void simulatePlayerInteract(EquipmentSlot hand, RayTraceResult rayTraceResult, double range, Player... players) {
        for (Player player : players) {
            User user = getUser(player);

            if (user == null) {
                continue;
            }

            InteractionHand interactionHand;
            if (hand == EquipmentSlot.HAND) {
                interactionHand = InteractionHand.MAIN_HAND;
            } else if (hand == EquipmentSlot.OFF_HAND) {
                interactionHand = InteractionHand.OFF_HAND;
            } else {
                throw new IllegalArgumentException("Hand slot must be a hand.");
            }

            Block hitBlock = rayTraceResult.getHitBlock();
            if (hitBlock == null) {
                hitBlock = player.getEyeLocation().add(WbsEntityUtil.getFacingVector(player, range)).getBlock();
            }

            BlockFace hitBlockFace = rayTraceResult.getHitBlockFace();
            if (hitBlockFace == null) {
                hitBlockFace = getClosestFace(WbsEntityUtil.getFacingVector(player), Set.of(
                        BlockFace.UP,
                        BlockFace.DOWN,
                        BlockFace.NORTH,
                        BlockFace.EAST,
                        BlockFace.SOUTH,
                        BlockFace.WEST
                ));
            }

            com.github.retrooper.packetevents.protocol.world.BlockFace face = WbsEnums.getEnumFromString(com.github.retrooper.packetevents.protocol.world.BlockFace.class, hitBlockFace.toString());
            if (face == null) {
                face = com.github.retrooper.packetevents.protocol.world.BlockFace.OTHER;
            }

            WrapperPlayClientPlayerBlockPlacement packet = new WrapperPlayClientPlayerBlockPlacement(
                    interactionHand,
                    SpigotConversionUtil.fromBukkitLocation(hitBlock.getLocation().toBlockLocation()).getPosition().toVector3i(),
                    face,
                    toVector3f(rayTraceResult.getHitPosition()),
                    SpigotConversionUtil.fromBukkitItemStack(player.getInventory().getItem(hand)),
                    player.getEyeLocation().getBlock().getType().isCollidable(),
                    false, // TODO: Validate that this doesn't cause issues lol
                    0
            );

            user.receivePacket(packet);
        }
    }

    private Vector3d toVector3d(Vector vector) {
        return new Vector3d(vector.getX(), vector.getY(), vector.getZ());
    }

    private Vector3f toVector3f(Vector vector) {
        return new Vector3f((float) vector.getX(), (float) vector.getY(), (float) vector.getZ());
    }

    private Vector3i toVector3i(Vector vector) {
        return new Vector3i(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    @NotNull
    private static BlockFace getClosestFace(@NotNull Vector facing, @NotNull Set<@NotNull BlockFace> options) {
        if (options.isEmpty()) {
            throw new IllegalArgumentException("Facing options cannot be empty");
        }

        BlockFace closest = BlockFace.UP;
        double smallestAngle = 180;
        for (BlockFace option : options) {
            double angle = facing.angle(option.getDirection());

            if (angle < smallestAngle) {
                smallestAngle = angle;
                closest = option;
            }
        }

        return closest;
    }
}
