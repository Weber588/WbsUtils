package wbs.utils.util.pluginhooks;

import com.palmergames.bukkit.towny.Towny;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import wbs.utils.WbsUtils;

@SuppressWarnings("unused")
public final class PluginHookManager {
    private PluginHookManager() {}

    public static boolean isConfigured = false;

    private static boolean isInstalled(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName) != null;
    }

    private static boolean griefPreventionInstalled = false;
    public static boolean isGriefPreventionInstalled() {
        return griefPreventionInstalled;
    }

    private static boolean townyInstalled = false;
    private static Towny towny;
    public static boolean isTownyInstalled() {
        return townyInstalled;
    }
    public static Towny getTowny() {
        return towny;
    }

    private static boolean plotsquaredInstalled = false;
    public static boolean isPlotsquaredInstalled() {
        return plotsquaredInstalled;
    }

    private static boolean worldGuardInstalled = false;
    private static WorldGuardPlugin worldGuard;
    public static boolean isWorldGuardInstalled() {
        return worldGuardInstalled;
    }
    public static WorldGuardPlugin getWorldGuard() {
        return worldGuard;
    }

    private static boolean viaVersionInstalled = false;
    public static boolean isViaVersionInstalled() {
        return viaVersionInstalled;
    }

    public static void configure() {
        if (isConfigured) {
            return;
        }
        WbsUtils.getInstance().logger.info("Configuring plugin hooks...");

        int hooksFound = 0;

        griefPreventionInstalled = isInstalled("GriefPrevention");
        if (griefPreventionInstalled) {
            WbsUtils.getInstance().logger.info("Successfully hooked into GriefPrevention!");
            hooksFound++;
        }

        townyInstalled = isInstalled("Towny");
        if (townyInstalled) {
            towny = (Towny) Bukkit.getServer().getPluginManager().getPlugin("Towny");
            WbsUtils.getInstance().logger.info("Successfully hooked into Towny!");
            hooksFound++;
        }

        plotsquaredInstalled = isInstalled("PlotSquared");
        if (plotsquaredInstalled) {
            WbsUtils.getInstance().logger.info("Successfully hooked into PlotSquared!");
            if (Bukkit.getPluginManager().getPlugin("PlotSquared").getDescription().getVersion().startsWith("6")) {
                WbsUtils.getInstance().logger.warning("PlotSquared 6 is not yet supported!");
            }
            hooksFound++;
        }

        worldGuardInstalled = isInstalled("WorldGuard");
        if (worldGuardInstalled) {
            WbsUtils.getInstance().logger.info("Successfully hooked into WorldGuard!");
            worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
            hooksFound++;
        }

        viaVersionInstalled = isInstalled("ViaVersion");
        if (viaVersionInstalled) {
            WbsUtils.getInstance().logger.info("Successfully hooked into ViaVersion!");
            hooksFound++;
        }

        isConfigured = true;
        WbsUtils.getInstance().logger.info("Successfully hooked into " + hooksFound + " plugins.");
    }

}
