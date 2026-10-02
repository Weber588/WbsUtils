package wbs.utils.util.particles.entity;

import com.google.common.collect.Multimap;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@NullMarked
public class DisplayParticle<T extends Display> extends EntityParticle<T> {
    public static final double MINIMUM_ANGULAR_SPEED = 0.001;

    @Nullable
    protected Vector angularVelocity = null; // Magnitude is speed (radians per tick), direction is axis of rotation
    protected double angularDrag = 0;
    @Nullable
    protected Vector rotationPivot = null;

    protected Matrix4f transformation = new Matrix4f();

    public DisplayParticle(T entity,
                           boolean usePackets,
                           int maxAge,
                           List<Player> viewers,
                           Multimap<String, Keyframe<T>> keyframes,
                           Multimap<String, Keyframe<T>> dynamicKeyframes) {
        super(entity, usePackets, maxAge, viewers, keyframes, dynamicKeyframes);
    }

    @Override
    protected void startTick(int currentAge) {
        if (angularVelocity != null && angularVelocity.length() > MINIMUM_ANGULAR_SPEED) {
            Transformation transformation = entity.getTransformation();

            double angularSpeed = angularVelocity.length();

            if (angularSpeed == 0) {
                this.transformation = new Matrix4f()
                        .translate(transformation.getTranslation())
                        .rotate(transformation.getLeftRotation())
                        .scale(transformation.getScale())
                        .rotate(transformation.getRightRotation());
            } else {

                Vector3f existingScale = transformation.getScale();
                Vector3f inverseScale = new Vector3f(
                        1f / existingScale.x,
                        1f / existingScale.y,
                        1f / existingScale.z
                );

                // Set matrix to the current transformation of the
                this.transformation = new Matrix4f()
                        .translate(transformation.getTranslation())
                        .rotate(transformation.getLeftRotation())
                        .scale(existingScale)
                        .rotate(transformation.getRightRotation());

                // Get this *after* setting the current details from the entity, in case it's not explicitly set and
                // needs to generate a default
                Vector pivot = rotationPivot();

                // Apply rotation physics
                this.transformation
                        .scale(inverseScale) // Revert scale to 1,1,1 avoid warping
                        .translate(pivot.toVector3f().mul(-1))
                        .rotate((float) angularSpeed, angularVelocity.toVector3f().normalize())
                        .translate(pivot.toVector3f())
                        .scale(existingScale); // Restore original scale
            }

            if (angularDrag > 0) {
                angularVelocity.normalize().multiply(Math.max(angularSpeed - (angularSpeed * angularDrag), MINIMUM_ANGULAR_SPEED));
            }
        }

        entity.setTransformationMatrix(transformation);
    }

    @Override
    protected void beforeKeyframe(int currentFrame) {

    }

    public DisplayParticle<T> setAngularVelocity(@Nullable Vector angularVelocity) {
        if (angularVelocity != null) {
            this.angularVelocity = angularVelocity.clone();
        } else {
            this.angularVelocity = null;
        }
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

    public DisplayParticle<T> editTransformation(Consumer<Matrix4f> modifier) {
        modifier.accept(transformation);
        return this;
    }

    public DisplayParticle<T> transformation(Matrix4f transformation) {
        this.transformation = new Matrix4f(transformation);
        return this;
    }

    public Matrix4f transformation() {
        return new Matrix4f(transformation);
    }

    public Vector rotationPivot() {
        return rotationPivot == null ? new Vector() : rotationPivot.clone();
    }

    public DisplayParticle<T> rotationPivot(@Nullable Vector rotationPivot) {
        this.rotationPivot = rotationPivot;
        return this;
    }
}
