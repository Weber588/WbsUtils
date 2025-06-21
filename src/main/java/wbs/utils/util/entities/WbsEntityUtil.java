package wbs.utils.util.entities;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.WbsUtils;
import wbs.utils.util.WbsLocationUtil;
import wbs.utils.util.WbsMath;
import wbs.utils.util.string.WbsStringify;
import wbs.utils.util.string.WbsStrings;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public final class WbsEntityUtil {
    private WbsEntityUtil() {}

    public static boolean canSeeSky(Entity entity) {
        return entity.getLocation().getBlock().getLightFromSky() == 15;
    }

    public static boolean isInMaterial(Entity entity, Material type) {
        return entity.getLocation().getBlock().getType() == type;
    }

    public static boolean isInWater(Entity entity) {
        Block block = entity.getLocation().getBlock();
        if (block.getType() == Material.WATER) {
            return true;
        }
        BlockData data = block.getBlockData();
        if (data instanceof Waterlogged) {
            return ((Waterlogged) data).isWaterlogged();
        }

        return false;
    }


    /**
     * Gets the location at the block the entity is looking at.
     * @param range The maximum distance away from the entity the block may be
     * @return The target location. null if there was no block in range
     */
    @Nullable
    public static Location getTargetPos(LivingEntity entity, double range) {
        World world = entity.getWorld();
        RayTraceResult result = world.rayTraceBlocks(entity.getEyeLocation(), getFacingVector(entity), range);
        if (result == null) {
            return null;
        }

        Vector hitPoint = result.getHitPosition();
        Location loc = entity.getLocation();

        loc.setX(hitPoint.getX());
        loc.setY(hitPoint.getY());
        loc.setZ(hitPoint.getZ());

        return loc;
    }


    /**
     * Gets a unit vector in the direction the entity is facing
     * @return The facing vector
     */
    @NotNull
    public static Vector getFacingVector(Entity entity) {
        return getFacingVector(entity, 1);
    }

    /**
     * Gets a vector in the direction the entity is facing scaled to the
     * given magnitude
     * @param magnitude The scale of the resulting vector
     * @return The facing vector
     */
    @NotNull
    public static Vector getFacingVector(Entity entity, double magnitude) {
        double x, y, z;
        double pitch = Math.toRadians(entity.getLocation().getPitch());
        double yaw = Math.toRadians(entity.getLocation().getYaw());

        double planeMagnitude;
        if (pitch == 0) {
            y = 0;
            planeMagnitude = magnitude;
        } else {
            y = (magnitude * Math.sin(0 - pitch));
            planeMagnitude = Math.min(magnitude, Math.abs(y / Math.tan(0 - pitch)));
        }
        x = planeMagnitude * Math.cos(yaw + (Math.PI/2));
        z = planeMagnitude * Math.sin(yaw + (Math.PI/2));

        return new Vector(x, y, z);
    }

    @NotNull
    public static Vector getLocalUp(Entity entity) {
        double x, y, z;
        double pitch = entity.getLocation().getPitch();
        boolean reverse = (pitch <= 0);
        pitch -= 90;
        pitch = Math.toRadians(pitch);
        double yaw = Math.toRadians(entity.getLocation().getYaw());


        y = (Math.sin(0 - pitch));

        double planeMagnitude = Math.abs(y / Math.tan(0 - pitch));
        if (pitch == 0) {
            planeMagnitude = 1;
        }

        if (reverse) {
            planeMagnitude = -1 * planeMagnitude;
        }
        x = planeMagnitude * Math.cos(yaw + (Math.PI/2));
        z = planeMagnitude * Math.sin(yaw + (Math.PI/2));

        return new Vector(x, y, z);
    }

    /**
     * Pushes the entity in the direction they're facing at a given speed
     * @param speed The new speed
     * @param entity The entity to push
     */
    public static void push(Entity entity, double speed) {
        entity.setVelocity(getFacingVector(entity, speed));
    }

    @Nullable
    public static Block getSafeLocation(Entity entity, Location near, double maxDistance) {
        return getSafeLocation(entity, near, maxDistance, BlockFace.UP.getDirection());
    }

    @Nullable
    public static Block getSafeLocation(Entity entity, Location near,  double maxDistance, Vector direction) {
        BoundingBox boundingBox = entity.getBoundingBox();

        direction = direction.clone().normalize();

        double distanceChecked = 0;
        Vector offset = new Vector(0, 0, 0);
        Block safeLocation = null;
        Logger logger = WbsUtils.getInstance().getLogger();
        do {
            Location check = near.clone().add(offset);

            Set<Block> intersectingBlocks = WbsLocationUtil.getIntersectingBlocks(boundingBox, check);

            if (intersectingBlocks.stream().allMatch(Block::isPassable)) {
                safeLocation = check.getBlock();
            }

            offset = offset.add(direction);
            distanceChecked++;
        } while (safeLocation == null && distanceChecked < maxDistance);

        return safeLocation;
    }


    /**
     * Teleports the entity a given distance in the direction they're looking, ensuring
     * a safe landing (cannot land in non-solid blocks)
     * @param distance The distance to teleport
     * @return true if the blink was successful, false if there was no safe landing spot
     * found
     */
    public static boolean blink(Entity entity, double distance) {
        Location tryPos = entity.getLocation().clone().add(getFacingVector(entity, distance));
        Block safePos = getSafeLocation(entity, tryPos, distance * 2);

        if (safePos != null) {
            return entity.teleport(safePos.getLocation());
        }

        return false;
    }

    /**
     * Make the specified player deal damage to a living entity.
     * <p>
     * This method uses {@link org.bukkit.entity.Damageable#damage(double, Entity)},
     * except it works on Ender Dragons because they're awful to work with.
     * @param target The entity to damage
     * @param damage The amount of damage to deal
     * @param attacker The attacking entity to deal the damage. Nullable.
     */
    public static void damage(Damageable target, double damage, LivingEntity attacker) {
        if (target instanceof EnderDragon) {
            EnderDragon dragon = (EnderDragon) target;

            EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(attacker, target, EntityDamageEvent.DamageCause.MAGIC, damage);

            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            // Credits to 469512345 on spigot.com
            dragon.setHealth(dragon.getHealth() - damage);
            dragon.playEffect(EntityEffect.HURT);
            dragon.getWorld().playSound(dragon.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 100, 1);

            dragon.setLastDamageCause(event);
        } else {
            target.damage(damage, attacker);
        }
    }

    /**
     * Heal the target LivingEntity safely, taking into account max health
     * @param entity The entity to heal
     * @param toHeal The amount to heal
     * @return True if the entity was healed, false if already at max health
     */
    public static boolean heal(LivingEntity entity, double toHeal) {
        double currentHealth = entity.getHealth();
        AttributeInstance healthInstance = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthInstance == null) {
            throw new IllegalArgumentException("Invalid entity; health attribute was null");
        }
        double maxHealth = healthInstance.getBaseValue();

        if (currentHealth < maxHealth) {
            currentHealth += toHeal;

            entity.setHealth(Math.min(currentHealth, maxHealth));
        } else {
            return false;
        }

        return true;
    }

    /**
     * Get the location half way up a mob
     * @param target The target mob
     * @return The location half way up the given mob.
     */
    @NotNull
    public static Location getMiddleLocation(Entity target) {
        double middleHeight = target.getHeight()/2;
        Location middleLoc = target.getLocation();
        middleLoc.setY(middleLoc.getY() + middleHeight);
        return middleLoc;
    }

    @Nullable
    public static ItemStack getItemInSlot(LivingEntity entity, EquipmentSlot slot) {
        EntityEquipment equipment = entity.getEquipment();

        if (equipment == null)
            throw new IllegalArgumentException("The given entity did not have EntityEquipment.");

        switch (slot) {
            case CHEST:
                return equipment.getChestplate();
            case FEET:
                return equipment.getBoots();
            case HAND:
                return equipment.getItemInMainHand();
            case HEAD:
                return equipment.getHelmet();
            case LEGS:
                return equipment.getLeggings();
            case OFF_HAND:
                return equipment.getItemInOffHand();
            default:
                break;
        }
        return null;
    }
}
