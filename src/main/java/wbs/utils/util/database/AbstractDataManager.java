package wbs.utils.util.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.WbsUtils;
import wbs.utils.util.plugin.WbsPlugin;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents an object that can be used to save &amp; retrieve {@link WbsRecord}s from
 * a database. <p>
 * Retrieves from a given table by default, but can be extended to read complex objects
 * across multiple.
 * @param <T> The type that can be saved to the database.
 * @param <K> The type used as the primary key in the database.
 */
@SuppressWarnings("unused")
public abstract class AbstractDataManager<T extends RecordProducer, K> {

    /**
     * The related {@link WbsPlugin}.
     */
    protected final WbsPlugin plugin;

    /**
     * @param plugin The related {@link WbsPlugin}
     * @param table The {@link WbsTable} to use as the default table for simple operations.
     */
    public AbstractDataManager(WbsPlugin plugin, WbsTable table) {
        this.plugin = plugin;
        this.defaultTable = table;
    }

    private final WbsTable defaultTable;

    /**
     * Represents which caching type to use for the volatile cache.
     */
    protected VolatileCacheType volatileCacheType = VolatileCacheType.WEAK;
    private final Map<K, Reference<T>> volatileCache = new HashMap<>();

    private int cacheSize = 25;
    private final Map<K, T> cache = new LinkedHashMap<K, T>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<K, T> eldest) {
            return size() > cacheSize;
        }
    };

    /**
     * Gets a shallow copy of the merged cache, including both the guaranteed cache and the
     * volatile cache.
     * @return The merged cache.
     */
    public Map<K, T> getCache() {
        Map<K, T> mergedCache = new HashMap<>(cache);

        if (volatileCacheType != VolatileCacheType.DISABLED) {
            Set<K> toRemove = new HashSet<>();

            for (K volatileKey : volatileCache.keySet()) {
                Reference<T> ref = volatileCache.get(volatileKey);
                T value = ref.get();
                if (value != null) {
                    mergedCache.putIfAbsent(volatileKey, value);
                } else {
                    toRemove.add(volatileKey);
                }
            }

            for (K volatileKey : toRemove) {
                volatileCache.remove(volatileKey);
            }
        }

        return mergedCache;
    }

    /**
     * @return The default table, as specified on construction.
     */
    protected WbsTable getDefaultTable() {
        return defaultTable;
    }

    /**
     * Set the number of objects that are cached in active memory to prevent
     * unneeded calls to the database. Changing the cache size while populated
     * with more entries than the new size will not remove excess entries.
     * <br/>
     * This is the number of records guaranteed to exist in the cache once populated,
     * however more may be stored as {@link SoftReference}s.
     * @param size The new size of the cache.
     */
    public void setCacheSize(int size) {
        cacheSize = size;
    }

    /**
     * Clear the cache.
     * @return The number of entries the cache contained prior to being cleared.
     */
    public int clearCache() {
        int size = getCache().size();
        cache.clear();
        volatileCache.clear();
        return size;
    }

    /**
     * Adds a key/value entry to the cache directly
     * @param key The key for the cache entry
     * @param value The value to add to the cache
     */
    protected void addToCache(K key, T value) {
        cache.put(key, value);

        switch (volatileCacheType) {
            case SOFT:
                volatileCache.put(key, new SoftReference<>(value));
                break;
            case WEAK:
                volatileCache.put(key, new WeakReference<>(value));
                break;
            case DISABLED:
                break;
        }
    }

    /**
     * Synchronously get a record based on it's key. It's recommended
     * to use {@link #getAsync(Object, Consumer)} to avoid freezing the server,
     * or use this in an asynchronous thread.
     * @param key The key to retrieve by.
     * @return The value, or a new value based on the key.
     */
    @NotNull
    public T get(K key) {
        Map<K, T> mergedCache = getCache();
        if (mergedCache.containsKey(key)) return mergedCache.get(key);

        WbsRecord record = select(Collections.singletonList(key));

        T found;
        if (record != null) {
            found = fromRecord(record);
        } else {
            found = produceDefault(key);
        }

        addToCache(key, found);

        return found;
    }

    /**
     * Get a value from the cache if it exists
     * @param key The key to retrieve by
     * @return The value to which the key is mapped, or null otherwise.
     */
    @Nullable
    public T getCached(K key) {
        return getCache().get(key);
    }

    /**
     * Get a record asynchronously, or synchronously if the
     * value is cached.
     * @param key The key to retrieve by.
     * @param callback The consumer to be called when the value is available.
     * @return The Id of the scheduled BukkitTask. To get the retrieved object,
     * accept it in the callback.
     */
    public int getAsync(K key, @NotNull Consumer<T> callback) {
        Map<K, T> mergedCache = getCache();
        if (mergedCache.containsKey(key)) {
            callback.accept(mergedCache.get(key));
            return -1;
        }

        return plugin.getAsync(() -> get(key), callback);
    }

    /**
     * Write all cached values to the database
     */
    public void saveCacheAsync() {
        saveAsync(new LinkedList<>(cache.values()));
    }

    /**
     * Write all cached values to the database
     */
    public void saveCache() {
        Map<K, T> mergedCache = getCache();
        if (!mergedCache.isEmpty())
            save(new LinkedList<>(mergedCache.values()));
    }

    /**
     * Write a collection of objects to the database asynchronously.
     * @param toInsert The objects to insert
     */
    public void saveAsync(Collection<T> toInsert) {
        saveAsync(toInsert, () -> {});
    }

    /**
     * Write a collection of objects to the database asynchronously,
     * with a callback for when the operation has been completed.
     * @param toInsert The objects to insert.
     * @param callback A callback to run once the operation is complete.
     */
    public void saveAsync(Collection<T> toInsert, Runnable callback) {
        plugin.runAsync(() -> save(toInsert), callback);
    }

    /**
     * Save the given collection of {@link T} to the database synchronously.
     * @param toInsert The records to save to the database.
     */
    public void save(Collection<T> toInsert) {
        if (toInsert.isEmpty()) return;
        List<WbsRecord> records =
                toInsert.stream()
                        .map(RecordProducer::toRecord)
                        .collect(Collectors.toList());
        defaultTable.upsert(records);
    }

    /**
     * Overrideable field for selecting from the database.
     * Just reads from the default table by default.
     * @param keys An ordered list of keys to use against the default table's primary keys.
     * @return The found record, or null if none found.
     */
    @Nullable
    protected WbsRecord select(List<K> keys) {
        List<WbsRecord> records = defaultTable.selectOnFields(defaultTable.getPrimaryKeys(), keys);
        if (records.isEmpty()) {
            return null;
        } else {
            return records.get(0);
        }
    }

    /**
     * Create an object based on the record representing it.
     * @param record The record to be read into a new object
     * @return A non-null object of type T
     */
    @NotNull
    protected abstract T fromRecord(@NotNull WbsRecord record);

    /**
     * Create an object based on the key. Used for when
     * no record was found in the database.
     * @param key The key to use to generate the new object.
     * @return A possibly null object of type T
     */
    @NotNull
    protected abstract T produceDefault(K key);

    /**
     * What type of volatile caching to use, where:
     * <ul>
     *     <li>{@link #SOFT} uses {@link SoftReference}s;</li>
     *     <li>{@link #WEAK} uses {@link WeakReference}s; and</li>
     *     <li>{@link #DISABLED} disables the use of the volatile cache entirely</li>
     * </ul>
     */
    public enum VolatileCacheType {
        /**
         * Represents a caching strategy using {@link SoftReference}s.
         */
        SOFT,
        /**
         * Represents a caching strategy using {@link WeakReference}s.
         */
        WEAK,
        /**
         * Represents no caching.
         */
        DISABLED
    }
}
