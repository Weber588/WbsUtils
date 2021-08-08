package wbs.utils.util.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.string.WbsStrings;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class WbsMenu implements Listener {

    private final WbsPlugin plugin;
    private final int rows;

    private final String titleString;

    private final Map<Integer, MenuSlot> slots = new HashMap<>();

    private boolean unregisterOnClose = false;

    /**
     * @param plugin The plugin to register this menu to
     * @param title The title of the menu to show to players
     * @param rows How many rows to create, between 1 and 6 inclusive
     * @param id A unique identifier (per plugin) for identifying inventory clicks
     */
    public WbsMenu(WbsPlugin plugin, String title, int rows, String id) {
        if (rows < 1) throw new IllegalArgumentException("Rows must be greater than 1");
        if (rows > 6) throw new IllegalArgumentException("Rows may not be greater than 6");

        this.plugin = plugin;
        this.rows = rows;

        titleString = plugin.dynamicColourise(title) + WbsStrings.getInvisibleString(plugin.getName() + ":" + id);

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(this, plugin);
    }

    private boolean unregister(Player cause) {
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);

        boolean wasOpen = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == cause) continue;

            if (hasMenuOpen(player)) {
                player.closeInventory();
                plugin.sendMessage("&wMenu was unregistered!", player);
                wasOpen = true;
            }
        }

        return wasOpen;
    }

    /**
     * Unregisters this menu, and closes it for any players that have it open.
     * Once unregistered, the menu will cease to function.
     * @return True any players had the menu open when it was unregistered.
     */
    public boolean unregister() {
        return unregister(null);
    }

    private int getMaxSlot() {
        return rows * 9 - 1;
    }

    /**
     * Set a menu slot for this
     * @param i The number of the slot
     * @param slot The menu slot that defines the item, action,
     *             and various other properties
     */
    public void setSlot(int i, MenuSlot slot) {
        if (i > getMaxSlot())
            throw new IndexOutOfBoundsException("Max slot is " + getMaxSlot() + " for a menu with " + rows + " rows");

        if (i < 0)
            throw new IndexOutOfBoundsException("Slot must be greater than or equal to 0");

        slots.put(i, slot);
    }

    public void setUnregisterOnClose(boolean unregisterOnClose) {
        this.unregisterOnClose = unregisterOnClose;
    }

    private Inventory buildInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(player, rows * 9, titleString);


        slots.forEach((slotNumber, slot) -> {
            ItemStack formattedItem = slot.getFormattedItem(player);

            inventory.setItem(slotNumber, formattedItem);
        });

        return inventory;
    }

    public void showTo(Player player) {
        Inventory inventory = buildInventory(player);

        player.openInventory(inventory);
    }

    public boolean hasMenuOpen(Player player) {
        return player.getOpenInventory().getTitle().equals(titleString);
    }

    public boolean isMenu(InventoryView view) {
        return view.getTitle().equals(titleString);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClose(InventoryCloseEvent event) {
        if (isMenu(event.getView())) {
            if (unregisterOnClose) unregister((Player) event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(InventoryClickEvent event) {
        if (isMenu(event.getView())) {
            InventoryView view = event.getView();
            int clicked = event.getSlot();

            MenuSlot slot = slots.get(clicked);
            if (event.getView().getBottomInventory()
                    .equals(event.getClickedInventory()) || slot == null) {
                event.setCancelled(true);
                return;
            }

            if (!slot.allowItemTaking())
                event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();

            if (slot.closeOnClick())
                player.closeInventory();

            if (slot.getClickAction() != null)
                slot.getClickAction().accept(event);
        }
    }

    // ============================== //
    //          Helper methods        //
    // ============================== //

    /**
     * Add a slot in the next available slot in the menu if any are available
     * @param slot The slot to add at the first unassigned slot
     * @return Whether or not there was a free space to fill.
     */
    public boolean setNextFreeSlot(MenuSlot slot) {
        boolean slotFound = false;

        for (int i = 0; i <= getMaxSlot(); i++) {
            if (!slots.containsKey(i)) {
                slots.put(i, slot);
                slotFound = true;
                break;
            }
        }

        return slotFound;
    }

    /**
     * Helper method to set the outline of the menu to a specific slot.
     * On 1 or 2 row menus, this will fill the entire menu.
     * @param slot The slot to use as an outline
     */
    public void setOutline(MenuSlot slot) {
        for (int i = 0; i <= getMaxSlot(); i++) {
            if (i < 9 || i >= getMaxSlot() - 9 || i % 9 == 0 || i % 9 == 8) {
                setSlot(i, slot);
            }
        }
    }

    /**
     * Fill the first and last rows with the given slot.
     * On 1 or 2 row menus, this will fill the entire menu.
     * @param slot The slot to use as the "banners" of the menu
     */
    public void setBanners(MenuSlot slot) {
        for (int i = 0; i <= getMaxSlot(); i++) {
            if (i < 9 || i >= getMaxSlot() - 9) {
                setSlot(i, slot);
            }
        }
    }
}
