package wbs.utils.util.persistent;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public class KeyedPersistentDataType<T extends Keyed> implements WbsPersistentDataType<String, T> {
    private final Class<T> tClass;
    private final Function<NamespacedKey, T> fromKey;

    public KeyedPersistentDataType(Class<T> tClass, Function<NamespacedKey, T> fromKey) {
        this.tClass = tClass;
        this.fromKey = fromKey;
    }

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<T> getComplexType() {
        return tClass;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull T keyedValue, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return keyedValue.getKey().asString();
    }

    @Override
    public @NotNull T fromPrimitive(@NotNull String keyString, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return fromKey.apply(Objects.requireNonNull(NamespacedKey.fromString(keyString), "Invalid NamespacedKey \"%s\"".formatted(keyString)));
    }
}
