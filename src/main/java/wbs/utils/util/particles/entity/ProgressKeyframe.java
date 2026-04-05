package wbs.utils.util.particles.entity;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Entity;

import java.util.function.Consumer;

public class ProgressKeyframe<T extends Entity> extends Keyframe<T> {
    private final double progress;

    protected ProgressKeyframe(double progress, Consumer<EntityParticle<T>> keyframe) {
        super(keyframe);

        Preconditions.checkArgument(progress >= 0, "progress cannot be negative.");
        Preconditions.checkArgument(progress <= 1, "progress must be less than or equal to 1");

        this.progress = progress;
    }

    @Override
    public int getTick(int maxTick) {
        return (int) Math.clamp((((double) maxTick) * progress), 0, maxTick);
    }
}
