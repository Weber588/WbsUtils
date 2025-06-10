package wbs.utils.util.persistent;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("unused")
public final class WbsPersistentDataType {
    public static final PersistentLocationType LOCATION = new PersistentLocationType();
    public static final PersistentItemType ITEM = new PersistentItemType();
    public static final PersistentItemByteType ITEM_AS_BYTES = new PersistentItemByteType();
    public static final PersistentKeyType NAMESPACED_KEY = new PersistentKeyType();

    @Nullable
    @Contract("_, _, _, !null -> !null")
    public static <T> T getOrDefault(@NotNull PersistentDataContainerView container, NamespacedKey key, PersistentDataType<?, T> type, T defaultValue) {
        if (container.has(key, type)) {
            T value = container.get(key, type);
            if (value == null) {
                return defaultValue;
            } else {
                return value;
            }
        } else {
            return defaultValue;
        }
    }

    public static <T> void setIfNotDefault(@NotNull PersistentDataContainer container,
                                           @NotNull NamespacedKey key,
                                           @NotNull PersistentDataType<?, T> type,
                                           @Nullable T value,
                                           @Nullable T defaultValue) {
        if (value != defaultValue && value != null) {
            container.set(key, type, value);
        }
    }

    private WbsPersistentDataType() {}

    public static class PersistentKeyType implements PersistentDataType<String, NamespacedKey> {
        @Override
        public @NotNull Class<String> getPrimitiveType() {
            return String.class;
        }

        @Override
        public @NotNull Class<NamespacedKey> getComplexType() {
            return NamespacedKey.class;
        }

        @Override
        public @NotNull String toPrimitive(@NotNull NamespacedKey namespacedKey, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            return namespacedKey.asString();
        }

        @Override
        public @NotNull NamespacedKey fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            return Objects.requireNonNull(NamespacedKey.fromString(s));
        }
    }
}
