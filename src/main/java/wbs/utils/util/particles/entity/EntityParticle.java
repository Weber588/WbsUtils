package wbs.utils.util.particles.entity;

import com.google.common.collect.Table;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import wbs.utils.WbsUtils;
import wbs.utils.util.pluginhooks.PacketEventsWrapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.jetbrains.annotations.ApiStatus.OverrideOnly;

public class EntityParticle<T extends Entity> {
    protected final T entity;
    protected final boolean usePackets;
    protected final List<Player> viewers;
    protected final Map<Integer, Consumer<EntityParticle<T>>> keyframes;
    protected final Table<String, Integer, Consumer<EntityParticle<T>>> dynamicKeyframes;

    protected final int maxAge;
    protected int startTick = -1;
    protected boolean isSpawned = false;
    private int currentAge;

    public EntityParticle(T entity,
                          boolean usePackets,
                          int maxAge,
                          List<Player> viewers,
                          Map<Integer, Consumer<EntityParticle<T>>> keyframes,
                          Table<String, Integer, Consumer<EntityParticle<T>>> dynamicKeyframes) {
        this.entity = entity;
        this.usePackets = usePackets;
        this.maxAge = maxAge;
        this.viewers = viewers;
        this.keyframes = keyframes;
        this.dynamicKeyframes = dynamicKeyframes;
    }

    @Contract(mutates = "this")
    public void start() {
        spawn();

        runTick(0);

        startTick = Bukkit.getCurrentTick();
        WbsUtils.getInstance().runTimer(runnable -> {
            currentAge = Bukkit.getCurrentTick() - startTick;

            if (maxAge > 0 && currentAge >= maxAge) {
                if (usePackets) {
                    viewers.forEach(viewer -> {
                        PacketEventsWrapper.removeEntity(viewer, entity);
                    });
                } else {
                    entity.remove();
                }

                runnable.cancel();
                return;
            }

            runTick(currentAge);
        }, 1, 1);
    }

    private void runTick(int currentAge) {
        startTick(currentAge);

        playKeyframeIfPresent(currentAge);

        if (usePackets) {
            viewers.forEach(viewer -> {
                PacketEventsWrapper.updateEntity(viewer, entity);
            });
        }

        endTick(currentAge);
    }

    private void playKeyframeIfPresent(int currentAge) {
        Consumer<EntityParticle<T>> keyframe = keyframes.get(currentAge);
        if (keyframe != null) {
            beforeKeyframe(currentAge);
            playKeyframe(currentAge, keyframe);
            afterKeyframe(currentAge);
        }

        dynamicKeyframes.rowMap().forEach((key, column) -> {
            Consumer<EntityParticle<T>> dynamicKeyframe = column.get(currentAge);
            if (dynamicKeyframe != null) {
                dynamicKeyframe.accept(this);
            }
        });
    }

    @OverrideOnly
    protected void startTick(int currentAge) {

    }

    @OverrideOnly
    protected void beforeKeyframe(int currentAge) {

    }

    protected void playKeyframe(int currentFrame, Consumer<EntityParticle<T>> keyframe) {
        keyframe.accept(this);
    }

    @OverrideOnly
    protected void afterKeyframe(int currentAge) {

    }

    @OverrideOnly
    protected void endTick(int currentAge) {

    }

    private void spawn() {
        if (isSpawned) {
            throw new IllegalStateException("Already spawned!");
        }

        isSpawned = true;

        if (usePackets) {
            viewers.forEach(viewer -> {
                PacketEventsWrapper.showFakeEntity(viewer, entity);
            });
        } else {
            entity.getWorld().addEntity(entity);

            Collection<Player> worldViewers = entity.getChunk().getPlayersSeeingChunk();

            // Hide from everyone else
            worldViewers.forEach(worldViewer -> {
                if (!viewers.contains(worldViewer)) {
                    worldViewer.hideEntity(WbsUtils.getInstance(), entity);
                }
            });
        }
    }

    public T getEntity() {
        return entity;
    }

    public boolean usePackets() {
        return usePackets;
    }

    public EntityParticle<T> teleport(Location location) {
        if (usePackets) {
            viewers.forEach(viewer -> {
                PacketEventsWrapper.teleportEntity(viewer, entity, location);
            });
        } else {
            entity.teleport(location);
        }

        return this;
    }

    public int getAge() {
        return currentAge;
    }
}
