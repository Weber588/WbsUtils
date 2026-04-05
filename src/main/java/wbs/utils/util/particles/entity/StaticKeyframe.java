package wbs.utils.util.particles.entity;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Entity;

import java.util.function.Consumer;

public class StaticKeyframe<T extends Entity> extends Keyframe<T> {
    private final int tick;

    protected StaticKeyframe(int tick, Consumer<EntityParticle<T>> keyframe) {
        super(keyframe);

        Preconditions.checkArgument(tick >= 0, "tick cannot be negative.");

        this.tick = tick;
    }

    @Override
    public int getTick(int maxTick) {
        return tick;
    }
}
