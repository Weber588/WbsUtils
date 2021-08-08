package wbs.utils.util.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.pluginhooks.PlaceholderAPIWrapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class MenuSlot {

    @NotNull
    private ItemStack item;

    @Nullable
    private Consumer<InventoryClickEvent> clickAction;
    private boolean closeOnClick;
    private boolean allowItemTaking;

    private boolean fillPlaceholders;

    private final WbsPlugin plugin;

    public MenuSlot(@NotNull WbsPlugin plugin, @NotNull ItemStack item) {
        this.plugin = plugin;
        this.item = item;
        this.item = getFormattedItem(null);

        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

        String displayName = meta.getDisplayName();
        displayName = plugin.dynamicColourise(displayName);
        meta.setDisplayName(displayName);

        if (meta.hasLore()) {
            List<String> newLore = new LinkedList<>();
            for (String loreLine : Objects.requireNonNull(meta.getLore())) {
                newLore.add(plugin.dynamicColourise(loreLine));
            }
            meta.setLore(newLore);
        }
    }

    /**
     * Get a clone of the item, with placeholders filled and colours
     * created
     * @param player The player to fill in placeholders for. When null,
     *               colours will be filled but no placeholders will be.
     * @return A cloned item formatted for display to a player
     */
    public ItemStack getFormattedItem(@Nullable Player player) {
        ItemStack formattedItem = item.clone();

        ItemMeta meta = Objects.requireNonNull(formattedItem.getItemMeta());

        String displayName = meta.getDisplayName();
        displayName = formatString(player, displayName);
        meta.setDisplayName(displayName);

        if (meta.hasLore()) {
            List<String> newLore = new LinkedList<>();
            for (String loreLine : Objects.requireNonNull(meta.getLore())) {
                newLore.add(formatString(player, loreLine));
            }
            meta.setLore(newLore);
        }

        formattedItem.setItemMeta(meta);

        return formattedItem;
    }

    private String formatString(@Nullable Player player, String string) {
        if (fillPlaceholders && player != null) {
            string = PlaceholderAPIWrapper.setPlaceholders(player, string);
        }
        string = plugin.dynamicColourise(string);

        return string;
    }

    @NotNull
    public ItemStack getItem() {
        return item;
    }

    public @Nullable Consumer<InventoryClickEvent> getClickAction() {
        return clickAction;
    }
    public void setClickAction(@Nullable Consumer<InventoryClickEvent> clickAction) {
        this.clickAction = clickAction;
    }

    public boolean closeOnClick() {
        return closeOnClick;
    }
    public void setCloseOnClick(boolean closeOnClick) {
        this.closeOnClick = closeOnClick;
    }

    public boolean allowItemTaking() {
        return allowItemTaking;
    }
    public void setAllowItemTaking(boolean allowItemTaking) {
        this.allowItemTaking = allowItemTaking;
    }

    public boolean fillPlaceholders() {
        return fillPlaceholders;
    }
    public void setFillPlaceholders(boolean fillPlaceholders) {
        this.fillPlaceholders = fillPlaceholders;
    }
}
