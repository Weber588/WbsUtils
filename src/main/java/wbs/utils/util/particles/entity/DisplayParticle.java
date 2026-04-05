package wbs.utils.util.particles.entity;

import com.google.common.collect.Multimap;
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

import static wbs.utils.util.particles.entity.TextDisplayParticleBuilder.*;

@NullMarked
public class DisplayParticle<T extends Display> extends EntityParticle<T> {
    public static final double MINIMUM_ANGULAR_SPEED = 0.001;

    @Nullable
    protected Vector angularVelocity = null; // Magnitude is speed (radians per tick), direction is axis of rotation
    protected double angularDrag = 0;

    // Transformation components
    protected Vector3f translation = new Vector3f();
    protected Quaternionf rightRotation = new Quaternionf();
    protected Vector3f scale = new Vector3f();
    protected Quaternionf leftRotation = new Quaternionf();

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
            Vector3f scale = transformation.getScale();

            double angularSpeed = angularVelocity.length();

            Quaternionf updatedRotation = leftRotation.rotateAxis((float) angularSpeed, angularVelocity.toVector3f());

            if (entity instanceof TextDisplay) {
                setTranslation(new Vector3f(0, -1 / DEFAULT_TEXT_DISPLAY_HEIGHT / scale.y / 2, 0)
                        .add(DUMB_TEXT_DISPLAY_FIX)
                        .rotate(updatedRotation));
            }

            setLeftRotation(updatedRotation);

            if (angularDrag > 0) {
                angularVelocity.normalize().multiply(Math.max(angularSpeed - (angularSpeed * angularDrag), MINIMUM_ANGULAR_SPEED));
            }
        }

        entity.setTransformation(new Transformation(
                translation,
                rightRotation,
                scale,
                leftRotation
        ));
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

    public DisplayParticle<T> setTranslation(Vector3f translation) {
        this.translation = translation;
        return this;
    }

    public DisplayParticle<T> setRightRotation(Quaternionf rightRotation) {
        this.rightRotation = rightRotation;
        return this;
    }

    public DisplayParticle<T> setScale(Vector3f scale) {
        this.scale = new Vector3f(scale).mul(SCALE_TO_SQUARE);
        return this;
    }

    public DisplayParticle<T> setScale(float scale) {
        return setScale(new Vector3f(scale));
    }

    public DisplayParticle<T> setLeftRotation(Quaternionf leftRotation) {
        this.leftRotation = leftRotation;
        return this;
    }
}
