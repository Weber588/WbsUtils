package wbs.utils.util.entities.state.tracker;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.entities.state.EntityState;
import wbs.utils.util.entities.state.EntityStateManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An {@link EntityState} that captures whether or not an {@link Entity}
 * is currently invulnerable.
 * @see Entity#isInvulnerable()
 */
@SuppressWarnings("unused")
public class InvulnerableState implements EntityState<Entity>, ConfigurationSerializable {

    private boolean invulnerable = false;

    /**
     * Creates the state with the default value (false).
     */
    public InvulnerableState() {}

    /**
     * @param invulnerable Whether or not the entity is invulnerable
     */
    public InvulnerableState(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    @Override
    public void captureState(Entity target) {
        invulnerable = target.isInvulnerable();
    }

    @Override
    public void restoreState(Entity target) {
        target.setInvulnerable(invulnerable);
    }

    /**
     * @return Whether or not the entity is invulnerable
     */
    public boolean isInvulnerable() {
        return invulnerable;
    }

    /**
     * @param invulnerable Whether or not the entity is invulnerable
     */
    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    @Override
    public @NotNull Set<Class<? extends EntityState<?>>> restoreAfter() {
        return new HashSet<>();
    }

    // Serialization
    private static final String INVULNERABLE = "invulnerable";

    /**
     * Deserializer method for converting a {@link Map} into this object, for use in {@link EntityStateManager}
     * @param args The partially deserialized Map.
     * @return An instance of this class, deserialized from args.
     */
    public static InvulnerableState deserialize(Map<String, Object> args) {
        Object invulnerable = args.get(INVULNERABLE);
        if (invulnerable instanceof Boolean) {
            return new InvulnerableState(invulnerable == Boolean.TRUE);
        }
        return new InvulnerableState();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(INVULNERABLE, invulnerable);

        return map;
    }
}
