package wbs.utils.util.particles.entity;

import com.google.common.collect.HashBasedTable;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import wbs.utils.WbsUtils;

import java.util.HashMap;
import java.util.List;

@NullMarked
public class DisplayParticleBuilder<T extends Display> extends EntityParticleBuilder<T> {
    public static final int MAX_TP_DURATION = 59;
    protected boolean doDynamicTeleportDuration = false;
    protected boolean doDynamicInterpolationDuration = false;

    @Range(from = 0, to = 59)
    protected int teleportDuration = 0;
    protected int interpolationDuration = 0;

    // Transformation components
    protected Vector3f translation = new Vector3f();
    protected Quaternionf rightRotation = new Quaternionf();
    protected Vector3f scale = new Vector3f();
    protected Quaternionf leftRotation = new Quaternionf();

    @Nullable
    protected Vector angularVelocity = null; // Magnitude is speed, direction is axis of rotation
    protected double angularDrag = 0;

    public DisplayParticleBuilder(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    protected @NotNull EntityParticle<T> buildInternal(T entity, List<Player> viewers) {
        return new DisplayParticle<>(entity, usePackets, maxAge, viewers, new HashMap<>(this.keyframes), HashBasedTable.create(this.dynamicKeyframes))
                .doDynamicTeleportDuration(doDynamicTeleportDuration)
                .doDynamicInterpolationDuration(doDynamicInterpolationDuration)
                .setAngularVelocity(angularVelocity != null ? angularVelocity.clone() : null)
                .setAngularDrag(angularDrag)
                .setTickForce(tickForce != null ? tickForce.clone() : null)
                .setDrag(drag)
                .doBlockCollisions(doBlockCollisions);
    }

    @Override
    protected void configure(T display) {
        display.setInterpolationDuration(interpolationDuration);
        display.setTeleportDuration(teleportDuration);

        // Set interpolation as time to first non-zero keyframe
        keyframes.keySet().stream().mapToInt(val -> val).filter(val -> val > 0).min().ifPresent(firstKeyframe -> {
            if (doDynamicInterpolationDuration) {
                display.setInterpolationDuration(firstKeyframe);
            }

            if (doDynamicTeleportDuration) {
                display.setTeleportDuration(Math.min(firstKeyframe, MAX_TP_DURATION));
            }
        });

        display.setTransformation(new Transformation(
                translation,
                rightRotation,
                scale,
                leftRotation
        ));

        super.configure(display);
    }

    public DisplayParticleBuilder<T> setTeleportDuration(int teleportDuration) {
        if (teleportDuration < 0 || teleportDuration > MAX_TP_DURATION) {
            WbsUtils.getInstance().getLogger().warning("Invalid teleport duration " + teleportDuration
                    + ". Must be between 0 and " + MAX_TP_DURATION + " inclusive.");
            //noinspection CallToPrintStackTrace
            new RuntimeException().printStackTrace();
            teleportDuration = Math.clamp(teleportDuration, 0, MAX_TP_DURATION);
        }
        this.teleportDuration = teleportDuration;
        return this;
    }

    public DisplayParticleBuilder<T> setInterpolationDuration(int interpolationDuration) {
        this.interpolationDuration = interpolationDuration;
        return this;
    }

    public DisplayParticleBuilder<T> setTranslation(Vector3f translation) {
        this.translation = translation;
        return this;
    }

    public DisplayParticleBuilder<T> setRightRotation(Quaternionf rightRotation) {
        this.rightRotation = rightRotation;
        return this;
    }

    public DisplayParticleBuilder<T> setScale(Vector3f scale) {
        this.scale = scale;
        return this;
    }

    public DisplayParticleBuilder<T> setLeftRotation(Quaternionf leftRotation) {
        this.leftRotation = leftRotation;
        return this;
    }

    public DisplayParticleBuilder<T> setDoDynamicTeleportDuration(boolean doDynamicTeleportDuration) {
        this.doDynamicTeleportDuration = doDynamicTeleportDuration;
        return this;
    }

    public DisplayParticleBuilder<T> setDoDynamicInterpolationDuration(boolean doDynamicInterpolationDuration) {
        this.doDynamicInterpolationDuration = doDynamicInterpolationDuration;
        return this;
    }

    public DisplayParticleBuilder<T> setDoDynamicDurations(boolean doDynamicDuration) {
        this.doDynamicInterpolationDuration = doDynamicDuration;
        this.doDynamicTeleportDuration = doDynamicDuration;
        return this;
    }

    public DisplayParticleBuilder<T> setAngularVelocity(@Nullable Vector angularVelocity) {
        this.angularVelocity = angularVelocity;
        return this;
    }

    public DisplayParticleBuilder<T> setAngularDrag(double angularDrag) {
        this.angularDrag = angularDrag;
        return this;
    }
}
