package wbs.utils.util.particles.entity.interpolation;

import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class InterpolatedFrameGenerator<T extends Entity, V> extends KeyframeGenerator<T, V> {
    private final Interpolator<V> interpolator;
    private final V defaultValue;
    private final Map<Integer, V> frames = new HashMap<>();

    public InterpolatedFrameGenerator(int maxAge, Interpolator<V> interpolator, V defaultValue) {
        super(maxAge);
        this.interpolator = interpolator;
        this.defaultValue = defaultValue;
    }

    @Override
    protected V getValue(int currentTick) {
        if (currentTick < getStartTick() || currentTick > getEndTick()) {
            throw new IllegalStateException("Internal rotation frame invalid");
        }

        if (frames.isEmpty()) {
            return defaultValue;
        }

        int firstFrame = frames.keySet().stream().mapToInt(val -> val).min().orElseThrow();

        if (currentTick < firstFrame) {
            return defaultValue;
        }

        V explicitKeyframe = frames.get(currentTick);
        if (explicitKeyframe != null) {
            return explicitKeyframe;
        }

        int previousFrame = frames.keySet().stream()
                .filter(tick -> tick < currentTick)
                .mapToInt(val -> val)
                .max() // Max frame less than this one
                .orElseThrow();


        int nextFrame = frames.keySet().stream()
                .filter(tick -> tick > currentTick)
                .mapToInt(val -> val)
                .min()
                .orElseThrow();

        V previousValue = frames.get(previousFrame);
        V nextValue = frames.get(nextFrame);

        double progress = (double) (currentTick - getStartTick()) / ((getEndTick() - 1) - getStartTick());

        return interpolator.interpolate(previousValue, nextValue, progress);
    }

    @FunctionalInterface
    public interface Interpolator<T> {
        T interpolate(T previousValue, T nextValue, double progress);
    }
}
