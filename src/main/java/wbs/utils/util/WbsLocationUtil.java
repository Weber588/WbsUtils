package wbs.utils.util;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import wbs.utils.WbsUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class WbsLocationUtil {
    public static Set<Block> getNearbyBlocks(Location center, double size) {
        Set<Block> nearby = new HashSet<>();

        int halfSize = (int) Math.ceil(size / 2);
        for (double x = center.x() - halfSize; x <= center.x() + halfSize; x++) {
            for (double y = center.y() - halfSize; y <= center.y() + halfSize; y++) {
                for (double z = center.z() - halfSize; z <= center.z() + halfSize; z++) {
                    Block block = center.getWorld().getBlockAt((int) x, (int) y, (int) z);

                    nearby.add(block);
                }
            }
        }

        return nearby;
    }

    public static Set<Block> getNearbyBlocksSphere(Location center, double radius) {
        return getNearbyBlocks(center, radius).stream()
                .filter(block -> block.getLocation().distance(center) <= radius)
                .collect(Collectors.toSet());
    }

    public static Set<Block> getIntersectingBlocks(BoundingBox boundingBox, Location center) {
        Set<Block> intersecting = new HashSet<>();
        final double halfX = boundingBox.getWidthX() / 2;
        final double halfY = boundingBox.getHeight() / 2;
        final double halfZ = boundingBox.getWidthZ() / 2;

        for (double x = -halfX; x <= halfX; x = Math.min(halfX, x + 1)) {
            for (double y = -halfY; y <= halfY; y = Math.min(halfY, y + 1)) {
                for (double z = -halfZ; z <= halfZ; z = Math.min(halfZ, z + 1)) {
                    Location location = center.clone().add(x, y, z);
                    intersecting.add(location.getBlock());
                    if (z == halfZ) break;
                }
                if (y == halfY) break;
            }
            if (x == halfX) break;
        }

        return intersecting;
    }
}
