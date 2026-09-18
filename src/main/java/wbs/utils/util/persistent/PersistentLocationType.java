package wbs.utils.util.persistent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import wbs.utils.WbsUtils;

public class PersistentLocationType implements WbsPersistentDataType<PersistentDataContainer, Location> {
    private static final NamespacedKey X_KEY = new NamespacedKey(WbsUtils.getInstance(), "x");
    private static final NamespacedKey Y_KEY = new NamespacedKey(WbsUtils.getInstance(), "y");
    private static final NamespacedKey Z_KEY = new NamespacedKey(WbsUtils.getInstance(), "z");
    private static final NamespacedKey PITCH_KEY = new NamespacedKey(WbsUtils.getInstance(), "pitch");
    private static final NamespacedKey YAW_KEY = new NamespacedKey(WbsUtils.getInstance(), "yaw");
    private static final NamespacedKey WORLD_KEY = new NamespacedKey(WbsUtils.getInstance(), "world");

    @NotNull
    @Override
    public Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @NotNull
    @Override
    public Class<Location> getComplexType() {
        return Location.class;
    }

    @NotNull
    @Override
    public PersistentDataContainer toPrimitive(@NotNull Location location, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        PersistentDataContainer container = persistentDataAdapterContext.newPersistentDataContainer();

        WbsPersistentDataType.setIfNotDefault(container, X_KEY, PersistentDataType.DOUBLE, location.getX(), 0d);
        WbsPersistentDataType.setIfNotDefault(container, Y_KEY, PersistentDataType.DOUBLE, location.getY(), 0d);
        WbsPersistentDataType.setIfNotDefault(container, Z_KEY, PersistentDataType.DOUBLE, location.getZ(), 0d);
        WbsPersistentDataType.setIfNotDefault(container, PITCH_KEY, PersistentDataType.FLOAT, location.getPitch(), 0f);
        WbsPersistentDataType.setIfNotDefault(container, YAW_KEY, PersistentDataType.FLOAT, location.getYaw(), 0f);

        World world = location.getWorld();
        if (world != null) {
            container.set(WORLD_KEY, PersistentDataType.STRING, world.getUID().toString());
        }

        return container;
    }

    @NotNull
    @Override
    public Location fromPrimitive(@NotNull PersistentDataContainer container, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        double x = container.getOrDefault(X_KEY, PersistentDataType.DOUBLE, 0d);
        double y = container.getOrDefault(Y_KEY, PersistentDataType.DOUBLE, 0d);
        double z = container.getOrDefault(Z_KEY, PersistentDataType.DOUBLE, 0d);
        float pitch = container.getOrDefault(PITCH_KEY, PersistentDataType.FLOAT, 0f);
        float yaw = container.getOrDefault(YAW_KEY, PersistentDataType.FLOAT, 0f);

        World world = null;
        String worldUUID = container.get(WORLD_KEY, PersistentDataType.STRING);
        if (worldUUID != null) {
            world = Bukkit.getWorld(java.util.UUID.fromString(worldUUID));
        }

        return new Location(world, x, y, z, yaw, pitch);
    }
}

