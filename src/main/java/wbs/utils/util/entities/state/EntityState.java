package wbs.utils.util.entities.state;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Represents a capture of some property of an entity, such
 * as health.<p/>
 * The data captured in EntityStates should be immutable to the
 * game loop; that is, after capturing, it shouldn't matter what
 * happens to the captured entity. When an entity is restored,
 * the tracked state should be identical to the moment it was
 * captured, unless the EntityState object was directly interacted
 * with.<p/>
 * As an example, a captured ItemStack should appear the same
 * after being restored as it did when captured, even if the
 * original ItemStack had it's amount or ItemMeta changed.
 * @param <T> The Entity subclass this state may be used on
 */
public interface EntityState<T extends Entity> {
    void captureState(T target);
    void restoreState(T target);

    @NotNull
    Set<Class<? extends EntityState<?>>> restoreAfter();
}
