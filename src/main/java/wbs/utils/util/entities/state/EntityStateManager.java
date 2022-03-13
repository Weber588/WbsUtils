package wbs.utils.util.entities.state;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import wbs.utils.WbsUtils;
import wbs.utils.util.entities.state.tracker.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A registry of classes that implement both {@link EntityState} and {@link ConfigurationSerializable},
 * so they can be referenced during deserialization of {@link SavedEntityState}s.
 */
public final class EntityStateManager {
    private EntityStateManager() {}

    public static void registerNativeDeserializers() {
        register(AllowFlightState.class, AllowFlightState::deserialize);
        register(FallDistanceState.class, FallDistanceState::deserialize);
        register(FireTicksState.class, FireTicksState::deserialize);
        register(FlyingState.class, FlyingState::deserialize);
        register(GameModeState.class, GameModeState::deserialize);
        register(HealthState.class, HealthState::deserialize);
        register(HungerState.class, HungerState::deserialize);
        register(InvulnerableState.class, InvulnerableState::deserialize);
        register(LocationState.class, LocationState::deserialize);
        register(PotionEffectsState.class, PotionEffectsState::deserialize);
        register(SaturationState.class, SaturationState::deserialize);
        register(VelocityState.class, VelocityState::deserialize);
        register(XPState.class, XPState::deserialize);
    }

    /**
     * Maps the {@link #getEscapedClassName(Class)} name to the registered class.
     * Stored to allow reverse lookups when a deserializing object is referencing an escaped string,
     * rather than looking up the class directly in the class path.
     */
    private static final Map<String, Class<? extends EntityState<?>>> registeredClasses = new HashMap<>();

    private static final Map<Class<? extends EntityState<?>>, Function<Map<String, Object>, ? extends EntityState<?>>> deserializers = new HashMap<>();

    /**
     * Register an {@link EntityState} that can be deserialized, with the function provided.<br/>
     * If a class is registered before {@link JavaPlugin#onEnable()} (such as in {@link JavaPlugin#onLoad()})
     * in the WbsUtils main instance, it will be automatically registered in {@link ConfigurationSerialization}.
     * Otherwise, it will need to be registered manually.
     * @param clazz The EntityState that class that will be produced by the provided function
     * @param function The deserializer that accepts a map of String keys to objects and returns the
     *                 {@link EntityState} that matches the given clazz.
     */
    public static <T extends EntityState<?>> void register(Class<T> clazz,
                                Function<Map<String, Object>, T> function) {
        registeredClasses.put(getEscapedClassName(clazz), clazz);
        deserializers.put(clazz, function);
    }

    @SuppressWarnings("unchecked")
    public static void registerConfigurableClasses() {
        for (Class<?> entityStateClass : registeredClasses.values()) {
            if (ConfigurationSerializable.class.isAssignableFrom(entityStateClass)) {
                ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) entityStateClass);
            }
        }
    }

    @Nullable
    public static EntityState<?> deserialize(String escapedClassName, Map<String, Object> map) {
        Class<? extends EntityState<?>> clazz = registeredClasses.get(escapedClassName);

        if (clazz == null) return null;

        Function<Map<String, Object>, ? extends EntityState<?>> deserializer =
                deserializers.get(clazz);

        if (deserializer == null) {
            return null;
        }

        return deserializer.apply(map);
    }

    static String getEscapedClassName(Class<?> obj) {
        return obj.getCanonicalName().replace(".", "|");
    }

    static String unescapeClassName(String escapedClassName) {
        return escapedClassName.replace("|", ".");
    }
}
