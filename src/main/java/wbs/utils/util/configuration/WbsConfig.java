package wbs.utils.util.configuration;

import com.google.common.annotations.Beta;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.exceptions.MissingRequiredKeyException;
import wbs.utils.util.plugin.WbsSettings;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Similar to {@link ConfigurationSection}, but with support
 * for runtime configuration and class-specific options
 * with an annotation system. <p>
 * Also serves as a wrapper for ConfigurationSections.
 */
@Beta
public class WbsConfig {


    public WbsConfig(@NotNull ConfigurationSection wrapped, @NotNull WbsSettings settings) {
        this.wrapped = wrapped;
        this.settings = settings;
    }

    private final WbsSettings settings;

    @NotNull
    private final ConfigurationSection wrapped;

    @NotNull
    public ConfigurationSection getWrapped() {
        return wrapped;
    }

    /**
     * Check if a config is null, and if it is, log it against the current settings
     * @param section The section to check for a key
     * @param field The key
     * @param settings The settings to log errors to
     * @param directory The path to the object being configured
     */
    public static void requireNotNull(@NotNull ConfigurationSection section, String field,
                                      @Nullable WbsSettings settings, @Nullable String directory) throws MissingRequiredKeyException {
        if (section.get(field) == null) {
            if (settings != null) {
                settings.logError("Missing required field: " + field, directory + "/" + field);
            }
            throw new MissingRequiredKeyException();
        }
    }

    // region Wrapped

    @NotNull
    public Set<String> getKeys(boolean b) {
        return wrapped.getKeys(b);
    }

    @NotNull
    public Map<String, Object> getValues(boolean b) {
        return wrapped.getValues(b);
    }

    public boolean contains(@NotNull String s) {
        return wrapped.contains(s);
    }

    public boolean contains(@NotNull String s, boolean b) {
        return wrapped.contains(s, b);
    }

    public boolean isSet(@NotNull String s) {
        return wrapped.isSet(s);
    }

    @Nullable
    public String getCurrentPath() {
        return wrapped.getCurrentPath();
    }

    @NotNull
    public String getName() {
        return wrapped.getName();
    }

    @Nullable
    public Configuration getRoot() {
        return wrapped.getRoot();
    }

    @Nullable
    public ConfigurationSection getParent() {
        return wrapped.getParent();
    }

    @Nullable
    public Object get(@NotNull String s) {
        return wrapped.get(s);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Object get(@NotNull String s, @Nullable Object o) {
        return wrapped.get(s, o);
    }

    public void set(@NotNull String s, @Nullable Object o) {
        wrapped.set(s, o);
    }

    @NotNull
    public ConfigurationSection createSection(@NotNull String s) {
        return wrapped.createSection(s);
    }

    @NotNull
    public ConfigurationSection createSection(@NotNull String s, @NotNull Map<?, ?> map) {
        return wrapped.createSection(s, map);
    }

    @Nullable
    public String getString(@NotNull String s) {
        return wrapped.getString(s);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public String getString(@NotNull String s, @Nullable String s1) {
        return wrapped.getString(s, s1);
    }

    public boolean isString(@NotNull String s) {
        return wrapped.isString(s);
    }

    public int getInt(@NotNull String s) {
        return wrapped.getInt(s);
    }

    public int getInt(@NotNull String s, int i) {
        return wrapped.getInt(s, i);
    }

    public boolean isInt(@NotNull String s) {
        return wrapped.isInt(s);
    }

    public boolean getBoolean(@NotNull String s) {
        return wrapped.getBoolean(s);
    }

    public boolean getBoolean(@NotNull String s, boolean b) {
        return wrapped.getBoolean(s, b);
    }

    public boolean isBoolean(@NotNull String s) {
        return wrapped.isBoolean(s);
    }

    public double getDouble(@NotNull String s) {
        return wrapped.getDouble(s);
    }

    public double getDouble(@NotNull String s, double v) {
        return wrapped.getDouble(s, v);
    }

    public boolean isDouble(@NotNull String s) {
        return wrapped.isDouble(s);
    }

    public long getLong(@NotNull String s) {
        return wrapped.getLong(s);
    }

    public long getLong(@NotNull String s, long l) {
        return wrapped.getLong(s, l);
    }

    public boolean isLong(@NotNull String s) {
        return wrapped.isLong(s);
    }

    @Nullable
    public List<?> getList(@NotNull String s) {
        return wrapped.getList(s);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public List<?> getList(@NotNull String s, @Nullable List<?> list) {
        return wrapped.getList(s, list);
    }

    public boolean isList(@NotNull String s) {
        return wrapped.isList(s);
    }

    @NotNull
    public List<String> getStringList(@NotNull String s) {
        return wrapped.getStringList(s);
    }

    @NotNull
    public List<Integer> getIntegerList(@NotNull String s) {
        return wrapped.getIntegerList(s);
    }

    @NotNull
    public List<Boolean> getBooleanList(@NotNull String s) {
        return wrapped.getBooleanList(s);
    }

    @NotNull
    public List<Double> getDoubleList(@NotNull String s) {
        return wrapped.getDoubleList(s);
    }

    @NotNull
    public List<Float> getFloatList(@NotNull String s) {
        return wrapped.getFloatList(s);
    }

    @NotNull
    public List<Long> getLongList(@NotNull String s) {
        return wrapped.getLongList(s);
    }

    @NotNull
    public List<Byte> getByteList(@NotNull String s) {
        return wrapped.getByteList(s);
    }

    @NotNull
    public List<Character> getCharacterList(@NotNull String s) {
        return wrapped.getCharacterList(s);
    }

    @NotNull
    public List<Short> getShortList(@NotNull String s) {
        return wrapped.getShortList(s);
    }

    @NotNull
    public List<Map<?, ?>> getMapList(@NotNull String s) {
        return wrapped.getMapList(s);
    }

    @Nullable
    public <T> T getObject(@NotNull String s, @NotNull Class<T> aClass) {
        return wrapped.getObject(s, aClass);
    }

    @Nullable
    @Contract("_, _, !null -> !null")
    public <T> T getObject(@NotNull String s, @NotNull Class<T> aClass, @Nullable T t) {
        return wrapped.getObject(s, aClass, t);
    }

    @Nullable
    public <T extends ConfigurationSerializable> T getSerializable(@NotNull String s, @NotNull Class<T> aClass) {
        return wrapped.getSerializable(s, aClass);
    }

    @Nullable
    @Contract("_, _, !null -> !null")
    public <T extends ConfigurationSerializable> T getSerializable(@NotNull String s, @NotNull Class<T> aClass, @Nullable T t) {
        return wrapped.getSerializable(s, aClass, t);
    }

    @Nullable
    public Vector getVector(@NotNull String s) {
        return wrapped.getVector(s);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Vector getVector(@NotNull String s, @Nullable Vector vector) {
        return wrapped.getVector(s, vector);
    }

    public boolean isVector(@NotNull String s) {
        return wrapped.isVector(s);
    }

    @Nullable
    public OfflinePlayer getOfflinePlayer(@NotNull String s) {
        return wrapped.getOfflinePlayer(s);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public OfflinePlayer getOfflinePlayer(@NotNull String s, @Nullable OfflinePlayer offlinePlayer) {
        return wrapped.getOfflinePlayer(s, offlinePlayer);
    }

    public boolean isOfflinePlayer(@NotNull String s) {
        return wrapped.isOfflinePlayer(s);
    }

    @Nullable
    public ItemStack getItemStack(@NotNull String s) {
        return wrapped.getItemStack(s);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public ItemStack getItemStack(@NotNull String s, @Nullable ItemStack itemStack) {
        return wrapped.getItemStack(s, itemStack);
    }

    public boolean isItemStack(@NotNull String s) {
        return wrapped.isItemStack(s);
    }

    @Nullable
    public Color getColor(@NotNull String s) {
        return wrapped.getColor(s);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Color getColor(@NotNull String s, @Nullable Color color) {
        return wrapped.getColor(s, color);
    }

    public boolean isColor(@NotNull String s) {
        return wrapped.isColor(s);
    }

    @Nullable
    public Location getLocation(@NotNull String s) {
        return wrapped.getLocation(s);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Location getLocation(@NotNull String s, @Nullable Location location) {
        return wrapped.getLocation(s, location);
    }

    public boolean isLocation(@NotNull String s) {
        return wrapped.isLocation(s);
    }

    @Nullable
    public ConfigurationSection getConfigurationSection(@NotNull String s) {
        return wrapped.getConfigurationSection(s);
    }

    public boolean isConfigurationSection(@NotNull String s) {
        return wrapped.isConfigurationSection(s);
    }

    @Nullable
    public ConfigurationSection getDefaultSection() {
        return wrapped.getDefaultSection();
    }

    public void addDefault(@NotNull String s, @Nullable Object o) {
        wrapped.addDefault(s, o);
    }
    //endregion
}
