package wbs.utils.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;
import wbs.utils.WbsUtils;
import wbs.utils.util.pluginhooks.PluginHookManager;

public final class VersionUtil {
    private VersionUtil() {}

    private static double serverVersion = -1;

    /**
     * Get the server version as a double.
     * @return A double representing the server version, where the whole part is the major
     * version, and the decimal part is the minor release. 1.16.4 is represented as 16.4.
     */
    public static double getVersion() {
        if (serverVersion == -1) {
            String versionString = Bukkit.getBukkitVersion();
            versionString = versionString.split("-")[0].substring(2);
            try {
                serverVersion = Double.parseDouble(versionString);
            } catch (NumberFormatException e) {
                WbsUtils.getInstance().logger.warning("The server version failed to parse: " + versionString);
                WbsUtils.getInstance().logger.warning("This may limit functionality to API Version 1.14.");
                serverVersion = -2; // Only display this message once then stop trying.
            }
        }

        return serverVersion;
    }

    private static boolean isInstalled(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName) != null;
    }

    public static String getReadableVersion(Player player) {
        if (!PluginHookManager.isViaVersionInstalled()) return "1." + getVersion();

        ViaAPI api = Via.getAPI(); // Get the API
        int protocolVer = api.getPlayerVersion(player); // Get the protocol version

        ProtocolVersion versionForPlayer = ProtocolVersion.getProtocol(protocolVer);

        return versionForPlayer.getName();
    }

    public static double getVersion(Player player) {
        String versionName = getReadableVersion(player);

        double versionNum;
        try {
            versionNum = Double.parseDouble(versionName.substring(2));
        } catch (NumberFormatException e) {
            return getVersion();
        }
        return versionNum;
    }


}
