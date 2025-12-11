package wbs.utils.util;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.exceptions.InvalidConfigurationException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class WbsRegistry<T extends Keyed> implements Function<NamespacedKey, T> {
    private final HashMap<Key, T> registry = new HashMap<>();

    public WbsRegistry() {}

    @SafeVarargs
    public WbsRegistry(T... initial) {
        for (T t : initial) {
            register(t);
        }
    }

    public WbsRegistry(Iterable<T> initial) {
        for (T t : initial) {
            register(t);
        }
    }

    @Nullable
    public T register(T t) {
        return registry.put(t.key(), t);
    }

    @NotNull
    public T getAny() {
        return registry.values().stream().findFirst().orElseThrow();
    }

    @Nullable
    public T get(Key key) {
        return registry.get(key);
    }

    @NotNull
    public Optional<T> getOptional(Key key) {
        return Optional.ofNullable(registry.get(key));
    }

    @NotNull
    public <E extends Exception> T getOrElseThrow(Key key, Supplier<E> ex) throws E {
        return getOptional(key).orElseThrow(ex);
    }

    @NotNull
    public T getOrConfigException(Key key, String message, @Nullable String directory) throws InvalidConfigurationException {
        return getOptional(key).orElseThrow(() -> new InvalidConfigurationException(message, directory));
    }

    public Collection<T> values() {
        return registry.values();
    }

    public Collection<Key> keys() {
        return registry.keySet();
    }

    public Collection<T> getWhere(Predicate<T> predicate) {
        return registry.values().stream()
                .filter(predicate)
                .toList();
    }

    public Stream<T> stream() {
        return registry.values().stream();
    }

    @Override
    public T apply(NamespacedKey namespacedKey) {
        return get(namespacedKey);
    }
}
