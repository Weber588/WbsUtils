package wbs.utils.util.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import wbs.utils.util.WbsMath;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings({"unused", "SameParameterValue"})
public abstract class PagedMenu<T> extends WbsMenu {
    private static int getRowsForPage(int size, int maxRows, int maxColumns, int pageNumber) {
        if (size < 0) {
            throw new IllegalArgumentException("Invalid size: " + size);
        }

        if (maxColumns <= 0) {
            throw new IllegalArgumentException("maxColumns must be at least 1: " + size);
        }

        int maxSlots = maxRows * maxColumns;

        int onPage = size - (maxSlots * pageNumber);
        onPage = WbsMath.clamp(0, maxSlots, onPage);

        int rows = ((onPage) - 1) / maxColumns + 1;
        // Add difference between 6 and the maximum, to allow the menu to expand up to 6
        return (6 - maxRows) + rows;
    }

    protected final int lastPageNumber;

    protected final int maxRows;
    protected final int maxColumns;
    protected final int maxSlots;
    protected final int rowStart;
    protected final int page;

    protected final List<PageSlot<? extends T>> pageSlots = new ArrayList<>();

    public PagedMenu(WbsPlugin plugin,
                     Collection<T> toDisplay,
                     String title,
                     String id,
                     int rowStart,
                     int maxRows,
                     int minColumn,
                     int maxColumn,
                     int page) {
        super(plugin, title, getRowsForPage(toDisplay.size(), maxRows, (maxColumn - minColumn), page), id + ":" + page);

        if (maxRows + rowStart > 5) {
            throw new IllegalArgumentException("maxRows must be less than " + (5 - rowStart) + " when starting on row " + rowStart);
        }

        this.page = page;

        this.maxRows = maxRows;
        this.maxColumns = (maxColumn - minColumn) + 1; // +1 to account for 0-indexing; this is a count
        this.maxSlots = maxRows * maxColumns;
        this.rowStart = rowStart;

        lastPageNumber = toDisplay.size() / maxSlots;

        List<T> onPage = new LinkedList<>();

        int index = 0;
        for (T display : toDisplay) {
            if (index < maxSlots * (page + 1) && index >= maxSlots * page) {
                onPage.add(display);
            }
            index++;
        }

        if (toDisplay.size() >= maxSlots * (page + 1)) {
            fillNextPageSlots();
        }

        if (page > 0) {
            fillPrevPageSlots();
        }

        for (T display : onPage) {
            PageSlot<? extends T> slot = getSlot(display);

            pageSlots.add(slot);

            setNextFreeSlot(rowStart, rowStart + (maxRows - 1), minColumn, maxColumn, slot);
        }
    }

    protected void fillNextPageSlots() {
        MenuSlot nextPage = getPageChangeSlot(page + 1, Material.GREEN_STAINED_GLASS, "&r");

        setSlot(rows / 2, 8, nextPage);
        if (rows % 2 == 0) {
            setSlot((rows - 1) / 2, 8, nextPage);
        }
    }

    protected void fillPrevPageSlots() {
        MenuSlot prevPage = getPageChangeSlot(page - 1, Material.RED_STAINED_GLASS, "&r");

        setSlot(rows / 2, 0, prevPage);
        if (rows % 2 == 0) {
            setSlot((rows - 1) / 2, 0, prevPage);
        }
    }

    protected MenuSlot getPageChangeSlot(int page, Material material, String display) {
        MenuSlot slot = new MenuSlot(plugin, material, display);

        slot.setClickActionMenu((menu, click) -> {
            PagedMenu<? extends T> prevPage = getPage(page);
            prevPage.showTo((Player) click.getWhoClicked());
        });

        return slot;
    }

    protected abstract PageSlot<? extends T> getSlot(T display);

    protected abstract PagedMenu<? extends T> getPage(int page);
}
