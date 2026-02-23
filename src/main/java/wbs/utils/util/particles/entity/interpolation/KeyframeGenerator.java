package wbs.utils.util.particles.entity.interpolation;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import wbs.utils.util.particles.entity.EntityParticle;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class KeyframeGenerator<T extends Entity, V> {
    protected int endTick;
    private int startTick = 0;
    @Nullable
    private ParticleSetter<T, V> setter;

    public KeyframeGenerator(int maxAge) {
        endTick = maxAge;
    }

    public KeyframeGenerator<T, V> setSetter(ParticleSetter<T, V> setter) {
        this.setter = setter;
        return this;
    }

    public KeyframeGenerator<T, V> setEntitySetter(BiConsumer<T, V> setter) {
        this.setter = (particle, v) -> {
            setter.accept(particle.getEntity(), v);
        };
        return this;
    }

    public KeyframeGenerator<T, V> setTickRange(int start, int end) {
        this.startTick = start;
        this.endTick = end;
        return this;
    }

    public KeyframeGenerator<T, V> setStartTick(int startTick) {
        this.startTick = startTick;
        return this;
    }

    public KeyframeGenerator<T, V> setEndTick(int endTick) {
        this.endTick = endTick;
        return this;
    }

    public int getStartTick() {
        return startTick;
    }

    public int getEndTick() {
        return endTick;
    }

    public @NotNull ParticleSetter<T, V> getSetter() throws IllegalStateException {
        if (setter == null) {
            throw new IllegalStateException("DynamicKeyframeGenerators require a setter.");
        }
        return setter;
    }

    public Map<Integer, Consumer<EntityParticle<T>>> generate() {
        Map<Integer, Consumer<EntityParticle<T>>> frames = new HashMap<>();

        for (int i = getStartTick(); i <= getEndTick(); i++) {
            final int tick = i;

            Consumer<EntityParticle<T>> dynamicKeyframe = particle -> {
                getSetter().set(particle, getValue(tick));
            };

            frames.put(tick, dynamicKeyframe);
        }

        return frames;
    }

    protected abstract V getValue(int tick);
}
