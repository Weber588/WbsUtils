package wbs.utils.util.entities.state.tracker;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.entities.state.EntityState;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class InventoryState implements EntityState<Player> {

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
}
