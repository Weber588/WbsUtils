package wbs.utils.util.particles.entity;

import com.google.common.collect.Table;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DisplayParticle<T extends Display> extends EntityParticle<T> {
    protected boolean doDynamicTeleportDuration = false;
    protected boolean doDynamicInterpolationDuration = false;

    public DisplayParticle(T entity,
                           boolean usePackets,
                           int maxAge,
                           List<Player> viewers,
                           Map<Integer, Consumer<EntityParticle<T>>> keyframes,
                           Table<String, Integer, Consumer<EntityParticle<T>>> dynamicKeyframes) {
        super(entity, usePackets, maxAge, viewers, keyframes, dynamicKeyframes);
    }

    @Override
    protected void beforeKeyframe(int currentFrame) {
        if (!doDynamicInterpolationDuration && !doDynamicTeleportDuration) {
            return;
        }

        int lastKeyframe = getLastKeyframe();

        if (currentFrame >= lastKeyframe) {
            lastKeyframe = maxAge - 1;
        }

        int nextFrame = getNextKeyframe(currentFrame, lastKeyframe);

        if (doDynamicInterpolationDuration) {
            // TODO: Figure out why this works differently with packets
            entity.setInterpolationDuration(nextFrame - currentFrame);
        }
        if (doDynamicTeleportDuration) {
            entity.setTeleportDuration(nextFrame - currentFrame);
        }
    }

    private int getNextKeyframe(int currentFrame, int lastKeyframe) {
        int nextFrame = lastKeyframe;
        for (int i = currentFrame + 1; i <= lastKeyframe; i++) {
            if (keyframes.containsKey(i)) {
                nextFrame = i;
                break;
            }
        }
        return nextFrame;
    }

    private int getLastKeyframe() {
        return keyframes.keySet().stream().mapToInt(val -> val).max().orElseThrow();
    }

    DisplayParticle<T> doDynamicTeleportDuration(boolean doDynamicTeleportDuration) {
        this.doDynamicTeleportDuration = doDynamicTeleportDuration;
        return this;
    }

    DisplayParticle<T> doDynamicInterpolationDuration(boolean doDynamicInterpolationDuration) {
        this.doDynamicInterpolationDuration = doDynamicInterpolationDuration;
        return this;
    }
}
