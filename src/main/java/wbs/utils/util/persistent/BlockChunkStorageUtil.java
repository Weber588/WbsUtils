package wbs.utils.util.persistent;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import wbs.utils.WbsUtils;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class BlockChunkStorageUtil {
    public static final NamespacedKey TAG = new NamespacedKey(WbsUtils.getInstance(), "block_containers");

    public static NamespacedKey getBlockKey(Block block) {
        return new NamespacedKey(WbsUtils.getInstance(), block.getX() + "_" + block.getY() + '_' + block.getZ());
    }

    private static Location locationFromKey(NamespacedKey key) {
        String asString = key.value();

        String[] args = asString.split("_");
        if (args.length != 3) {
            throw new IllegalStateException("Invalid block key: " + key.asString());
        }

        return new Location(null, Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }

    @NotNull
    public static PersistentDataContainer getContainer(Block block) {
        PersistentDataContainer chunkContainer = block.getChunk().getPersistentDataContainer();
        PersistentDataContainer blocksContainer = chunkContainer.get(TAG, PersistentDataType.TAG_CONTAINER);

        NamespacedKey blockKey = getBlockKey(block);
        if (blocksContainer == null) {
            return chunkContainer.getAdapterContext().newPersistentDataContainer();
        }

        PersistentDataContainer blockContainer = blocksContainer.get(blockKey, PersistentDataType.TAG_CONTAINER);

        if (blockContainer == null) {
            return blocksContainer.getAdapterContext().newPersistentDataContainer();
        }

        return blockContainer;
    }

    public static void writeContainer(Block block, @NotNull PersistentDataContainer container) {
        PersistentDataContainer chunkContainer = block.getChunk().getPersistentDataContainer();
        PersistentDataContainer blocksContainer = chunkContainer.get(TAG, PersistentDataType.TAG_CONTAINER);
        if (blocksContainer == null) {
            blocksContainer = chunkContainer.getAdapterContext().newPersistentDataContainer();
        }

        NamespacedKey blockKey = getBlockKey(block);

        blocksContainer.set(blockKey, PersistentDataType.TAG_CONTAINER, container);
        chunkContainer.set(TAG, PersistentDataType.TAG_CONTAINER, blocksContainer);
    }

    public static void modifyContainer(Block block, Consumer<PersistentDataContainer> consumer) {
        PersistentDataContainer container = getContainer(block);
        consumer.accept(container);
        writeContainer(block, container);
    }

    public static List<PersistentDataContainer> getBlockContainersInChunk(Chunk chunk) {
        PersistentDataContainer chunkContainer = chunk.getPersistentDataContainer().get(TAG, PersistentDataType.TAG_CONTAINER);

        List<PersistentDataContainer> blocks = new LinkedList<>();
        if (chunkContainer == null) {
            return blocks;
        }

        Set<NamespacedKey> keys = chunkContainer.getKeys();
        for (NamespacedKey key : keys) {
            PersistentDataContainer container = chunkContainer.get(key, PersistentDataType.TAG_CONTAINER);

            if (container != null) {
                blocks.add(container);
            }
        }

        return blocks;
    }

    public static Map<Block, PersistentDataContainer> getBlockContainerMap(Chunk chunk) {
        PersistentDataContainer chunkContainer = chunk.getPersistentDataContainer().get(TAG, PersistentDataType.TAG_CONTAINER);

        Map<Block, PersistentDataContainer> blocks = new HashMap<>();
        if (chunkContainer == null) {
            return blocks;
        }

        Set<NamespacedKey> keys = chunkContainer.getKeys();
        for (NamespacedKey key : keys) {
            PersistentDataContainer container = chunkContainer.get(key, PersistentDataType.TAG_CONTAINER);

            if (container != null) {
                try {
                    Location location = locationFromKey(key);
                    location.setWorld(chunk.getWorld());
                    Block block = location.getBlock();

                    blocks.put(block, container);
                } catch (IllegalStateException ignored) {}
            }
        }

        return blocks;
    }
}
