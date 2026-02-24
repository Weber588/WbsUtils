package wbs.utils.util.particles.entity.interpolation;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class InterpolatedFrameGenerator<T extends Entity, V> extends KeyframeGenerator<InterpolatedFrameGenerator<T, V>, T, V> {
    private final Interpolator<V> interpolator;
    private final V defaultValue;
    private final Map<Integer, V> frames = new HashMap<>();

    public InterpolatedFrameGenerator(int maxAge, Interpolator<V> interpolator, V defaultValue) {
        super(maxAge);
        this.interpolator = interpolator;
        this.defaultValue = defaultValue;
    }

    public InterpolatedFrameGenerator<T, V> setFrame(int tick, V value) {
        frames.put(tick, value);
        return this;
    }

    public InterpolatedFrameGenerator<T, V> setFrame(@Range(from = 0, to = 1) double progress, V value) {
        if (getEndTick() <= 0) {
            throw new IllegalStateException("Cannot set relative keyframe before endTick is set.");
        }

        int closestTick = (int) Math.clamp((((double) getEndTick()) * progress), 0, getEndTick());

        return setFrame(closestTick, value);
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
                .orElse(getStartTick());

        int nextFrame = frames.keySet().stream()
                .filter(tick -> tick > currentTick)
                .mapToInt(val -> val)
                .min()
                .orElse(getEndTick());

        V previousValue = frames.getOrDefault(previousFrame, defaultValue);
        V nextValue = frames.getOrDefault(nextFrame, previousValue);

        double progress = (double) (currentTick - previousFrame) / ((nextFrame - 1) - previousFrame);

        return interpolator.interpolate(previousValue, nextValue, progress);
    }

    @FunctionalInterface
    public interface Interpolator<T> {
        T interpolate(T previousValue, T nextValue, double progress);
    }
}
