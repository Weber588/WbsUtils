package wbs.utils.util.pluginhooks;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import wbs.utils.WbsUtils;

/**
 * A simple wrapper for common Vault methods, to avoid
 * needing to set up Economy in every plugin
 */
@SuppressWarnings("unused")
public final class VaultWrapper {
    private VaultWrapper() {}

    private static Economy eco;

    public static boolean isConfigured = false;

    public static void configure() {
        if (isConfigured) return;

        if (!PluginHookManager.isVaultInstalled()) return;

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            WbsUtils.getInstance().logger.warning("Economy failed to establish.");
            return;
        }
        eco = rsp.getProvider();
        isConfigured = true;
    }

    public static boolean hasMoney(OfflinePlayer player, double amount) {
        return eco.has(player, amount);
    }

    public static double getMoney(OfflinePlayer player) {
        return eco.getBalance(player);
    }

    public static boolean giveMoney(OfflinePlayer player, double amount) {
        if (amount < 0) {
            return takeMoney(player, Math.abs(amount));
        }

        EconomyResponse response = eco.depositPlayer(player, amount);
        return response.transactionSuccess();
    }

    public static boolean takeMoney(OfflinePlayer player, double amount) {
        if (amount < 0) {
            return giveMoney(player, amount);
        }

        EconomyResponse response = eco.withdrawPlayer(player, amount);
        return response.transactionSuccess();
    }
}
