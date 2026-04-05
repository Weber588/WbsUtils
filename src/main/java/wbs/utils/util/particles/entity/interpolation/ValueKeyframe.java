package wbs.utils.util.particles.entity.interpolation;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

public abstract class ValueKeyframe<V> {
    public static <V> ValueKeyframe<V> of(int tick, V value) {
        return new StaticValueKeyframe<>(tick, value);
    }
    public static <V> ValueKeyframe<V> of(double progress, V value) {
        return new ProgressValueKeyframe<>(progress, value);
    }

    private final V value;
    @Nullable
    private BiFunction<V, Double, V> varianceFunction = null;
    private double variance = 0;

    public ValueKeyframe(V value) {
        this.value = value;
    }

    public V getValue() {
        if (varianceFunction != null) {
            return varianceFunction.apply(value, variance);
        }
        return value;
    }

    public abstract int getTick(int maxTick);

    public ValueKeyframe<V> setVarianceFunction(@Nullable BiFunction<V, Double, V> varianceFunction) {
        this.varianceFunction = varianceFunction;
        return this;
    }

    public ValueKeyframe<V> setVariance(double variance) {
        this.variance = variance;
        return this;
    }

    public ValueKeyframe<V> setVariance(@NotNull BiFunction<V, Double, V> varianceFunction, double variance) {
        this.varianceFunction = varianceFunction;
        this.variance = variance;
        return this;
    }

    @FunctionalInterface
    public interface VarianceFunction<V> {
        /**
         * Vary a value by a given amount.
         * @param base The base value to vary
         * @param variance The amount of variance to apply
         * @return A new instance of T varied by the given amount
         */
        @Contract("_, _ -> new")
        V vary(V base, double variance);
    }

    private static class StaticValueKeyframe<V> extends ValueKeyframe<V> {
        private final int tick;

        public StaticValueKeyframe(int tick, V value) {
            super(value);
            this.tick = tick;
        }

        @Override
        public int getTick(int maxTick) {
            return tick;
        }
    }

    private static class ProgressValueKeyframe<V> extends ValueKeyframe<V> {
        private final double progress;

        public ProgressValueKeyframe(double progress, V value) {
            super(value);
            this.progress = progress;
        }

        @Override
        public int getTick(int maxTick) {
            return (int) Math.clamp((((double) maxTick) * progress), 0, maxTick);
        }
    }
}
