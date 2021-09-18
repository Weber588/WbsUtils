package wbs.utils.util.entities.state;

import org.bukkit.entity.Entity;
import wbs.utils.util.entities.state.tracker.*;

import java.util.Collection;

/**
 * Represents a snapshot of an entity, tracking only specific qualities.
 * Can be captured and restored from entities to save an entity's state
 * before returning them to their former state later.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class SavedEntityState<T extends Entity> {

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
     * <p/>
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
}
