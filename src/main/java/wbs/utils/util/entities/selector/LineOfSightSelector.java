package wbs.utils.util.entities.selector;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unused")
public class LineOfSightSelector<T extends Entity> extends EntitySelector<T, LineOfSightSelector<T>> {
    public LineOfSightSelector(Class<T> clazz) {
        super(clazz);
    }

    @NotNull
    protected FluidCollisionMode fluidCollisionMode = FluidCollisionMode.NEVER;
    @Nullable
    protected Vector direction = null;
    protected boolean ignorePassableBlocks = true;
    protected double raySize = 1;

    @Override
    protected @NotNull List<T> getSelection(Location loc) {
        World world = Objects.requireNonNull(loc.getWorld());
        List<T> found = new LinkedList<>();

        Vector checkedDirection = direction;
        if (checkedDirection == null) {
            checkedDirection = loc.getDirection();
        }

        RayTraceResult result = world.rayTrace(loc, checkedDirection, range, fluidCollisionMode, ignorePassableBlocks, raySize, getRawPredicate());

        if (result == null || result.getHitEntity() == null) {
            return found;
        }

        @SuppressWarnings("unchecked")
        T target = (T) result.getHitEntity();
        found.add(target);

        return found;
    }

    @Override
    protected LineOfSightSelector<T> getThis() {
        return this;
    }

    public @NotNull FluidCollisionMode getFluidCollisionMode() {
        return fluidCollisionMode;
    }

    public LineOfSightSelector<T> setFluidCollisionMode(@NotNull FluidCollisionMode fluidCollisionMode) {
        this.fluidCollisionMode = fluidCollisionMode;
        return this;
    }

    public @Nullable Vector getDirection() {
        return direction;
    }

    public LineOfSightSelector<T> setDirection(@Nullable Vector direction) {
        this.direction = direction;
        return this;
    }

    public boolean isIgnorePassableBlocks() {
        return ignorePassableBlocks;
    }

    public LineOfSightSelector<T> setIgnorePassableBlocks(boolean ignorePassableBlocks) {
        this.ignorePassableBlocks = ignorePassableBlocks;
        return this;
    }

    public double getRaySize() {
        return raySize;
    }

    public LineOfSightSelector<T> setRaySize(double raySize) {
        this.raySize = raySize;
        return this;
    }
}
