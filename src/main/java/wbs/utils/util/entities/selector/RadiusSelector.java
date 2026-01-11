package wbs.utils.util.entities.selector;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
public class RadiusSelector<T extends Entity> extends EntitySelector<T, RadiusSelector<T>> {

    public RadiusSelector(Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected @NotNull List<T> getSelection(Location loc) {
        World world = Objects.requireNonNull(loc.getWorld());

        BoundingBox boundingBox = BoundingBox.of(loc, range / 2, range / 2, range / 2);

        Collection<T> entities = filter(world.getNearbyEntities(boundingBox));

        Map<T, Double> selected = new HashMap<>();

        for (T check : entities) {
            if (!isValid(check)) continue;

            Location checkLocation = check.getBoundingBox().intersection(boundingBox).getCenter().toLocation(world);

            double distanceSquared = checkLocation.distanceSquared(loc);
            if (distanceSquared <= range * range) {
                selected.put(check, distanceSquared);
            }
        }

        List<T> orderedSelected = new LinkedList<>(selected.keySet());

        orderedSelected.sort(Comparator.comparingDouble(selected::get));

        return orderedSelected;
    }

    @Override
    protected RadiusSelector<T> getThis() {
        return this;
    }
}
