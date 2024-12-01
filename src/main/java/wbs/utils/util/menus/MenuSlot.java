package wbs.utils.util.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.pluginhooks.PlaceholderAPIWrapper;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class MenuSlot {

    @NotNull
    protected ItemStack item;

    @Nullable
    protected Consumer<InventoryClickEvent> clickAction;
    @Nullable
    protected BiConsumer<WbsMenu, InventoryClickEvent> clickActionMenu;
    protected boolean closeOnClick;
    protected boolean allowItemTaking;

    protected boolean fillPlaceholders;

    protected final WbsPlugin plugin;

    public MenuSlot(@NotNull WbsPlugin plugin,
                    @NotNull Material material,
                    @NotNull String displayName,
                    boolean shiny,
                    @Nullable List<String> lore) {
        this.plugin = plugin;

        item = new ItemStack(material);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
        if (meta == null) {
            throw new IllegalArgumentException("Material must be able to store meta");
        }
        meta.setDisplayName(displayName);
        meta.setLore(lore);

        meta.addItemFlags(
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_ATTRIBUTES);

        if (shiny) {
            meta.addEnchant(Enchantment.LOYALTY, 0, true);
        }

        item.setItemMeta(meta);

        this.item = getFormattedItem(null);
    }

    public MenuSlot(@NotNull WbsPlugin plugin,
                    @NotNull Material material,
                    @NotNull String displayName,
                    boolean shiny,
                    @Nullable String ... lore) {
        this(plugin, material, displayName, shiny, Arrays.asList(lore));
    }

    public MenuSlot(@NotNull WbsPlugin plugin,
                    @NotNull Material material,
                    @NotNull String displayName,
                    @Nullable String ... lore) {
        this(plugin, material, displayName, false, lore);
    }

    public MenuSlot(@NotNull WbsPlugin plugin,
                    @NotNull Material material,
                    @NotNull String displayName,
                    @Nullable List<String> lore) {
        this(plugin, material, displayName, false, lore);
    }

    public MenuSlot(@NotNull WbsPlugin plugin, @NotNull ItemStack item) {
        this.plugin = plugin;
        this.item = item;
        this.item = getFormattedItem(null);
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

    protected String formatString(@Nullable Player player, String string) {
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

    public @Nullable BiConsumer<WbsMenu, InventoryClickEvent> getClickActionMenu() {
        return clickActionMenu;
    }
    public void setClickActionMenu(@Nullable BiConsumer<WbsMenu, InventoryClickEvent> clickActionMenu) {
        this.clickActionMenu = clickActionMenu;
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
