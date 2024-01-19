package wbs.utils.util.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

/**
 * Represents an item that gets rendered in the world with a given
 * type, to allow for smooth transitions between different {@link ItemRenderer}s.
 */
@SuppressWarnings("unused")
@Deprecated
public class RenderedItem {

    /**
     * Represents a specific implementation of {@link ItemRenderer}
     */
    public enum RenderType {
        HEAD, HAND, SMALL_HEAD, SMALL_HAND;

        /**
         * Gets a new ItemRenderer based on the enum value,
         * created at the given location
         * @param loc The location to render the item
         * @return A new ItemRenderer
         */
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
    private ItemRenderer renderer;
    private RenderType renderType;

    /**
     * Create a new rendered item in the world, defaulting to
     * using a {@link HeadRenderer}
     * @param location The location to render the item
     */
    public RenderedItem(Location location) {
        renderer = new HeadRenderer(location);
        renderType = RenderType.HEAD;

        renderer.calculateActual();

        stand = (ArmorStand) renderer.getWorld().spawnEntity(renderer.getActualLocation(), EntityType.ARMOR_STAND);
        stand.setInvisible(true);
        stand.setMarker(true);
        stand.setInvulnerable(true);
        stand.setGravity(false);
        stand.setArms(true);

        renderer.setStand(stand);
        renderer.reposition();
    }

    /**
     * Change which renderer to use by using a RenderType
     * @param renderType The type of renderer to change to
     */
    public void setRenderType(RenderType renderType) {
        if (this.renderType == renderType) return;
        this.renderType = renderType;

        Location renderLocation = renderer.renderLocation;
        Vector facing = renderer.facing;
        Material material = renderer.getMaterial();

        renderer.setMaterial(Material.AIR);

        renderer = renderType.getRenderer(renderer.renderLocation);

        renderer.setStand(stand);

        renderer.setLocation(renderLocation);
        renderer.setFacing(facing);
        renderer.setMaterial(material);

        renderer.reposition();
    }

    /**
     * Set the type of item to render
     * @param material The item type to render
     */
    public void setMaterial(Material material) {
        renderer.setMaterial(material);
    }


    /**
     * Change this items location (direction ignored;
     * use {@link #setFacing(Vector)}
     * @param location The new position of this item
     */
    public void setLocation(Location location) {
        renderer.setLocation(location);
    }

    /**
     * Change the direction the item is facing
     * @param facing The new facing direction
     */
    public void setFacing(Vector facing) {
        renderer.setFacing(facing);
    }

    /**
     * Remove this item
     */
    public void remove() {
        stand.remove();
    }

    private void calculateActual() {
        renderer.calculateActual();
    }

    /**
     * Recalculate the position and facing of the item.
     */
    public void reposition() {
        renderer.reposition();
    }
}
