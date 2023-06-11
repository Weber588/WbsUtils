package wbs.utils.util.entities.state;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import wbs.utils.WbsUtils;
import wbs.utils.util.entities.state.tracker.*;

import java.util.*;
import java.util.logging.Logger;

/**
 * Represents a snapshot of an entity, tracking only specific qualities.
 * Can be captured and restored from entities to save an entity's state
 * before returning them to their former state later.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class SavedEntityState<T extends Entity> implements ConfigurationSerializable {

    private final EntityStateGraph<T> graph = new EntityStateGraph<>();

    public SavedEntityState() {

    }

    public SavedEntityState(Collection<EntityState<? super T>> toTrack) {
        track(toTrack);
    }

    /**
     * Add all known states that aren't already being tracked.
     * If using an extending class, such as {@link SavedPlayerState},
     * additional states are tracked for the implementing classes entity type.
     * <p>
     * Note that this does not use a concrete way of finding subclasses; the classpath
     * is not scanned, and the generic parameter isn't used. This is manually
     * implemented and will not reflect new changes without updating, including
     * new trackable states created outside of WbsUtils.
     */
    public void trackAll() {
        trackIfAbsent(new FallDistanceState());
        trackIfAbsent(new FireTicksState());
        trackIfAbsent(new InvulnerableState());
        trackIfAbsent(new LocationState());
        trackIfAbsent(new VelocityState());
    }

    /**
     * Add multiple states to track.
     * Overrides existing states if another state of
     * the same class is already being tracked.
     * @param toTrack The new states to track
     * @return The same object (for chaining)
     */
    public SavedEntityState<T> track(Collection<EntityState<? super T>> toTrack) {
        for (EntityState<? super T> tracker : toTrack) {
            track(tracker);
        }
        return this;
    }

    /**
     * Add a new state to track.
     * @param toTrack The new state to track
     * @return The same object (for chaining)
     */
    public SavedEntityState<T> track(EntityState<? super T> toTrack) {
        graph.addTracker(toTrack);
        return this;
    }

    @SuppressWarnings("unchecked")
    private SavedEntityState<?> trackUnsafe(EntityState<?> toTrack) {
        graph.addTracker((EntityState<? super T>) toTrack);
        return this;
    }

    /**
     * Add a new state to track, only if another of the same
     * class has not been added.
     * @param toTrack The new state to track
     * @return The same object (for chaining)
     */
    public SavedEntityState<T> trackIfAbsent(EntityState<? super T> toTrack) {
        graph.addIfAbsent(toTrack);
        return this;
    }

    /**
     * Capture the tracked states for the given entity.
     * @param target The entity to capture states from.
     * @return The same object (for chaining)
     */
    public SavedEntityState<T> captureState(T target) {
        graph.captureState(target);
        return this;
    }

    /**
     * Restore the captured states to the given entity.
     * The entity provided need not be the same entity
     * the states were originally captured from.
     * @param target The entity to restore the states to.
     * @return The same object (for chaining)
     */
    public SavedEntityState<T> restoreState(T target) {
        graph.restoreState(target);
        return this;
    }

    // Serialization
    public static SavedEntityState<?> deserialize(Map<String, Object> args) {
        Object stateNames = args.get("tracked-types");

        SavedEntityState<? extends Entity> savedState = new SavedEntityState<>();

        Logger logger = WbsUtils.getInstance().logger;

        if (stateNames instanceof List) {
            for (Object check : (List<?>) stateNames) {
                if (check instanceof String) {
                    String escapedName = (String) check;
                    EntityState<? extends Entity> state = EntityStateManager.deserialize(escapedName, args);

                    if (state == null) {
                        logger.warning("An entity state failed to deserialize: " +
                                EntityStateManager.unescapeClassName(escapedName));
                        continue;
                    }

                    try {
                        savedState.trackUnsafe(state);
                    } catch (ClassCastException e) {
                        logger.warning("An entity state failed to deserialize (invalid subclass): " +
                                EntityStateManager.unescapeClassName(escapedName));
                    }
                }
            }
        }

        return savedState;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        List<String> stateNames = new LinkedList<>();

        for (EntityState<? super T> state : graph) {
            if (state instanceof ConfigurationSerializable) {
                map.putAll(((ConfigurationSerializable) state).serialize());
                stateNames.add(EntityStateManager.getEscapedClassName(state.getClass()));
            }
        }

        map.put("tracked-types", stateNames);

        return map;
    }
}
