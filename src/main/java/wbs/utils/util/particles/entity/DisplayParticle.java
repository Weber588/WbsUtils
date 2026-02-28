package wbs.utils.util.particles.entity;

import com.google.common.collect.Table;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static wbs.utils.util.particles.entity.TextDisplayParticleBuilder.DEFAULT_TEXT_DISPLAY_HEIGHT;
import static wbs.utils.util.particles.entity.TextDisplayParticleBuilder.DUMB_TEXT_DISPLAY_FIX;

@NullMarked
public class DisplayParticle<T extends Display> extends EntityParticle<T> {
    public static final double MINIMUM_ANGULAR_SPEED = 0.001;
    protected boolean doDynamicTeleportDuration = false;
    protected boolean doDynamicInterpolationDuration = false;

    @Nullable
    protected Vector angularVelocity = null; // Magnitude is speed (radians per tick), direction is axis of rotation
    protected double angularDrag = 0;


    public DisplayParticle(T entity,
                           boolean usePackets,
                           int maxAge,
                           List<Player> viewers,
                           Map<Integer, Consumer<EntityParticle<T>>> keyframes,
                           Table<String, Integer, Consumer<EntityParticle<T>>> dynamicKeyframes) {
        super(entity, usePackets, maxAge, viewers, keyframes, dynamicKeyframes);
    }

    @Override
    protected void startTick(int currentAge) {
        if (angularVelocity != null && angularVelocity.length() > MINIMUM_ANGULAR_SPEED) {
            Transformation transformation = entity.getTransformation();
            Vector3f translation = transformation.getTranslation();
            Vector3f scale = transformation.getScale();

            double angularSpeed = angularVelocity.length();

            Quaternionf updatedRotation = transformation.getLeftRotation().rotateAxis((float) angularSpeed, angularVelocity.toVector3f());

            if (entity instanceof TextDisplay) {
                translation = new Vector3f(0, -1 / DEFAULT_TEXT_DISPLAY_HEIGHT / scale.y / 2, 0)
                        .add(DUMB_TEXT_DISPLAY_FIX)
                        .rotate(updatedRotation);
            }

            entity.setTransformation(new Transformation(
                    translation,
                    updatedRotation,
                    scale,
                    transformation.getRightRotation()
            ));

            if (angularDrag > 0) {
                angularVelocity.normalize().multiply(Math.max(angularSpeed - (angularSpeed * angularDrag), MINIMUM_ANGULAR_SPEED));
            }
        }
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
            entity.setTeleportDuration(Math.clamp(nextFrame - currentFrame, 0, DisplayParticleBuilder.MAX_TP_DURATION));
        }
    }

    public int getNextKeyframe(int currentFrame, int lastKeyframe) {
        int nextFrame = lastKeyframe;
        for (int i = currentFrame + 1; i <= lastKeyframe; i++) {
            if (keyframes.containsKey(i)) {
                nextFrame = i;
                break;
            }
        }
        return nextFrame;
    }

    public int getLastKeyframe() {
        return keyframes.keySet().stream().mapToInt(val -> val).max().orElseThrow();
    }

    public DisplayParticle<T> doDynamicTeleportDuration(boolean doDynamicTeleportDuration) {
        this.doDynamicTeleportDuration = doDynamicTeleportDuration;
        return this;
    }

    public DisplayParticle<T> doDynamicInterpolationDuration(boolean doDynamicInterpolationDuration) {
        this.doDynamicInterpolationDuration = doDynamicInterpolationDuration;
        return this;
    }

    public DisplayParticle<T> setAngularVelocity(@Nullable Vector angularVelocity) {
        this.angularVelocity = angularVelocity;
        return this;
    }

    public DisplayParticle<T> setAngularSpeed(double angularSpeed) {
        if (angularVelocity != null) {
            angularVelocity.normalize().multiply(angularSpeed);
        }

        return this;
    }

    public DisplayParticle<T> setAngularDrag(double angularDrag) {
        this.angularDrag = angularDrag;
        return this;
    }

    public @Nullable Vector getAngularVelocity() {
        if (angularVelocity != null) {
            return angularVelocity.clone();
        }
        return null;
    }

    public double getAngularSpeed() {
        if (angularVelocity != null) {
            return angularVelocity.length();
        }
        return 0;
    }

    public double getAngularDrag() {
        return angularDrag;
    }
}
