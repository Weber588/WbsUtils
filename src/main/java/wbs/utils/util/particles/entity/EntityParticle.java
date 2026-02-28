package wbs.utils.util.particles.entity;

import com.github.retrooper.packetevents.util.reflection.Reflection;
import com.google.common.collect.Table;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;
import wbs.utils.WbsUtils;
import wbs.utils.util.pluginhooks.PacketEventsWrapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.jetbrains.annotations.ApiStatus.OverrideOnly;

public class EntityParticle<T extends Entity> {
    public static final double MINIMUM_SPEED = 0.001;

    protected final T entity;
    protected final boolean usePackets;
    protected final List<Player> viewers;
    protected final Map<Integer, Consumer<EntityParticle<T>>> keyframes;
    protected final Table<String, Integer, Consumer<EntityParticle<T>>> dynamicKeyframes;

    protected boolean doBlockCollisions = false;

    protected final int maxAge;
    protected int startTick = -1;
    protected boolean isSpawned = false;
    private int currentAge;

    @Nullable
    protected Vector tickForce = null; // Applied to the entity every tick
    protected double drag = 0;

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
        if (tickForce != null && tickForce.length() > 0) {
            entity.setVelocity(entity.getVelocity().add(tickForce));
        }

        if (entity.getVelocity().length() > MINIMUM_SPEED) {
            Vector velocity = entity.getVelocity();

            if (usePackets || (entity instanceof Display && PacketEventsWrapper.isActive())) {
                directlyProcessPhysics(velocity);
                velocity = entity.getVelocity();
            }

            double speed = velocity.length();
            if (drag > 0 && speed > MINIMUM_SPEED) {
                Vector newVelocity = velocity.normalize().multiply(Math.max(speed - (speed * drag), MINIMUM_SPEED));
                entity.setVelocity(newVelocity);
            }
        }

        startTick(currentAge);

        playKeyframeIfPresent(currentAge);

        if (usePackets) {
            viewers.forEach(viewer -> {
                PacketEventsWrapper.updateEntity(viewer, entity);
            });
        }

        endTick(currentAge);
    }

    protected void directlyProcessPhysics(Vector velocity) {
        Location location = entity.getLocation();

        Location newLocation;

        if (doBlockCollisions) {
            RayTraceResult rayTraceResult = location.getWorld().rayTraceBlocks(location, velocity, velocity.length());

            if (rayTraceResult != null) {
                newLocation = rayTraceResult.getHitPosition().toLocation(location.getWorld());
                entity.setVelocity(new Vector(0, 0, 0));
            } else {
                newLocation = location.add(velocity);
            }
        } else {
            newLocation = location.add(velocity);
        }

        try {
            Object handle = Reflection.getMethod(entity.getClass(), "getHandle").invoke(entity);

            Method setPosRawMethod = Class.forName("net.minecraft.world.entity.Entity")
                    .getMethod("setPosRaw", double.class, double.class, double.class, boolean.class);

            setPosRawMethod.invoke(handle, newLocation.getX(), newLocation.getY(), newLocation.getZ(), false);
        } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        viewers.forEach(viewer -> {
            PacketEventsWrapper.updateEntityPosition(viewer, entity);
        });
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

    public EntityParticle<T> setTickForce(@Nullable Vector tickForce) {
        this.tickForce = tickForce;
        return this;
    }

    public EntityParticle<T> setDrag(double drag) {
        this.drag = drag;
        return this;
    }

    public EntityParticle<T> doBlockCollisions(boolean doBlockCollisions) {
        this.doBlockCollisions = doBlockCollisions;
        return this;
    }
}
