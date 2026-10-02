package wbs.utils.util.particles.entity;

import com.google.common.collect.LinkedHashMultimap;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Range;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import wbs.utils.WbsUtils;

import java.util.List;
import java.util.function.Consumer;

@NullMarked
public class DisplayParticleBuilder<T extends Display> extends EntityParticleBuilder<T> {
    public static final int MAX_TP_DURATION = 59;

    @Range(from = 0, to = 59)
    protected int teleportDuration = 0;
    protected int interpolationDuration = 0;

    // Transformation components
    protected Matrix4f transformation = new Matrix4f();

    @Nullable
    protected Vector angularVelocity = null; // Magnitude is speed, direction is axis of rotation
    protected double angularDrag = 0;
    @Nullable
    protected Vector rotationPivot = null;

    public DisplayParticleBuilder(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    protected EntityParticle<T> buildInternal(T entity, List<Player> viewers) {
        return new DisplayParticle<>(entity, usePackets, maxAge, viewers, LinkedHashMultimap.create(this.keyframes), LinkedHashMultimap.create(this.dynamicKeyframes))
                .transformation(transformation)
                .rotationPivot(rotationPivot)
                .setAngularVelocity(angularVelocity)
                .setAngularDrag(angularDrag)
                .setTickForce(tickForce != null ? tickForce.clone() : null)
                .setDrag(drag)
                .doBlockCollisions(doBlockCollisions);
    }

    @Override
    protected void configure(T display) {
        display.setInterpolationDuration(interpolationDuration);
        display.setTeleportDuration(teleportDuration);

        display.setTransformationMatrix(transformation);

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

    public DisplayParticleBuilder<T> setAngularVelocity(@Nullable Vector angularVelocity) {
        this.angularVelocity = angularVelocity;
        return this;
    }

    public DisplayParticleBuilder<T> setAngularDrag(double angularDrag) {
        this.angularDrag = angularDrag;
        return this;
    }

    public DisplayParticleBuilder<T> editTransformation(Consumer<Matrix4f> modifier) {
        modifier.accept(transformation);
        return this;
    }

    public Matrix4f transformation() {
        return new Matrix4f(transformation);
    }

    public DisplayParticleBuilder<T> transformation(Matrix4f transformation) {
        this.transformation = transformation;
        return this;
    }

    public Vector rotationPivot() {
        return rotationPivot == null ? new Vector() : rotationPivot.clone();
    }

    public DisplayParticleBuilder<T> rotationPivot(@Nullable Vector rotationPivot) {
        this.rotationPivot = rotationPivot;
        return this;
    }
}
