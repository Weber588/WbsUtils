package wbs.utils.util.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.List;

@SuppressWarnings("unused")
public class PageSlot<T> extends MenuSlot {
    protected final T pageItem;

    public PageSlot(@NotNull WbsPlugin plugin, T pageItem, @NotNull Material material, @NotNull String displayName, boolean shiny, @Nullable List<String> lore) {
        super(plugin, material, displayName, shiny, lore);
        this.pageItem = pageItem;
    }

    public PageSlot(@NotNull WbsPlugin plugin, T pageItem, @NotNull Material material, @NotNull String displayName, boolean shiny, @Nullable String... lore) {
        super(plugin, material, displayName, shiny, lore);
        this.pageItem = pageItem;
    }

    public PageSlot(@NotNull WbsPlugin plugin, T pageItem, @NotNull Material material, @NotNull String displayName, @Nullable String... lore) {
        super(plugin, material, displayName, lore);
        this.pageItem = pageItem;
    }

    public PageSlot(@NotNull WbsPlugin plugin, T pageItem, @NotNull Material material, @NotNull String displayName, @Nullable List<String> lore) {
        super(plugin, material, displayName, lore);
        this.pageItem = pageItem;
    }

    public PageSlot(@NotNull WbsPlugin plugin, T pageItem, @NotNull ItemStack item) {
        super(plugin, item);
        this.pageItem = pageItem;
    }

    public T getPageItem() {
        return pageItem;
    }
}
