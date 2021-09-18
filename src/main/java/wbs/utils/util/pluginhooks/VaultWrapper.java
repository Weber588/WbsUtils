package wbs.utils.util.pluginhooks;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import wbs.utils.WbsUtils;

/**
 * A simple wrapper for common Vault methods, to avoid
 * needing to set up Economy in every plugin
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
public final class VaultWrapper {
    private VaultWrapper() {}

    private static Economy eco;
    public static boolean isEcoSetup() {
        return eco != null;
    }
    private static Permission perms;
    public static boolean arePermsSetup() {
        return perms != null;
    }

    public static boolean isConfigured = false;

    public static void configure() {
        if (isConfigured) return;

        if (!PluginHookManager.isVaultInstalled()) return;

        RegisteredServiceProvider<Economy> ecoRSP = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (ecoRSP == null) {
            WbsUtils.getInstance().logger.warning("Economy failed to establish.");
            return;
        } else {
            eco = ecoRSP.getProvider();
        }

        RegisteredServiceProvider<Permission> permsRSP = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (permsRSP == null) {
            WbsUtils.getInstance().logger.warning("Permissions manager failed to establish.");
            return;
        } else {
            perms = permsRSP.getProvider();
        }

        isConfigured = true;
    }

    public static boolean hasMoney(OfflinePlayer player, double amount) {
        if (!isEcoSetup()) return false;
        return eco.has(player, amount);
    }

    public static double getMoney(OfflinePlayer player) {
        if (!isEcoSetup()) return 0;
        return eco.getBalance(player);
    }

    public static boolean giveMoney(OfflinePlayer player, double amount) {
        if (!isEcoSetup()) return false;
        if (amount < 0) {
            return takeMoney(player, Math.abs(amount));
        }

        EconomyResponse response = eco.depositPlayer(player, amount);
        return response.transactionSuccess();
    }

    public static boolean takeMoney(OfflinePlayer player, double amount) {
        if (!isEcoSetup()) return false;
        if (amount < 0) {
            return giveMoney(player, amount);
        }

        EconomyResponse response = eco.withdrawPlayer(player, amount);
        return response.transactionSuccess();
    }

    public static String formatMoney(double amount) {
        if (!isEcoSetup()) return amount + "";

        return eco.format(amount);
    }

    public static String formatMoneyFor(OfflinePlayer player) {
        if (!isEcoSetup()) return "";

        return eco.format(eco.getBalance(player));
    }

    public static boolean givePermission(Player player, String permission) {
        if (!arePermsSetup()) return false;

        return perms.playerAdd(null, player, permission);

    }

    public static boolean removePermission(Player player, String permission) {
        if (!arePermsSetup()) return false;

        return perms.playerRemove(null, player, permission);
    }

    public static String getGroup(Player player) {
        if (!arePermsSetup()) return "";

        return perms.getPrimaryGroup(player);
    }
}
