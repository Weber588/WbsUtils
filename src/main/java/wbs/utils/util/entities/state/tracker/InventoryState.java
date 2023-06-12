package wbs.utils.util.entities.state.tracker;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.entities.state.EntityState;

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

    public InventoryState() {}
    public InventoryState(ItemStack[] contents) {
        this.contents = contents;
    }
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

    public @Nullable ItemStack[] getContents() {
        return contents;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public int getItemSlot() {
        return itemSlot;
    }

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
