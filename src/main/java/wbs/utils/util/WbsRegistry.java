package wbs.utils.util;

import io.papermc.paper.registry.RegistryAccess;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class WbsRegistry<T extends Keyed> {
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

    @Nullable
    public T get(Key key) {
        return registry.get(key);
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
}
