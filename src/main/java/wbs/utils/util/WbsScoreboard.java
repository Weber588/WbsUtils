package wbs.utils.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class WbsScoreboard {

    private final WbsPlugin plugin;
    private Scoreboard scoreboard;
    private Objective obj;

    private final List<Score> scores = new ArrayList<>();

    private String title;
    private final String namespace;

    public WbsScoreboard(WbsPlugin plugin, String namespace, String title) {
        this.plugin = plugin;
        this.namespace = namespace;
        this.title = title;

        registerObjective();
    }

    public String getNamespace() {
        return namespace;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        obj.setDisplayName(plugin.dynamicColourise(title));
    }

    /**
     * Set the line at index i to the given String.
     * @param i The line number to set at
     * @param line The String to show at the line number
     * @throws IndexOutOfBoundsException If the index is below 0, or if
     * the index is greater than {@link #size()}
     */
    public void setLine(int i, String line) throws IndexOutOfBoundsException {
        setLineInternal(i, line);
    }

    /**
     * Add a line to the bottom of the scoreboard
     * @param line The text to show on the board
     */
    public void addLine(String line) {
        setLineInternal(scores.size(), line);
    }

    private void setLineInternal(int i, String line) {
        if (scores.size() > i) {
            scoreboard.resetScores(scores.get(i).getEntry());
        }

        Score score = obj.getScore(plugin.dynamicColourise(line));
        score.setScore(16 - i);

        if (scores.size() > i) {
            scores.set(i, score);
        } else {
            scores.add(score);
        }
    }

    public int size() {
        return scores.size();
    }

    public void clear() {
        scores.clear();

        obj.unregister();
        registerObjective();
    }

    private void registerObjective() {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        assert scoreboardManager != null;
        scoreboard = scoreboardManager.getNewScoreboard();

        obj = scoreboard.registerNewObjective(namespace, "", plugin.dynamicColourise(title));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    /**
     * Show this scoreboard to the given player.
     * @param player The player to show the scoreboard to
     * @return True if the player's scoreboard changed.
     */
    public boolean showToPlayer(Player player) {
        if (player.getScoreboard().equals(scoreboard)) return false;

        player.setScoreboard(scoreboard);
        return true;
    }
}
