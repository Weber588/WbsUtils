package wbs.utils.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("unused")
public final class WbsCollectionUtil {
    private WbsCollectionUtil() {}

    /**
     * Gets a weighted value from a set
     * @param weightedMap A map of values to their weights
     * @return A random value from the key set of the provided map, weighted by
     * the value.
     * @throws IllegalArgumentException Thrown if the collection is empty.
     */
    @NotNull
    public static <T, N extends Number> T getRandomWeighted(Map<T, N> weightedMap) {
        if (weightedMap.isEmpty()) throw new IllegalArgumentException("Map is empty.");

        double weight = 0;
        for (T value : weightedMap.keySet()) {
            weight += weightedMap.get(value).doubleValue();
        }

        double current = 0;
        double random = Math.random() * weight;
        for (T value : weightedMap.keySet()) {
            current += weightedMap.get(value).doubleValue();
            if (current >= random) {
                return value;
            }
        }

        throw new AssertionError("Weighted map calculation failed to select a value.");
    }

    /**
     * Gets a random
     * @param collection The collection containing
     * @return A random value from the collection.
     * @throws IllegalArgumentException Thrown if the collection is empty.
     */
    @NotNull
    public static <T> T getRandom(Collection<T> collection) {
        if (collection.isEmpty()) throw new IllegalArgumentException("Collection is empty.");

        int index = new Random().nextInt(collection.size());
        int current = 0;
        for (T t : collection) {
            current++;
            if (index >= current) {
                return t;
            }
        }
        throw new AssertionError("Random selection failed to select a value.");
    }
}
