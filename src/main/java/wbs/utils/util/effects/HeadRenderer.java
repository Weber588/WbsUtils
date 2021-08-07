package wbs.utils.util.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import wbs.utils.util.WbsMath;

import java.util.Objects;

/**
 * An ItemRenderer that uses an {@link org.bukkit.entity.ArmorStand}'s head slot
 */
class HeadRenderer extends ItemRenderer {

    public HeadRenderer(Location location) {
        super(location);
        limbLength = 0.25;
        offset = new Vector(0, 1.44, 0);
        itemSize = 0.8;
    }

    @Override
    protected void calculateActual() {
        actualLocation = renderLocation.clone().subtract(offset);

        double y = facing.getY();

        Vector locationOffset = facing.clone().normalize().multiply(limbLength);

        locationOffset =
                WbsMath.rotateFrom(
                        locationOffset,
                        new Vector(0, 1, 0),
                        new Vector(facing.getX(), 0, facing.getZ())
                );


        actualLocation.subtract(locationOffset);
        actualLocation.setDirection(facing);
    }

    @Override
    public void reposition() {
        calculateActual();

        double x = facing.getX();
        double y = facing.getY();
        double z = facing.getZ();

        double XZDistance = Math.sqrt(x * x + z * z);
        double pitch = XZDistance == 0 ? y / Math.abs(y) : Math.atan(y / XZDistance);

        double theta = Math.atan2(-x, z);
        double yaw = (theta + Math.PI * 2) % (Math.PI * 2);

        stand.setHeadPose(new EulerAngle(-pitch, 0, 0));

        stand.teleport(actualLocation);
    }

    @Override
    public void setMaterial(Material mat) {
        super.setMaterial(mat);
        Objects.requireNonNull(stand.getEquipment()).setHelmet(new ItemStack(mat));
    }
}
