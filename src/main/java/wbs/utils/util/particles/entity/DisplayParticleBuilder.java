package wbs.utils.util.particles.entity;

import com.google.common.collect.HashBasedTable;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;

@NullMarked
public class DisplayParticleBuilder<T extends Display> extends EntityParticleBuilder<T> {
    protected boolean doDynamicTeleportDuration = false;
    protected boolean doDynamicInterpolationDuration = false;

    protected int teleportDuration = 0;
    protected int interpolationDuration = 0;

    // Transformation components
    protected Vector3f translation = new Vector3f();
    protected Quaternionf rightRotation = new Quaternionf();
    protected Vector3f scale = new Vector3f();
    protected Quaternionf leftRotation = new Quaternionf();

    public DisplayParticleBuilder(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    protected @NotNull EntityParticle<T> buildInternal(T entity, List<Player> viewers) {
        return new DisplayParticle<>(entity, usePackets, maxAge, viewers, new HashMap<>(this.keyframes), HashBasedTable.create(this.dynamicKeyframes))
                .doDynamicTeleportDuration(doDynamicTeleportDuration)
                .doDynamicInterpolationDuration(doDynamicInterpolationDuration);
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
                display.setTeleportDuration(firstKeyframe);
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
}
