package wbs.utils.util.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents an object that can be used to save & retrieve {@link WbsRecord}s from
 * a database. <p/>
 * Retrieves from a given table by default, but can be extended to read complex objects
 * across multiple.
 * @param <T> The type that can be saved to the database.
 * @param <K> The type used as the primary key in the database.
 */
@SuppressWarnings("unused")
public abstract class AbstractDataManager<T extends RecordProducer, K> {

    protected final WbsPlugin plugin;

    public AbstractDataManager(WbsPlugin plugin, WbsTable table) {
        this.plugin = plugin;
        this.defaultTable = table;
    }

    private final WbsTable defaultTable;

    private int cacheSize = 25;
    private final Map<K, T> cache = new LinkedHashMap<K, T>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<K, T> eldest) {
            return size() > cacheSize;
        }
    };

    protected Map<K, T> getCache() {
        return cache;
    }
    protected WbsTable getDefaultTable() {
        return defaultTable;
    }

    /**
     * Set the number of objects that are cached in active memory to prevent
     * unneeded calls to the database. Changing the cache size while populated
     * with more entries than the new size will not remove excess entries.
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
        int size = cache.size();
        cache.clear();
        return size;
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
        if (cache.containsKey(key)) return cache.get(key);

        WbsRecord record = select(Collections.singletonList(key));

        T found;
        if (record != null) {
            found = fromRecord(record);
        } else {
            found = produceDefault(key);
        }

        cache.put(key, found);
        return found;
    }

    /**
     * Get a value from the cache if it exists
     * @param key The key to retrieve by
     * @return The value to which the key is mapped, or null otherwise.
     */
    @Nullable
    public T getCached(K key) {
        return cache.get(key);
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
        if (cache.containsKey(key)) {
            callback.accept(cache.get(key));
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
        if (!cache.isEmpty()) save(new LinkedList<>(cache.values()));
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
}
