package wbs.utils.util.entities.state.tracker;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.entities.state.EntityState;
import wbs.utils.util.entities.state.EntityStateManager;

import java.util.*;

/**
 * An {@link EntityState} that captures the current {@link PlayerInventory} of a {@link Player}.
 * @see Player#getInventory()
 * @see PlayerInventory#getHeldItemSlot()
 */
@SuppressWarnings("unused")
public class InventoryState implements EntityState<Player>, ConfigurationSerializable {

    @Nullable
    private ItemStack[] contents;
    private int itemSlot = -1;

    /**
     * Creates the state with no items or item slot configured.
     */
    public InventoryState() {}

    /**
     * @param contents The items to track
     */
    public InventoryState(ItemStack[] contents) {
        this.contents = contents;
    }

    /**
     * @param contents The items to track
     * @param itemSlot The item slot (0-8, representing slots 1-9 in the hotbar) to track.
     */
    public InventoryState(ItemStack[] contents, int itemSlot) {
        this(contents);
        this.itemSlot = itemSlot;
    }

    @Override
    public void captureState(Player target) {
        ItemStack[] mutableContents = target.getInventory().getContents();
        contents = new ItemStack[mutableContents.length];
        for (int i = 0; i < mutableContents.length; i++) {
            ItemStack toClone = mutableContents[i];
            if (toClone != null) {
                contents[i] = toClone.clone();
            }
        }

        itemSlot = target.getInventory().getHeldItemSlot();
    }

    @Override
    public void restoreState(Player target) {
        if (contents != null) {
            target.getInventory().setContents(contents);
        }
        if (itemSlot >= 0) {
            target.getInventory().setHeldItemSlot(itemSlot);
        }
    }

    /**
     * @return The items to track
     */
    public @Nullable ItemStack[] getContents() {
        return contents;
    }

    /**
     * @param contents The items to track
     */
    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    /**
     * @return The item slot (0-8, representing slots 1-9 in the hotbar) to track.
     */
    public int getItemSlot() {
        return itemSlot;
    }

    /**
     * @param itemSlot The item slot (0-8, representing slots 1-9 in the hotbar) to track.
     */
    public void setItemSlot(int itemSlot) {
        this.itemSlot = itemSlot;
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        // As some world/region plugins manage inventories on teleport, restore after & capture before loc change.
        return new HashSet<>(Collections.singletonList(LocationState.class));
    }

    // Serialization
    private static final String CONTENTS = "contents";
    private static final String ITEM_SLOT = "item-slot";

    /**
     * Deserializer method for converting a {@link Map} into this object, for use in {@link EntityStateManager}
     * @param args The partially deserialized Map.
     * @return An instance of this class, deserialized from args.
     */
    public static InventoryState deserialize(Map<String, Object> args) {
        InventoryState state = new InventoryState();

        Object contents = args.get(CONTENTS);
        if (contents instanceof Map) {
            Map<?, ?> contentsMap = (Map<?, ?>) contents;

            int maxIndex = 0;
            for (Object i : contentsMap.keySet()) {
                if (i instanceof Integer) {
                    int index = (int) i;

                    if (index > maxIndex) {
                        maxIndex = index;
                    }
                }
            }

            ItemStack[] contentsArray = new ItemStack[maxIndex + 1];

            for (Object i : contentsMap.keySet()) {
                if (i instanceof Integer) {
                    int index = (int) i;
                    Object value = contentsMap.get(i);
                    if (value instanceof ItemStack) {
                        ItemStack item = (ItemStack) value;
                        contentsArray[index] = item;
                    }
                }
            }

            state.setContents(contentsArray);
        }

        Object itemSlot = args.get(ITEM_SLOT);
        if (itemSlot instanceof Integer) {
            state.setItemSlot((int) itemSlot);
        }

        return state;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        Map<Integer, ItemStack> contentMap = new HashMap<>();

        int index = 0;
        for (ItemStack item : contents) {
            if (item != null) {
                contentMap.put(index, item);
            }
            index++;
        }

        map.put(CONTENTS, contentMap);
        map.put(ITEM_SLOT, itemSlot);

        return map;
    }
}
