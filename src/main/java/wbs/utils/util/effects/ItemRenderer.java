package wbs.utils.util.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;

import java.util.Objects;

abstract class ItemRenderer {
    protected double limbLength;
    protected Vector offset;
    protected double itemSize;

    protected Location renderLocation;
    protected Location actualLocation;
    protected World world;
    protected Vector facing;
    protected ArmorStand stand;
    protected Material material;

    public ItemRenderer(Location location) {
        setFacing(location.getDirection());
        setLocation(location);
    }

    protected abstract void calculateActual();
    public abstract void reposition();

    public void setStand(ArmorStand stand) {
        this.stand = stand;
    }

    public void setLocation(Location location) {
        renderLocation = location;
        world = Objects.requireNonNull(location.getWorld());
    }

    public void setFacing(Vector facing) {
        this.facing = facing;
    }

    public World getWorld() {
        return world;
    }

    public Location getActualLocation() {
        return actualLocation;
    }

    public void setMaterial(Material mat) {
        material = mat;
    }

    public Material getMaterial() {
        return material;
    }
}
