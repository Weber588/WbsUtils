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
import org.bukkit.plugin.PluginManager;
import wbs.utils.util.WbsMath;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.string.WbsStrings;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class WbsMenu implements Listener {

    protected final WbsPlugin plugin;
    protected final int rows;

    private final String titleString;

    private final Map<Integer, MenuSlot> slots = new HashMap<>();

    private boolean unregisterOnClose = false;

    /**
     * @param plugin The plugin to register this menu to
     * @param title The title of the menu to show to players
     * @param rows How many rows to create, between 1 and 6 inclusive
     * @param id A unique identifier for identifying inventory clicks
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

    /**
     * Update the given slot for all players currently looking
     * at this menu.
     * @param slot The slot to update.
     */
    public void update(int slot) {
        MenuSlot menuSlot = slots.get(slot);
        if (menuSlot == null) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (hasMenuOpen(player)) {
                player.getOpenInventory().setItem(slot, menuSlot.getFormattedItem(player));
            }
        }
    }

    public void update(int row, int column) {
        update(getSlotNumber(row, column));
    }

    /**
     * Update all slots for all players currently viewing
     * this menu
     */
    public void update() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (hasMenuOpen(player)) {
                for (int i = 0; i < getMaxSlot(); i++) {
                    MenuSlot menuSlot = slots.get(i);
                    if (menuSlot == null) {
                        continue;
                    }

                    player.getOpenInventory().setItem(i, menuSlot.getFormattedItem(player));
                }
            }
        }
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

    public void setSlot(int row, int column, MenuSlot slot) {
        setSlot(row * 9 + column, slot);
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
            if (slot.getClickActionMenu() != null)
                slot.getClickActionMenu().accept(this, event);
        }
    }

    // ============================== //
    //          Helper methods        //
    // ============================== //

    /**
     * Get the number of the last slot in this menu
     * @return The maximum slot this menu may contain
     */
    protected int getMaxSlot() {
        return rows * 9 - 1;
    }

    /**
     * Get the slot number for a given row and column.
     * @param row The row
     * @param column The column
     * @return The slot number that represents the slot
     * at the given row and column
     */
    protected int getSlotNumber(int row, int column) {
        return row * 9 + column;
    }

    protected int getRow(int rawSlot) {
        return rawSlot / 9;
    }

    protected int getColumn(int rawSlot) {
        return rawSlot % 9;
    }

    /**
     * Add a slot in the next available slot in the menu if any are available
     * @param slot The slot to add at the first unassigned slot
     * @return True if the slot was added, false if no free slots were found.
     */
    public boolean setNextFreeSlot(MenuSlot slot) {
        return setNextFreeSlot(0, slot);
    }

    /**
     * Add a slot in the next available slot in the menu if any are available
     * @param startingAt The slot to start checking from.
     * @param slot The slot to add at the first unassigned slot
     * @return True if the slot was added, false if no free slots were found.
     */
    public boolean setNextFreeSlot(int startingAt, MenuSlot slot) {
        for (int i = startingAt; i <= getMaxSlot(); i++) {
            if (!slots.containsKey(i)) {
                slots.put(i, slot);
                return true;
            }
        }

        return false;
    }

    /**
     * Set the next free slot in a rectangular region bound by
     * a minimum and maximum row and column.
     * @param minRow The minimum row
     * @param maxRow The maximum row
     * @param minColumn The minimum column
     * @param maxColumn The maximum column
     * @param slot The slot to add
     * @return True if the slot was added, false if no free slots were found.
     */
    public boolean setNextFreeSlot(int minRow, int maxRow, int minColumn, int maxColumn, MenuSlot slot) {
        minRow = WbsMath.clamp(0, rows - 1, minRow);
        maxRow = WbsMath.clamp(0, rows - 1, maxRow);
        minColumn = WbsMath.clamp(0, 8, minColumn);
        maxColumn = WbsMath.clamp(0, 8, maxColumn);

        for (int row = minRow; row <= maxRow; row++) {
            for (int column = minColumn; column <= maxColumn; column++) {
                int slotNum = getSlotNumber(row, column);
                if (!slots.containsKey(slotNum)) {
                    slots.put(slotNum, slot);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Set the next free slot in a rectangular region defined by two corners,
     * where the first slot is the upper left corner, and the second slot is the
     * lower right corner.
     * @param minSlotCorner The minimum corner, in the upper left of the region
     * @param maxSlotCorner The maximum corner, in the lower right of the region
     * @param slot The slot to add
     * @return True if the slot was added, false if no free slots were found.
     */
    public boolean setNextFreeSlot(int minSlotCorner, int maxSlotCorner, MenuSlot slot) {
        setNextFreeSlot(getRow(minSlotCorner), getRow(maxSlotCorner), getColumn(minSlotCorner), getColumn(maxSlotCorner), slot);

        return false;
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

    /**
     * Fill a given row with the given slot.
     * @param row The row to fill
     * @param slot The slot to fill the given row with
     */
    public void setRow(int row, MenuSlot slot) {
        if (row >= rows) {
            throw new IndexOutOfBoundsException("Row must be between 0 (inclusive) and rows (exclusive)");
        }
        for (int i = 0; i < 9; i++) {
            setSlot(i + row * 9, slot);
        }
    }

    /**
     * Fill a given column with the given slot.
     * @param column The column to fill
     * @param slot The slot to fill the given column with
     */
    public void setColumn(int column, MenuSlot slot) {
        if (column < 0 || column >= 9) {
            throw new IndexOutOfBoundsException("Column must be between 0 and 8 inclusive");
        }
        for (int i = 0; i < rows; i++) {
            setSlot(i * 9 + column, slot);
        }
    }
}
