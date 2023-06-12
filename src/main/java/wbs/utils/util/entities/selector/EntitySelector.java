package wbs.utils.util.entities.selector;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A helper class for selecting an entity or collection of entities in a variety of controllable ways.
 * <br/>
 * Designed to replace the many selection methods in the now deprecated {@link wbs.utils.util.WbsEntities}.
 * @param <T> The subclass of entity to select
 * @param <E> The class of an implementing type, for ease of use in end point methods.
 */
@SuppressWarnings({"unused"})
public abstract class EntitySelector<T extends Entity, E extends EntitySelector<T, E>> {

    /**
     * @param clazz The subclass to be selected
     */
    public EntitySelector(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * The subclass to be selected
     */
    protected final Class<T> clazz;
    /**
     * The range the selector will search in.
     */
    protected double range = 10;
    private int maxSelections = Integer.MAX_VALUE;
    @NotNull
    private final Set<T> exclude = new HashSet<>();
    @NotNull
    private Predicate<T> predicate = entity -> true;

    /**
     * Perform a selection and return all found.
     * @param loc The location to perform the selection from
     * @return All selections
     */
    @NotNull
    protected abstract List<T> getSelection(Location loc);

    /**
     * @return This object as the subclass
     */
    protected abstract E getThis();

    // ================================== //
    //           List Selectors           //
    // ================================== //

    /**
     * Selects all entities of the given type, limited
     * to {@link #maxSelections}.
     * @param loc The location to perform the selection from
     * @return An ordered selection, limited to {@link #maxSelections} entities.
     */
    @NotNull
    public List<T> select(Location loc) {
        List<T> selection = getSelection(loc);
        List<T> newSelection = new LinkedList<>();

        int i = 0;
        for (T selected : selection) {
            newSelection.add(selected);
            i++;
            if (i >= maxSelections) break;
        }

        return newSelection;
    }

    /**
     * Selects all entities of the given type, limited
     * to {@link #maxSelections}.
     * @param entity The entity whose location will be used to
     *               perform the selection.
     * @return An ordered selection, limited to {@link #maxSelections} entities.
     */
    @NotNull
    public final List<T> select(Entity entity) {
        return select(entity.getLocation());
    }

    /**
     * Selects all entities of the given type, limited
     * to {@link #maxSelections}, excluding the given entity
     * @param entity The entity whose location will be used to
     *               perform the selection.
     * @return The first found entity, or null if none were found.
     */
    @NotNull
    public final List<T> selectExcluding(T entity) {
        exclude.add(entity);
        List<T> selected = select(entity);
        exclude.remove(entity);
        return selected;
    }

    /**
     * Selects all entities of the given type, limited
     * to {@link #maxSelections}, excluding the given entity
     * @param loc The location to perform the selection from
     * @param entity The entity to exclude from this operation only
     * @return The first found entity, or null if none were found.
     */
    @NotNull
    public final List<T> selectExcluding(Location loc, T entity) {
        exclude.add(entity);
        List<T> selected = select(loc);
        exclude.remove(entity);
        return selected;
    }

    // ================================== //
    //           First Selectors          //
    // ================================== //

    /**
     * Select only the first found selectable entity
     * @param loc The location to perform the selection from
     * @return The first found entity, or null if none were found.
     */
    @Nullable
    public final T selectFirst(Location loc) {
        List<T> selection = select(loc);

        if (selection.isEmpty()) return null;
        return select(loc).get(0);
    }

    /**
     * Select only the first found selectable entity.
     * To ignore the provided entity, use {@link #selectFirstExcluding(Entity)} instead.
     * @param entity The entity whose location will be used to
     *               perform the selection.
     * @return The first found entity, or null if none were found.
     */
    @Nullable
    public final T selectFirst(Entity entity) {
        return selectFirst(entity.getLocation());
    }

    /**
     * Select only the first found selectable entity, excluding
     * the provided one.
     * @param entity The entity whose location will be used to
     *               perform the selection.
     * @return The first found entity, or null if none were found.
     */
    @Nullable
    public final T selectFirstExcluding(T entity) {
        exclude.add(entity);
        T selected = selectFirst(entity);
        exclude.remove(entity);
        return selected;
    }

    /**
     * Select only the first found selectable entity, excluding
     * the provided one.
     * @param loc The location to perform the selection from
     * @param entity The entity to exclude from this operation only
     * @return The first found entity, or null if none were found.
     */
    @Nullable
    public final T selectFirstExcluding(Location loc, T entity) {
        exclude.add(entity);
        T selected = selectFirst(loc);
        exclude.remove(entity);
        return selected;
    }

    // ================================== //
    //          Random Selectors          //
    // ================================== //

    /**
     * Select a single random selectable entity
     * @param loc The location to perform the selection from
     * @return A random found entity, or null if none were found.
     */
    @Nullable
    public final T selectRandom(Location loc) {
        List<T> selection = select(loc);

        if (selection.isEmpty()) return null;

        return getRandom(selection);
    }

    /**
     * Select a single random selectable entity
     * To ignore the provided entity, use {@link #selectRandomExcluding(Entity)} instead.
     * @param entity The entity whose location will be used to
     *               perform the selection.
     * @return A random found entity, or null if none were found.
     */
    @Nullable
    public final T selectRandom(Entity entity) {
        return selectRandom(entity.getLocation());
    }

    /**
     * Select only the first found selectable entity, excluding
     * the provided one.
     * @param entity The entity whose location will be used to
     *               perform the selection.
     * @return A random found entity, or null if none were found.
     */
    @Nullable
    public final T selectRandomExcluding(T entity) {
        exclude.add(entity);
        T selected = selectRandom(entity);
        exclude.remove(entity);
        return selected;
    }

    /**
     * Select only the first found selectable entity, excluding
     * the provided one.
     * @param loc The location to perform the selection from
     * @param entity The entity to exclude from this operation only
     * @return A random found entity, or null if none were found.
     */
    @Nullable
    public final T selectRandomExcluding(Location loc, T entity) {
        exclude.add(entity);
        T selected = selectRandom(loc);
        exclude.remove(entity);
        return selected;
    }

    // ================================== //
    //          Utility Methods           //
    // ================================== //

    private final static Random RANDOM = new Random();
    private T getRandom(List<T> selection) {
        return selection.get(RANDOM.nextInt(selection.size()));
    }

    /**
     * A utility method for use in implementations, that filters the given collection against {@link T}
     * to ensure all instances are of type T (or removed otherwise)
     * @param collection The collection of entities to filter
     * @param <U> Some extension of {@link Entity}
     * @return The filtered collection as a Set of T.
     */
    protected final <U extends Entity> Set<T> filter(Collection<U> collection) {
        return collection.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toSet());
    }

    /**
     * @return A new predicate against Entity that returns true if:
     * <ul>
     *     <li>The entity is of type {@link T};</li>
     *     <li>The entity is not in the exclusion list as defined by {@link #exclude(Collection)}; and</li>
     *     <li>The custom predicate returns true, as defined by {@link #setPredicate(Predicate)}</li>
     * </ul>
     */
    @NotNull
    protected Predicate<Entity> getRawPredicate() {
        return entity ->
                clazz.isInstance(entity) && isValid(clazz.cast(entity));
    }

    /**
     * @param entity The entity to test.
     * @return true if the entity is not excluded, and the custom predicate passes.
     */
    protected boolean isValid(T entity) {
        return !exclude.contains(entity) && predicate.test(entity);
    }

    // ================================== //
    //          Getters/Setters           //
    // ================================== //

    /**
     * @return Gets the current range of selection
     */
    public final double getRange() {
        return range;
    }

    /**
     * @param range The range of selection
     * @return The same object.
     */
    public final E setRange(double range) {
        this.range = range;
        return getThis();
    }

    /**
     * @return The maximum number of entities to find in a given {@link #select(Location)}.
     */
    public final int getMaxSelections() {
        return maxSelections;
    }

    /**
     * @param maxSelections The maximum number of entities to find in a given {@link #select(Location)}.
     * @return The same object.
     */
    public final E setMaxSelections(int maxSelections) {
        this.maxSelections = maxSelections;
        return getThis();
    }

    /**
     * The current entity predicate.
     * @return The same object.
     */
    public final @NotNull Predicate<T> getPredicate() {
        return predicate;
    }

    /**
     * @param predicate The new predicate
     * @return The same object.
     */
    public final E setPredicate(Predicate<T> predicate) {
        this.predicate = predicate;
        return getThis();
    }

    /**
     * @param predicate The new predicate, which will also require a test for being an instance of T.
     * @return The same object.
     */
    public final E setPredicateRaw(Predicate<Entity> predicate) {
        this.predicate = entity ->
                clazz.isInstance(entity) && predicate.test(entity);
        return getThis();
    }

    /**
     * @return Gets the set of currently excluded entities
     */
    public final @NotNull Set<T> getExclude() {
        return exclude;
    }

    /**
     * @param toAdd The entity to exclude
     * @return The same object.
     */
    public final E exclude(T toAdd) {
        exclude.add(toAdd);
        return getThis();
    }

    /**
     * @param toAdd The entities to exclude
     * @return The same object.
     */
    public final E exclude(Collection<T> toAdd) {
        exclude.addAll(toAdd);
        return getThis();
    }

    /**
     * @param toRemove The entity to remove from the exclusion set, to allow it to be selected
     * @return The same object.
     */
    public final boolean unexclude(T toRemove) {
        return exclude.remove(toRemove);
    }

    /**
     * @param toRemove The entities to remove from the exclusion set, to allow it to be selected
     * @return The same object.
     */
    public final boolean unexclude(Collection<T> toRemove) {
        return exclude.removeAll(toRemove);
    }
}
