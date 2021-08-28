package wbs.utils.util.entities;

import com.google.common.annotations.Beta;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Beta
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
     * Retrieved from https://github.com/EssentialsX/Essentials/blob/1e0d7fb0a3545d15c3b0cc1d180b47551f98cb22/Essentials/src/main/java/com/earth2me/essentials/craftbukkit/SetExpFix.java
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
     * Retrieved from https://github.com/EssentialsX/Essentials/blob/1e0d7fb0a3545d15c3b0cc1d180b47551f98cb22/Essentials/src/main/java/com/earth2me/essentials/craftbukkit/SetExpFix.java
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
}
