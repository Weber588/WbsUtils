package wbs.utils.util;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import wbs.utils.util.plugin.WbsPlugin;

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

    public static <T extends Event> void register(WbsPlugin plugin, Class<T> eventClass, EventHandlerMethod<T> handler) {
        register(plugin, eventClass, handler, EventPriority.NORMAL);
    }

    public static <T extends Event> void register(WbsPlugin plugin, Class<T> eventClass, EventHandlerMethod<T> handler, EventPriority priority) {
        register(plugin, eventClass, handler, priority, true);
    }

    public static <T extends Event> void register(WbsPlugin plugin, Class<T> eventClass, EventHandlerMethod<T> handler, EventPriority priority, boolean ignoreCancelled) {
        Bukkit.getPluginManager().registerEvent(eventClass,
                handler,
                priority,
                (ignored, event) -> execute(eventClass, handler, event),
                plugin,
                ignoreCancelled);
    }

    private static <T extends Event> void execute(Class<T> eventClass, EventHandlerMethod<T> handler, Event event) {
        if (!eventClass.isInstance(event)) {
            return;
        }
        T castEvent = eventClass.cast(event);
        handler.handle(castEvent);
    }

    @FunctionalInterface
    public interface EventHandlerMethod<T extends Event> extends Listener {
        void handle(T event);
    }
}
