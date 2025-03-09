package wbs.utils.util;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import wbs.utils.WbsUtils;

@SuppressWarnings("unused")
public class WbsEventUtils {
    public static ItemStack getItemAddedToTopInventory(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        Inventory clicked = event.getClickedInventory();

        ItemStack addedItem = null;
        if (clicked == topInventory && clicked != event.getWhoClicked().getInventory()) {
            addedItem = event.getCursor();

            if (event.getClick() == ClickType.SWAP_OFFHAND) {
                addedItem = event.getWhoClicked().getInventory().getItemInOffHand();
            } else if (event.getClick() == ClickType.NUMBER_KEY) {
                addedItem = event.getView().getBottomInventory().getItem(event.getHotbarButton());
            }
        }

        if (clicked != null && clicked.getType() == InventoryType.PLAYER) {
            if (event.getSlot() != -1) {
                if (event.isShiftClick()) {
                    addedItem = clicked.getItem(event.getSlot());
                }
            }
        }

        if (addedItem != null && addedItem.isEmpty()) {
            addedItem = null;
        }

        return addedItem;
    }
}
