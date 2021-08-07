package wbs.utils.util.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import wbs.utils.util.WbsMath;

import java.util.Objects;

/**
 * An ItemRenderer that uses an {@link org.bukkit.entity.ArmorStand}'s hand
 */
class HandRenderer extends ItemRenderer {

    public HandRenderer(Location location) {
        super(location);
        limbLength = 0.3;
        offset = new Vector(0.25, 1.85, 0.8);
        itemSize = 0.8;
    }

    @Override
    protected void calculateActual() {

        double x = facing.getX();
        double y = facing.getY();
        double z = facing.getZ();

        double yaw = Math.atan2(x, z);

        actualLocation = renderLocation.clone()
                .subtract(
                        WbsMath.rotateVector(offset, new Vector(0, 1, 0), yaw)
                );

        Vector locationOffset = facing.clone().normalize().multiply(limbLength);

        locationOffset =
                WbsMath.rotateFrom(
                        locationOffset,
                        new Vector(0, 1, 0),
                        new Vector(facing.getX(), 0, facing.getZ())
                );

        locationOffset.multiply(-1);
        locationOffset.setY(locationOffset.getY() * -1);

        actualLocation.add(locationOffset);

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

        stand.setRightArmPose(new EulerAngle(pitch, 0, 0));

        stand.teleport(actualLocation);
    }

    @Override
    public void setMaterial(Material mat) {
        super.setMaterial(mat);
        Objects.requireNonNull(stand.getEquipment()).setItemInMainHand(new ItemStack(mat));
    }
}
