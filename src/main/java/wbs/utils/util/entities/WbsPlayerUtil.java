package wbs.utils.util.entities;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.UseRemainder;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.WbsMath;

@SuppressWarnings("unused")
public final class WbsPlayerUtil {
    private WbsPlayerUtil() {}


    /**
     * Returns the location at which the head pivots.
     * Useful for getting relative coordinates to the
     * head using facing vector.
     * @param player The player whose neck pos to get
     * @return The neck position
     */
    public static Location getNeckPosition(Player player) {
        return player.getEyeLocation().subtract(0, 0.2, 0);
    }


    /**
     * Set the total experience of a player safely and based on points
     * @param player The player to set the exp for
     * @param points The new experience
     */
    public static void setExp(Player player, int points) {
        float level = toLevels(points);

        player.setExp(level - (int) level);
        player.setLevel((int) level);
    }

    /**
     * Gets the amount of levels for the given amount of
     * points, where the whole number part is the level,
     * and the decimal is the progress to the next level.
     * @param points The points to find the level for
     * @return The level
     */
    public static float toLevels(int points) {
        float level = 0;
        while (points >= 0) {
            int expAtLevel = getExpAtLevel((int) level);

            if (points - expAtLevel >= 0) {
                level++;
            } else {
                level += points / (float) expAtLevel;
            }

            points -= expAtLevel;
        }

        return level;
    }

    public static float getProgressForExp(int points) {
        float level = toLevels(points);

        return level - (int) level;
    }

    /**
     * Retrieved from <a href="https://github.com/EssentialsX/Essentials/blob/1e0d7fb0a3545d15c3b0cc1d180b47551f98cb22/Essentials/src/main/java/com/earth2me/essentials/craftbukkit/SetExpFix.java">...</a>
     * on 25-07-21
     * @return Gets the actual experience of the player
     */
    public static int getExp(Player player) {
        int currentLevel = player.getLevel();
        int exp = Math.round(getExpAtLevel(currentLevel) * player.getExp());

        while (currentLevel > 0) {
            currentLevel--;
            exp += getExpAtLevel(currentLevel);
        }
        if (exp < 0) {
            exp = Integer.MAX_VALUE;
        }
        return exp;
    }

    /**
     * Calculation for experience internally.
     * Retrieved from <a href="https://github.com/EssentialsX/Essentials/blob/1e0d7fb0a3545d15c3b0cc1d180b47551f98cb22/Essentials/src/main/java/com/earth2me/essentials/craftbukkit/SetExpFix.java">...</a>
     * on 25-07-21
     */
    public static int getExpAtLevel(int level) {
        if (level <= 15) {
            return (2 * level) + 7;
        }
        if (level <= 30) {
            return (5 * level) - 38;
        }
        return (9 * level) - 158;
    }

    @NotNull
    public static PlayerConsumeItemResult consume(Player player, ItemStack item) {
        return consume(player, item, true);
    }

    @NotNull
    @SuppressWarnings("UnstableApiUsage")
    public static PlayerConsumeItemResult consume(Player player, ItemStack item, boolean throwEvent) {
        if (!item.getItemMeta().hasFood()) {
            return new PlayerConsumeItemResult(false, null);
        }

        FoodComponent food = item.getItemMeta().getFood();

        int foodLevel = player.getFoodLevel();
        if (foodLevel >= 20) { // 20 = player's max food level. If this changes, will probably need to change to "player.getMaxFoodLevel".
            if (!food.canAlwaysEat()) {
                return new PlayerConsumeItemResult(false, null);
            }
        }

        if (throwEvent) {
            PlayerItemConsumeEvent event = new PlayerItemConsumeEvent(player, item, EquipmentSlot.HAND);

            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return new PlayerConsumeItemResult(false, null);
            }

            if (!event.getItem().equals(item)) {
                // Force eat the new item, don't recheck event
                return consume(player, event.getItem(), false);
            }
        }

        int nutrition = food.getNutrition();
        player.setFoodLevel(Math.min(20, foodLevel + nutrition));

        float saturation = food.getSaturation();
        player.setSaturation(Math.min(20, player.getSaturation() + saturation));

        Consumable consumable = item.getData(DataComponentTypes.CONSUMABLE);
        if (consumable != null) {
            for (ConsumeEffect consumeEffect : consumable.consumeEffects()) {
                if (consumeEffect instanceof ConsumeEffect.ApplyStatusEffects statusEffects) {
                    if (WbsMath.chance(statusEffects.probability() * 100)) {
                        statusEffects.effects().forEach(player::addPotionEffect);
                    }
                } else if (consumeEffect instanceof ConsumeEffect.ClearAllStatusEffects clearEffect) {
                    player.clearActivePotionEffects();
                } else if (consumeEffect instanceof ConsumeEffect.PlaySound playSound) {
                    player.getWorld().playSound(player.getEyeLocation(), playSound.sound().asString(), SoundCategory.PLAYERS, 1, 1);
                } else if (consumeEffect instanceof ConsumeEffect.RemoveStatusEffects removeStatusEffects) {
                    removeStatusEffects.removeEffects().resolve(Registry.EFFECT).forEach(player::removePotionEffect);
                } else if (consumeEffect instanceof ConsumeEffect.TeleportRandomly teleportRandomly) {
                    Block found = WbsEntityUtil.getSafeLocation(player, player.getLocation().add(WbsMath.randomVector(teleportRandomly.diameter())), teleportRandomly.diameter());
                    if (found != null) {
                        player.teleport(found.getLocation());
                    }
                }
            }
        }

        UseRemainder useRemainder = item.getData(DataComponentTypes.USE_REMAINDER);
        ItemStack remainingItem = null;
        if (useRemainder != null) {
            remainingItem = useRemainder.transformInto();
        }

        return new PlayerConsumeItemResult(true, remainingItem);
    }

    public record PlayerConsumeItemResult(boolean success, @Nullable ItemStack remainingItem) {}
}
