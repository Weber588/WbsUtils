package wbs.utils.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

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
     * Gets a random element of a collection, including unordered ones.
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
            if (index == current) {
                return t;
            }
            current++;
        }
        throw new AssertionError("Random selection failed to select a value.");
    }

    /**
     * Choose a pseudo random element from the provided map of values to chances, ignoring elements contained
     * in the history list provided.<p>
     * This method makes use of the {@link #getRandomWeighted(Map)} method.
     * @param weightedMap A non-empty map of values to their weights
     * @param history A list forming a subset of the keys in the weighted map, already chosen randomly previously,
     *                which may not be chosen during this call.
     *                The intent of this parameter is to be passed into this method repeatedly, to allow it to
     *                avoid repeats, and automatically resize to maintain a buffer to prevent repeats within a certain
     *                number of operations.
     * @param repeatRatio How much of the history list is to be used as a buffer, relative to the size of the map given.
     *                    For example, setting this to 3 with a map size of 60 would prevent a given element being
     *                    chosen within 20 calls to this method.
     * @param <T> The type of element to be selected.
     * @param <N> The type of number used in the map to represent weights.
     * @return The chosen element.
     * @throws RuntimeException Thrown when it takes more than 1000 * the size of the given map attempts to find a
     *                          non-history value.
     */
    @NotNull
    public static <T, N extends Number>  T pseudoRandomAvoidRepeats(Map<T, N> weightedMap,
                                                                    List<T> history,
                                                                    double repeatRatio) {
        if (weightedMap.isEmpty()) throw new IllegalArgumentException("Map is empty.");
        if (repeatRatio <= 1) throw new IllegalArgumentException("repeatRatio must be greater than 1.");
        if (history.size() >= weightedMap.size())
            throw new IllegalArgumentException("Size of history must be less than size of weighted map.");

        T chosen;
        int escape = 0;
        int escapeThreshold = weightedMap.size() * 1000;
        do {
            chosen = getRandomWeighted(weightedMap);

            escape++;
            if (escape >= escapeThreshold) {
                // TODO: Find/create a better exception to throw
                throw new RuntimeException("Operation failed to find an object within map size * 1000 attempts. (" +
                        escapeThreshold + ")");
            }
        } while (history.contains(chosen));

        history.add(chosen);
        while (history.size() > weightedMap.size() / repeatRatio) {
            history.remove(0);
        }

        return chosen;
    }

    /**
     * Overload of {@link #pseudoRandomAvoidRepeats(Map, List, double)} with repeatRatio set to 2.
     */
    @NotNull
    public static <T, N extends Number> T pseudoRandomAvoidRepeats(Map<T, N> weightedMap, List<T> history) {
        return pseudoRandomAvoidRepeats(weightedMap, history, 2);
    }

    /**
     * Choose a pseudo random element from the provided map of values to chances, ignoring elements contained
     * in the history list provided.<p>
     * This method makes use of the {@link #getRandomWeighted(Map)} method.
     * @param supplier A generator that returns
     * @param size The amount of unique possibilities that may be produced by supplier, usually the size of the
     *             collection being returned from.
     * @param history A list forming a subset of the keys in the weighted map, already chosen randomly previously,
     *                which may not be chosen during this call.
     *                The intent of this parameter is to be passed into this method repeatedly, to allow it to
     *                avoid repeats, and automatically resize to maintain a buffer to prevent repeats within a certain
     *                number of operations.
     * @param repeatRatio How much of the history list is to be used as a buffer, relative to the size of the map given.
     *                    For example, setting this to 3 with a map size of 60 would prevent a given element being
     *                    chosen within 20 calls to this method.
     * @param <T> The type of element to be selected.
     * @return The chosen element.
     * @throws RuntimeException Thrown when it takes more than 1000 * the size of the given map attempts to find a
     *                          non-history value.
     */
    @NotNull
    public static <T> T getAvoidRepeats(Supplier<T> supplier,
                                         int size,
                                         List<T> history,
                                         double repeatRatio) {
        if (repeatRatio <= 1) throw new IllegalArgumentException("repeatRatio must be greater than 1.");
        if (history.size() >= size)
            throw new IllegalArgumentException("Size of history must be less than size of weighted map.");

        T chosen;
        int escape = 0;
        do {
            chosen = supplier.get();

            escape++;
            if (escape >= size * 1000) {
                // TODO: Find/create a better exception to throw
                throw new RuntimeException("Operation failed to find an object within 1000 * size attempts. (" + (size * 1000) + ")");
            }
        } while (history.contains(chosen));

        history.add(chosen);
        while (history.size() > size / repeatRatio) {
            history.remove(0);
        }

        return chosen;
    }
}
