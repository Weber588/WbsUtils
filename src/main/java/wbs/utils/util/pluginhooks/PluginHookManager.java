package wbs.utils.util.pluginhooks;

import com.palmergames.bukkit.towny.Towny;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import wbs.utils.WbsUtils;
import wbs.utils.util.pluginhooks.hooks.PacketEventsWrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a central point to check if supported plugins are installed
 */
@SuppressWarnings("unused")
public final class PluginHookManager {
    private PluginHookManager() {}

    private static final Map<String, PluginHook> HOOKS = new HashMap<>();
    private static final Map<String, String> HOOK_CLASS_NAMES = new HashMap<>();

    static {
        HOOK_CLASS_NAMES.put(PacketEventsWrapper.PLUGIN_NAME, "wbs.utils.util.pluginhooks.hooks.PacketEventsHook");
    }

    @Nullable
    public static <T extends PluginHookWrapper> T getHook(Class<T> wrapperClass, String pluginName) {
        PluginHook hook = getHook(pluginName);

        if (hook == null) {
            return null;
        }

        //noinspection unchecked
        return (T) hook;
    }

    @Nullable
    public static PluginHook getHook(String pluginName) {
        pluginName = pluginName.toLowerCase();

        if (isInstalled(pluginName)) {
            PluginHook hook = HOOKS.get(pluginName);

            if (hook == null) {
                String hookClassName = HOOK_CLASS_NAMES.get(pluginName);
                if (hookClassName == null) {
                    return null;
                }

                try {
                    Class<?> checkClass = Class.forName(hookClassName);

                    if (!PluginHook.class.isAssignableFrom(checkClass)) {
                        throw new RuntimeException("Hook class must extend PluginHook.");
                    }

                    Class<? extends PluginHook> hookClass = (Class<? extends PluginHook>) checkClass;

                    Constructor<? extends PluginHook> constructor = hookClass.getConstructor();

                    hook = constructor.newInstance();
                    HOOKS.put(pluginName, hook);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Invalid hook class " + hookClassName + " for plugin " + pluginName + ".", e);
                } catch (NoSuchMethodException | IllegalAccessException e) {
                    throw new RuntimeException("Hook class " + hookClassName + " for plugin " + pluginName + " must have a public no-args constructor.", e);
                } catch (InvocationTargetException | InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }

            return hook;
        }
        return null;
    }

    public static boolean isConfigured = false;

    static boolean isInstalled(String pluginName) {
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

    private static boolean packetEventsInstalled = false;
    public static boolean isPacketEventsInstalled() {
        packetEventsInstalled = isInstalled("packetevents");
        return packetEventsInstalled;
    }

    /**
     * If not already configured, check all supported plugins to see if they're
     * online. Prints in console which supported plugins were found
     */
    public static void configure() {
        if (isConfigured) {
            return;
        }
        WbsUtils.getInstance().getLogger().info("Configuring plugin hooks...");

        int hooksFound = 0;

        if (isGriefPreventionInstalled()) {
            WbsUtils.getInstance().getLogger().info("Successfully hooked into GriefPrevention!");
            hooksFound++;
        }

        if (isTownyInstalled()) {
            towny = (Towny) Bukkit.getServer().getPluginManager().getPlugin("Towny");
            WbsUtils.getInstance().getLogger().info("Successfully hooked into Towny!");
            hooksFound++;
        }

        if (isPlotsquaredInstalled()) {
            WbsUtils.getInstance().getLogger().info("Successfully hooked into PlotSquared!");

            hooksFound++;
        }

        if (isWorldGuardInstalled()) {
            WbsUtils.getInstance().getLogger().info("Successfully hooked into WorldGuard!");
            worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
            hooksFound++;
        }

        if (isViaVersionInstalled()) {
            WbsUtils.getInstance().getLogger().info("Successfully hooked into ViaVersion!");
            hooksFound++;
        }

        if (isVaultInstalled()) {
            WbsUtils.getInstance().getLogger().info("Successfully hooked into Vault!");
            hooksFound++;
        }

        if (isPacketEventsInstalled()) {
            WbsUtils.getInstance().getLogger().info("Successfully hooked into PacketEvents!");
            hooksFound++;
        }

        isConfigured = true;
        WbsUtils.getInstance().getLogger().info("Successfully hooked into " + hooksFound + " plugins.");
    }

}
