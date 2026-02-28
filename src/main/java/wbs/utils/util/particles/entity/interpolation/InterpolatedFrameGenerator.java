package wbs.utils.util.particles.entity.interpolation;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;
import wbs.utils.WbsUtils;
import wbs.utils.util.WbsMath;
import wbs.utils.util.particles.entity.EntityParticle;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    public Map<Integer, Consumer<EntityParticle<T>>> generate() {
        Map<Integer, Consumer<EntityParticle<T>>> generated = super.generate();

        /*
        WbsUtils.getInstance().getLogger().info(
                "Frames: " + frames.keySet().stream().sorted().map(frame ->
                        frame + " (" + frames.get(frame) + ")"
                ).collect(Collectors.joining(", "))
        );
         */

        return generated;
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
        //    WbsUtils.getInstance().getLogger().info( "EXPLICIT FRAME: " + currentTick + ": " + explicitKeyframe);
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

        double progress = (double) (currentTick - previousFrame) / ((nextFrame) - previousFrame);

        V interpolated = interpolator.interpolate(previousValue, nextValue, progress);

        /*
        if (interpolated instanceof Number number) {
            WbsUtils.getInstance().getLogger().info( previousFrame + " (" + previousValue + ")\t" +
                    "< " + currentTick + " (" + WbsMath.roundTo(number.doubleValue(), 2) + ") >\t"
                    + nextFrame + " (" + nextValue + ")\t" +
                    "(" + (WbsMath.roundTo(progress * 100, 2)) + "%)");
        }
         */
        return interpolated;
    }

    @FunctionalInterface
    public interface Interpolator<T> {
        T interpolate(T previousValue, T nextValue, double progress);
    }
}
