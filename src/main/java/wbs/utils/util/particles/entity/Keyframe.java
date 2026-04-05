package wbs.utils.util.particles.entity;

import org.bukkit.entity.Entity;

import java.util.function.Consumer;

public abstract class Keyframe<T extends Entity> {
    public static <T extends Entity> Keyframe<T> particle(int tick, Consumer<EntityParticle<T>> keyframe) {
        return new StaticKeyframe<>(tick, keyframe);
    }
    public static <T extends Entity> Keyframe<T> particle(double progress, Consumer<EntityParticle<T>> keyframe) {
        return new ProgressKeyframe<>(progress, keyframe);
    }
    public static <T extends Entity> Keyframe<T> entity(int tick, Consumer<T> keyframe) {
        return new StaticKeyframe<>(tick, particle -> keyframe.accept(particle.getEntity()));
    }
    public static <T extends Entity> Keyframe<T> entity(double progress, Consumer<T> keyframe) {
        return new ProgressKeyframe<>(progress, particle -> keyframe.accept(particle.getEntity()));
    }

    private final Consumer<EntityParticle<T>> keyframe;

    protected Keyframe(Consumer<EntityParticle<T>> keyframe) {
        this.keyframe = keyframe;
    }

    public abstract int getTick(int maxTick);
    public Consumer<EntityParticle<T>> getValue() {
        return keyframe;
    }
    public void run(EntityParticle<T> particle) {
        keyframe.accept(particle);
    }
    public void runIfMatch(EntityParticle<T> particle, int currentTick) {
        if (getTick(particle.maxAge - 1) == currentTick) {
            run(particle);
        }
    }
}
