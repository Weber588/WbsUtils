package wbs.utils.util.pluginhooks;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import wbs.utils.WbsUtils;

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

    public static double getMoney(OfflinePlayer player, double amount) {
        return eco.getBalance(player);
    }

    public static EconomyResponse giveMoney(OfflinePlayer player, double amount) {
        if (amount < 0) {
            return takeMoney(player, Math.abs(amount));
        }


        return eco.depositPlayer(player, amount);
    }

    public static EconomyResponse takeMoney(OfflinePlayer player, double amount) {
        if (amount < 0) {
            return giveMoney(player, amount);
        }


        return eco.withdrawPlayer(player, amount);
    }
}
