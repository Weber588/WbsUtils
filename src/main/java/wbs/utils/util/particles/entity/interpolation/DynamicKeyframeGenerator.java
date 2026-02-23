package wbs.utils.util.particles.entity.interpolation;

import org.bukkit.entity.Entity;

import java.util.function.Function;

public class DynamicKeyframeGenerator<T extends Entity, V> extends KeyframeGenerator<T, V> {
    private final ParticleValueSupplier<V> supplier;

    public DynamicKeyframeGenerator(ParticleValueSupplier<V> supplier, int maxAge) {
        super(maxAge);

        this.supplier = supplier;
    }
    public DynamicKeyframeGenerator(Function<Double, V> supplier, int maxAge) {
        super(maxAge);

        this.supplier = (currentTick, firstTick, lastTick) -> {
            if (currentTick <= firstTick) {
                return supplier.apply(0d);
            }

            if (lastTick <= firstTick) {
                throw new IllegalStateException("Last tick cannot be less than or equal to first tick.");
            }

            double progress = (double) (currentTick - firstTick) / ((lastTick - 1) - firstTick);

            return supplier.apply(progress);
        };
    }

    public ParticleValueSupplier<V> getSupplier() {
        return supplier;
    }

    @Override
    protected V getValue(int tick) {
        return getSupplier().get(tick, getStartTick(), getEndTick());
    }
}
