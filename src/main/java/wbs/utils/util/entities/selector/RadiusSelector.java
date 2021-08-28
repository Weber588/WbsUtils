package wbs.utils.util.entities.selector;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
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
        Collection<T> entities = filter(world.getNearbyEntities(loc, range, range, range));

        Map<T, Double> selected = new HashMap<>();

        for (T check : entities) {
            if (!isValid(check)) continue;

            double distanceSquared = check.getLocation().distanceSquared(loc);
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
