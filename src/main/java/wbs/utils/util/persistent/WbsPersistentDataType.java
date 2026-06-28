package wbs.utils.util.persistent;

import io.papermc.paper.persistence.PersistentDataContainerView;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("unused")
public final class WbsPersistentDataType {
    public static final PersistentLocationType LOCATION = new PersistentLocationType();
    public static final PersistentItemType ITEM = new PersistentItemType();
    public static final PersistentItemByteType ITEM_AS_BYTES = new PersistentItemByteType();
    public static final PersistentKeyType NAMESPACED_KEY = new PersistentKeyType();
    public static final PersistentUUIDType UUID = new PersistentUUIDType();

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

    public static String toString(PersistentDataContainerView container) {
        try (DataInputStream dataInput = new DataInputStream(new ByteArrayInputStream(container.serializeToBytes()))) {
            CompoundTag compound = NbtIo.read(dataInput);
            return compound.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static String toString(PersistentDataContainerView container, NamespacedKey key) {
        try (DataInputStream dataInput = new DataInputStream(new ByteArrayInputStream(container.serializeToBytes()))) {
            CompoundTag compound = NbtIo.read(dataInput);
            Tag tag = compound.get(key.asString());

            return tag != null ? tag.toString() : null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static String toString(PersistentDataContainerView container, String namespace) {
        try (DataInputStream dataInput = new DataInputStream(new ByteArrayInputStream(container.serializeToBytes()))) {
            CompoundTag compound = NbtIo.read(dataInput);

            List<String> tagStrings = new LinkedList<>();
            for (Map.Entry<String, Tag> entry : compound.entrySet()) {
                if (entry.getKey().split(":")[0].startsWith(namespace)) {
                    tagStrings.add(entry.getValue().toString());
                }
            }

            return Strings.join(tagStrings, ';');
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        public @NotNull NamespacedKey fromPrimitive(@NotNull String asString, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            return Objects.requireNonNull(NamespacedKey.fromString(asString));
        }
    }

    public static class PersistentUUIDType implements PersistentDataType<String, UUID> {
        @Override
        public @NotNull Class<String> getPrimitiveType() {
            return String.class;
        }

        @Override
        public @NotNull Class<UUID> getComplexType() {
            return UUID.class;
        }

        @Override
        public @NotNull String toPrimitive(@NotNull UUID uuid, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            return uuid.toString();
        }

        @Override
        public @NotNull UUID fromPrimitive(@NotNull String asString, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            return Objects.requireNonNull(java.util.UUID.fromString(asString));
        }
    }
}
