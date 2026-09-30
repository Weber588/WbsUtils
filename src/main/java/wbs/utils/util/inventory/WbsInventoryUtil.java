package wbs.utils.util.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@NullMarked
public class WbsInventoryUtil {
    public static Map<Integer, ItemStack> transferItems(Inventory source, Inventory destination, Predicate<ItemStack> itemPredicate) {
        Map<Integer, ItemStack> toTransfer = new HashMap<>();

        @Nullable ItemStack[] contents = source.getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null) {
                if (itemPredicate.test(item)) {
                    toTransfer.put(i, item);
                }
            }
        }

        Map<Integer, ItemStack> transferred = new HashMap<>();

        for (Map.Entry<Integer, ItemStack> entry : toTransfer.entrySet()) {
            Integer index = entry.getKey();
            ItemStack item = entry.getValue();
            HashMap<Integer, ItemStack> failedToAdd = destination.addItem(item.clone());
            ItemStack failedItem = failedToAdd.get(0);
            if (failedItem != null) {
                int failedAmount = failedItem.getAmount();
                int movedAmount = item.getAmount() - failedAmount;
                if (movedAmount > 0) {
                    transferred.put(index, item.asQuantity(movedAmount));
                }

                item.setAmount(failedAmount);
            } else {
                // Nothing failed, remove from original inventory
                source.clear(index);
                transferred.put(index, item);
            }
        }

        return transferred;
    }
}
