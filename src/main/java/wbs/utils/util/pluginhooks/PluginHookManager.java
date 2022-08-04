package wbs.utils.util.pluginhooks;

import com.palmergames.bukkit.towny.Towny;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import wbs.utils.WbsUtils;

/**
 * Represents a central point to check if supported plugins are installed
 */
@SuppressWarnings("unused")
public final class PluginHookManager {
    private PluginHookManager() {}

    public static boolean isConfigured = false;

    private static boolean isInstalled(String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    private static boolean griefPreventionInstalled = false;
    public static boolean isGriefPreventionInstalled() {
        griefPreventionInstalled = isInstalled("GriefPrevention");
        return griefPreventionInstalled;
    }

    private static boolean townyInstalled = false;
    private static Towny towny;
    public static boolean isTownyInstalled() {
        townyInstalled = isInstalled("Towny");
        return townyInstalled;
    }
    public static Towny getTowny() {
        return towny;
    }

    private static boolean plotsquaredInstalled = false;
    public static boolean isPlotsquaredInstalled() {
        plotsquaredInstalled = isInstalled("PlotSquared");
        return plotsquaredInstalled;
    }

    private static boolean worldGuardInstalled = false;
    private static WorldGuardPlugin worldGuard;
    public static boolean isWorldGuardInstalled() {
        worldGuardInstalled = isInstalled("WorldGuard");
        return worldGuardInstalled;
    }
    public static WorldGuardPlugin getWorldGuard() {
        return worldGuard;
    }

    private static boolean viaVersionInstalled = false;
    public static boolean isViaVersionInstalled() {
        viaVersionInstalled = isInstalled("ViaVersion");
        return viaVersionInstalled;
    }

    private static boolean vaultInstalled = false;
    public static boolean isVaultInstalled() {
        vaultInstalled = isInstalled("Vault");
        return vaultInstalled;
    }

    /**
     * If not already configured, check all supported plugins to see if they're
     * online. Prints in console which supported plugins were found
     */
    public static void configure() {
        if (isConfigured) {
            return;
        }
        WbsUtils.getInstance().logger.info("Configuring plugin hooks...");

        int hooksFound = 0;

        if (isGriefPreventionInstalled()) {
            WbsUtils.getInstance().logger.info("Successfully hooked into GriefPrevention!");
            hooksFound++;
        }

        if (isTownyInstalled()) {
            towny = (Towny) Bukkit.getServer().getPluginManager().getPlugin("Towny");
            WbsUtils.getInstance().logger.info("Successfully hooked into Towny!");
            hooksFound++;
        }

        if (isPlotsquaredInstalled()) {
            WbsUtils.getInstance().logger.info("Successfully hooked into PlotSquared!");

            hooksFound++;
        }

        if (isWorldGuardInstalled()) {
            WbsUtils.getInstance().logger.info("Successfully hooked into WorldGuard!");
            worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
            hooksFound++;
        }

        if (isViaVersionInstalled()) {
            WbsUtils.getInstance().logger.info("Successfully hooked into ViaVersion!");
            hooksFound++;
        }

        if (isVaultInstalled()) {
            WbsUtils.getInstance().logger.info("Successfully hooked into Vault!");
            hooksFound++;
        }

        isConfigured = true;
        WbsUtils.getInstance().logger.info("Successfully hooked into " + hooksFound + " plugins.");
    }

}
