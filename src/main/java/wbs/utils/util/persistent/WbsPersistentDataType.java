package wbs.utils.util.persistent;

import io.papermc.paper.persistence.PersistentDataContainerView;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.ListPersistentDataType;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("unused")
public interface WbsPersistentDataType<P, T> extends PersistentDataType<P, T> {
    PersistentLocationType LOCATION = new PersistentLocationType();
    PersistentItemType ITEM = new PersistentItemType();
    PersistentItemByteType ITEM_AS_BYTES = new PersistentItemByteType();
    PersistentKeyType NAMESPACED_KEY = new PersistentKeyType();
    PersistentUUIDType UUID = new PersistentUUIDType();

    static <T> void setIfNotDefault(@NotNull PersistentDataContainer container,
                                    @NotNull NamespacedKey key,
                                    @NotNull PersistentDataType<?, T> type,
                                    @Nullable T value,
                                    @Nullable T defaultValue) {
        if (value != defaultValue && value != null) {
            container.set(key, type, value);
        }
    }

    static String toString(PersistentDataContainerView container) {
        try (DataInputStream dataInput = new DataInputStream(new ByteArrayInputStream(container.serializeToBytes()))) {
            CompoundTag compound = NbtIo.read(dataInput);
            return compound.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    static String toString(PersistentDataContainerView container, NamespacedKey key) {
        try (DataInputStream dataInput = new DataInputStream(new ByteArrayInputStream(container.serializeToBytes()))) {
            CompoundTag compound = NbtIo.read(dataInput);
            Tag tag = compound.get(key.asString());

            return tag != null ? tag.toString() : null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    static String toString(PersistentDataContainerView container, String namespace) {
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

    default ListPersistentDataType<P, T> asList() {
        return PersistentDataType.LIST.listTypeFrom(this);
    }

    class PersistentKeyType implements WbsPersistentDataType<String, NamespacedKey> {
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
            return Objects.requireNonNull(NamespacedKey.fromString(asString), "Invalid NamespacedKey \"%s\"".formatted(asString));
        }
    }

    class PersistentUUIDType implements WbsPersistentDataType<String, UUID> {
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
