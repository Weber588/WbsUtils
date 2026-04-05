package wbs.utils.util.particles.entity;

import com.google.common.collect.*;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import wbs.utils.util.particles.entity.interpolation.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

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

    protected final Multimap<String, Keyframe<T>> keyframes = LinkedHashMultimap.create();
    protected final Multimap<String, Keyframe<T>> dynamicKeyframes = LinkedHashMultimap.create();

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
        return new EntityParticle<>(entity, usePackets, maxAge, viewers, HashMultimap.create(this.keyframes), HashMultimap.create(this.dynamicKeyframes))
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

    public <V> EntityParticleBuilder<T> setSimpleKeyframeGroup(String group, ParticleSetter<T, V> setter, Map<Integer, V> frames) {
        this.keyframes.removeAll(group);

        frames.forEach((frame, value) -> {
            this.keyframes.put(group, Keyframe.particle(frame,particle -> setter.set(particle, value)));
        });

        return this;
    }

    public <V> EntityParticleBuilder<T> setSimpleKeyframeGroupProgress(String group, ParticleSetter<T, V> setter, Map<Double, V> frames) {
        this.keyframes.removeAll(group);

        frames.forEach((frame, value) -> {
            this.keyframes.put(group, Keyframe.particle(frame,particle -> setter.set(particle, value)));
        });

        return this;
    }

    public EntityParticleBuilder<T> setKeyframe(String group, Keyframe<T> keyframe) {
        clearKeyframes(group);
        this.keyframes.put(group, keyframe);
        return this;
    }

    public EntityParticleBuilder<T> addKeyframe(String group, Keyframe<T> keyframe) {
        this.keyframes.put(group, keyframe);
        return this;
    }

    public EntityParticleBuilder<T> clearKeyframes(String group) {
        this.keyframes.removeAll(group);
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
        this.dynamicKeyframes.removeAll(key);

        builder.generate().forEach(keyframe -> {
            dynamicKeyframes.put(key, keyframe);
        });
        return this;
    }

    public EntityParticleBuilder<T> removeDynamicKeyframes(String key) {
        this.dynamicKeyframes.removeAll(key);
        return this;
    }

    public EntityParticleBuilder<T> setTickForce(@Nullable Vector tickForce) {
        if (tickForce != null) {
            this.tickForce = tickForce.clone();
        } else {
            this.tickForce = null;
        }
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
