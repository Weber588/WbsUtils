package wbs.utils.util.particles.entity;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import wbs.utils.WbsUtils;
import wbs.utils.util.particles.entity.interpolation.DynamicKeyframeGenerator;
import wbs.utils.util.particles.entity.interpolation.InterpolatedFrameGenerator;
import wbs.utils.util.particles.entity.interpolation.KeyframeGenerator;
import wbs.utils.util.particles.entity.interpolation.ParticleValueSupplier;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
@NullMarked
public class EntityParticleBuilder<T extends Entity> {
    public static List<Player> getViewers(Location location, @Nullable Player player) {
        List<Player> viewers = new LinkedList<>();

        Collection<Player> playersSeeingChunk = location.getChunk().getPlayersSeeingChunk();

        if (player != null) {
            viewers.add(player);
        } else {
            viewers.addAll(playersSeeingChunk);
        }
        return viewers;
    }

    protected final Class<T> entityClass;
    protected boolean usePackets = true;
    protected Consumer<T> configurationConsumer = t -> {};
    protected int maxAge = -1;

    protected boolean doBlockCollisions = false;

    @Nullable
    protected Vector tickForce = null; // Applied to the entity every tick
    protected double drag = 0;

    protected final Map<Integer, Consumer<EntityParticle<T>>> keyframes = new HashMap<>();
    protected final Table<String, Integer, ?> interpolatedFrames = HashBasedTable.create();
    protected final Table<String, Integer, Consumer<EntityParticle<T>>> dynamicKeyframes = HashBasedTable.create();

    public EntityParticleBuilder(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public EntityParticleBuilder<T> configure(Consumer<T> configurationConsumer) {
        this.configurationConsumer = configurationConsumer;
        return this;
    }

    public EntityParticle<T> buildParticle(Location location) {
        return buildParticle(location, null);
    }
    public EntityParticle<T> buildParticle(Location location, @Nullable Player onlyPlayer) {
        T entity = location.getWorld().createEntity(location, entityClass);

        configure(entity);

        List<Player> viewers = getViewers(location, onlyPlayer);

        return buildInternal(entity, viewers);
    }

    protected void configure(T t) {
        configurationConsumer.accept(t);

        t.setPersistent(false);
    }

    protected @NotNull EntityParticle<T> buildInternal(T entity, List<Player> viewers) {
        return new EntityParticle<>(entity, usePackets, maxAge, viewers, new HashMap<>(this.keyframes), HashBasedTable.create(this.dynamicKeyframes))
                .setTickForce(tickForce != null ? tickForce.clone() : null)
                .setDrag(drag)
                .doBlockCollisions(doBlockCollisions);
    }

    public EntityParticle<T> playParticle(Location location) {
        return playParticle(location, null);
    }
    public EntityParticle<T> playParticle(Location location, @Nullable Player onlyPlayer) {
        EntityParticle<T> built = buildParticle(location, onlyPlayer);

        built.start();

        return built;
    }

    /**
     * @param usePackets If this particle should use packets to create a fake entity, or false if the entity will really be spawned into the world
     * @return The same object
     */
    public EntityParticleBuilder<T> usePackets(boolean usePackets) {
        this.usePackets = usePackets;
        return this;
    }

    public EntityParticleBuilder<T> setMaxAge(int maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public EntityParticleBuilder<T> setKeyframe(int tick, Consumer<EntityParticle<T>> keyframe) {
        Preconditions.checkArgument(tick >= 0, "tick cannot be negative.");
        Preconditions.checkArgument(tick < maxAge, "tick must be less than maxAge");

        this.keyframes.put(tick, keyframe);
        return this;
    }

    public EntityParticleBuilder<T> setKeyframe(@Range(from = 0, to = 1) double progress, Consumer<EntityParticle<T>> keyframe) {
        Preconditions.checkArgument(progress >= 0, "progress cannot be negative.");
        Preconditions.checkArgument(progress <= 1, "progress must be less than or equal to 1");

        if (maxAge <= 0) {
            throw new IllegalStateException("Cannot set relative keyframe before maxAge is set.");
        }

        int closestTick = (int) Math.clamp((((double) maxAge - 1) * progress), 0, maxAge);

        prependKeyframe(closestTick, keyframe);
        return this;
    }

    public EntityParticleBuilder<T> setEntityKeyframe(int tick, Consumer<? super T> keyframe) {
        return setKeyframe(tick, particle -> keyframe.accept(particle.getEntity()));
    }

    public EntityParticleBuilder<T> appendKeyframe(int tick, Consumer<EntityParticle<T>> runAfterExisting) {
        Consumer<EntityParticle<T>> currentKeyframe = this.keyframes.get(tick);

        if (currentKeyframe != null) {
            Consumer<EntityParticle<T>> existingKeyframe = currentKeyframe;
            currentKeyframe = particle -> {
                existingKeyframe.accept(particle);
                runAfterExisting.accept(particle);
            };
        } else {
            currentKeyframe = runAfterExisting;
        }

        return setKeyframe(tick, currentKeyframe);
    }

    public EntityParticleBuilder<T> prependKeyframe(int tick, Consumer<EntityParticle<T>> runAfterExisting) {
        Consumer<EntityParticle<T>> currentKeyframe = this.keyframes.get(tick);

        if (currentKeyframe != null) {
            Consumer<EntityParticle<T>> existingKeyframe = currentKeyframe;
            currentKeyframe = particle -> {
                runAfterExisting.accept(particle);
                existingKeyframe.accept(particle);
            };
        } else {
            currentKeyframe = runAfterExisting;
        }

        setKeyframe(tick, currentKeyframe);
        return this;
    }

    public <V> DynamicKeyframeGenerator<T, V> buildDynamicKeyframes(ParticleValueSupplier<V> supplier) {
        return new DynamicKeyframeGenerator<>(supplier, maxAge);
    }

    public <V> DynamicKeyframeGenerator<T, V> buildDynamicKeyframes(Function<Double, V> supplier) {
        return new DynamicKeyframeGenerator<>(supplier, maxAge);
    }

    public <V> InterpolatedFrameGenerator<T, V> buildInterpolatedKeyframes(InterpolatedFrameGenerator.Interpolator<V> interpolator, V defaultValue) {
        return new InterpolatedFrameGenerator<>(maxAge, interpolator, defaultValue);
    }

    public <V> EntityParticleBuilder<T> fillKeyframes(String key, KeyframeGenerator<?, T, V> builder) {
        this.dynamicKeyframes.rowMap().remove(key);

        builder.generate().forEach((tick, keyframe) -> {
            dynamicKeyframes.put(key, tick, keyframe);
        });
        return this;
    }

    public <V> EntityParticleBuilder<T> removeDynamicKeyframes(String key) {
        this.dynamicKeyframes.rowMap().remove(key);
        return this;
    }

    public EntityParticleBuilder<T> setTickForce(@Nullable Vector tickForce) {
        this.tickForce = tickForce;
        return this;
    }

    public EntityParticleBuilder<T> setDrag(double drag) {
        this.drag = drag;
        return this;
    }

    public EntityParticleBuilder<T> doBlockCollisions(boolean doBlockCollisions) {
        this.doBlockCollisions = doBlockCollisions;
        return this;
    }
}
