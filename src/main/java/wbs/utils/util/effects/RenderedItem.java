package wbs.utils.util.effects;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Objects;

@SuppressWarnings("unused")
public class RenderedItem {

    public enum RenderType {
        HEAD, HAND, SMALL_HEAD, SMALL_HAND;

        public ItemRenderer getRenderer(Location loc) {
            switch (this) {
                case HEAD:
                    return new HeadRenderer(loc);
                case HAND:
                    return new HandRenderer(loc);
                case SMALL_HEAD:
                    break;
                case SMALL_HAND:
                    break;
            }

            return new HeadRenderer(loc);
        }
    }

    private final ArmorStand stand;
    private ItemRenderer type;

    public RenderedItem(Location location) {
        type = new HeadRenderer(location);

        type.calculateActual();

        stand = (ArmorStand) type.getWorld().spawnEntity(type.getActualLocation(), EntityType.ARMOR_STAND);
        stand.setInvisible(true);
        stand.setMarker(true);
        stand.setInvulnerable(true);
        stand.setGravity(false);
        stand.setArms(true);

        type.setStand(stand);
        type.reposition();
    }

    public void setType(RenderType renderType) {
        Location renderLocation = type.renderLocation;
        Vector facing = type.facing;
        Material material = type.getMaterial();

        type.setMaterial(Material.AIR);

        type = renderType.getRenderer(type.renderLocation);

        type.setStand(stand);

        type.setLocation(renderLocation);
        type.setFacing(facing);
        type.setMaterial(material);

        type.reposition();
    }

    public void setMaterial(Material mat) {
        type.setMaterial(mat);
    }


    public void setLocation(Location location) {
        type.setLocation(location);
    }

    public void setFacing(Vector facing) {
        type.setFacing(facing);
    }

    public void remove() {
        stand.remove();
    }

    private void calculateActual() {
        type.calculateActual();
    }

    public void reposition() {
        type.reposition();
    }
}
