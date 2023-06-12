package wbs.utils.util.entities.state;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.tracker.AllowFlightState;
import wbs.utils.util.entities.state.tracker.GameModeState;

import java.util.Set;

/**
 * Represents a capture of some property of an entity, such
 * as health.<p>
 * The data captured in EntityStates should be immutable to the
 * game loop; that is, after capturing, it shouldn't matter what
 * happens to the captured entity. When an entity is restored,
 * the tracked state should be identical to the moment it was
 * captured, unless the EntityState object was directly interacted
 * with.<p>
 * As an example, a captured ItemStack should appear the same
 * after being restored as it did when captured, even if the
 * original ItemStack had it's amount or ItemMeta changed.
 * @param <T> The Entity subclass this state may be used on
 */
public interface EntityState<T extends Entity> {
    /**
     * Capture the state of an instance of {@link T} according to the implementation,
     * and store it until {@link #restoreState(T)} is called.
     * @param target The {@link T} from which to capture.
     */
    void captureState(T target);

    /**
     * Restore the state of an instance of {@link T} according to the implementation.
     * @param target The {@link T} for which to the stored state.
     */
    void restoreState(T target);

    /**
     * Used to build a graph of captured states, allowing states to be restored
     * in the appropriate order, such as restoring {@link AllowFlightState} after {@link GameModeState},
     * to prevent a change to {@link GameMode#SURVIVAL} from overriding the {@link Player#getAllowFlight()}
     * state.
     * @return A set of classes to restore after, if present in a given {@link SavedEntityState}.
     */
    @NotNull
    Set<Class<? extends EntityState<?>>> restoreAfter();
}
