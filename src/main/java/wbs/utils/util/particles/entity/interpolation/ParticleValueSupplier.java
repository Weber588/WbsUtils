package wbs.utils.util.particles.entity.interpolation;

@FunctionalInterface
public interface ParticleValueSupplier<V> {
    V get(int currentTick, int firstTick, int lastTick);
}
