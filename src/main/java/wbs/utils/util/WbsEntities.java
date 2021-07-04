package wbs.utils.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public final class WbsEntities {
	
	private WbsEntities() {}
	
	public static boolean canSeeSky(LivingEntity entity) {
		if (entity.getWorld().hasStorm()) {
			return entity.getLocation().getBlock().getLightFromSky() == 15;
		}
		return false;
	}
	
	public static boolean isInMaterial(LivingEntity entity, Material type) {
		return entity.getLocation().getBlock().getType() == type;
	}
	
	public static boolean isInWater(LivingEntity entity) {
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
	
	public static Set<LivingEntity> getNearbyLivingSpherical(Location loc, double range) {
		Set<LivingEntity> excludeSet = new HashSet<>();
		return getNearbySpherical(loc, range, excludeSet, LivingEntity.class);
	}
	public static Set<LivingEntity> getNearbyLivingSpherical(Location loc, double range, LivingEntity exclude) {
		Set<LivingEntity> excludeSet = new HashSet<>();
		excludeSet.add(exclude);
		return getNearbySpherical(loc, range, excludeSet, LivingEntity.class);
	}
	
	public static Set<LivingEntity> getNearbyLivingSpherical(Location loc, double range, Set<LivingEntity> exclude) {
		return getNearbySpherical(loc, range, exclude, LivingEntity.class);
	}



	public static <T extends Entity> Set<T> getNearbySpherical(Location loc, double range, Class<T> clazz) {
		Set<T> excludeSet = new HashSet<>();

		return getNearbySpherical(loc, range, excludeSet, clazz);
	}

	public static <T extends Entity> Set<T> getNearbySpherical(Location loc, double range, T exclude, Class<T> clazz) {
		Set<T> excludeSet = new HashSet<>();
		excludeSet.add(exclude);

		return getNearbySpherical(loc, range, excludeSet, clazz);
	}
	
	/**
	 * Get all entities of a given LivingEntity subclass 
	 * within a spherical region of a given location.
	 * @param <T> The class of LivingEntity to be retrieved
	 * @param loc The center of the selection.
	 * @param radius The radius around the location to check in.
	 * @param exclude A set of entities to ignore
	 * @param clazz The type of LivingEntity to be retrieved
	 * @return A Set of LivingEntities within the radius specified with range.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Entity> Set<T> getNearbySpherical(Location loc, double radius, Set<T> exclude, Class<T> clazz) {
		World world = loc.getWorld();
		if (world == null) throw new IllegalArgumentException();
		Collection<Entity> entities = world.getNearbyEntities(loc, radius, radius, radius);
	
		Set<Entity> nearby = new HashSet<>();
		for (Entity targetEntity : entities) {
			if (targetEntity.getLocation().distance(loc) <= radius) {
				if (exclude == null || exclude.isEmpty()) {
					nearby.add(targetEntity);
				} else {
					if (!exclude.contains(targetEntity)) {
						nearby.add(targetEntity);
					}
				}
			}
		}

		Set<T> targets = new HashSet<>();
		
		if (!clazz.equals(Entity.class)) { // Don't need to repeat for this
			for (Entity filterEntity : nearby) {
				if (clazz.isInstance(filterEntity)) {
					targets.add((T) filterEntity);
				}
			}
		} else {
			for (Entity filterEntity : nearby) {
				targets.add((T) filterEntity);
			}
		}
		
		return targets;
	}
	
	public static Set<LivingEntity> getNearbyLiving(Location loc, double range) {
		Set<LivingEntity> excludeSet = new HashSet<>();
		return getNearby(loc, range, excludeSet, LivingEntity.class);
	}

	public static Set<LivingEntity> getNearbyLiving(Location loc, double range, LivingEntity exclude) {
		Set<LivingEntity> excludeSet = new HashSet<>();
		excludeSet.add(exclude);
		return getNearby(loc, range, excludeSet, LivingEntity.class);
	}
	
	public static Set<LivingEntity> getNearbyLiving(Location loc, double range, Set<LivingEntity> exclude) {
		return getNearby(loc, range, exclude, LivingEntity.class);
	}
		
	public static <T extends Entity> Set<T> getNearby(Location loc, double range, Class<T> clazz) {
		Set<T> excludeSet = new HashSet<>();
		
		return getNearby(loc, range, excludeSet, clazz);
	}
	public static <T extends Entity> Set<T> getNearby(Location loc, double range, T exclude, Class<T> clazz) {
		Set<T> excludeSet = new HashSet<>();
		excludeSet.add(exclude);

		return getNearby(loc, range, excludeSet, clazz);
	}

	/**
	 * Get all entities of a given LivingEntity subclass
	 * within a cuboid radius of a given location.
	 * For a spherical region, use {@link #getNearbySpherical(Location, double, Entity, Class)}
	 * @param <T> The class of LivingEntity to be retrieved
	 * @param loc The center of the selection.
	 * @param range The max cuboid distance around the location to check in.
	 * @param exclude A set of entities to ignore
	 * @param clazz The type of LivingEntity to be retrieved
	 * @return A Set of LivingEntities within the radius specified with range.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Entity> Set<T> getNearby(Location loc, double range, Set<T> exclude, Class<T> clazz) {
		World world = loc.getWorld();
		if (world == null) throw new IllegalArgumentException();
		Collection<Entity> entities = world.getNearbyEntities(loc, range, range, range);
	
		Set<LivingEntity> nearbyLiving = new HashSet<>();
		for (Entity targetEntity : entities) {
			if (targetEntity instanceof LivingEntity) {
				if (exclude == null || exclude.isEmpty()) {
					nearbyLiving.add((LivingEntity) targetEntity);
				} else {
					if (!exclude.contains(targetEntity)) {
						nearbyLiving.add((LivingEntity) targetEntity);
					}
				}
			}
		}

		Set<T> targets = new HashSet<>();
		
		if (!clazz.equals(LivingEntity.class)) { // Don't need to repeat for this
			for (LivingEntity filterEntity : nearbyLiving) {
				if (clazz.isInstance(filterEntity)) {
					targets.add((T) filterEntity);
				}
			}
		} else {
			for (LivingEntity filterEntity : nearbyLiving) {
				targets.add((T) filterEntity);
			}
		}
		
		return targets;
	}
	

	public static Set<LivingEntity> getNearbyLiving(LivingEntity entity, double range, boolean includeSelf) {
		return getNearby(entity, range, includeSelf, LivingEntity.class);
	}

	/**
	 * Get all entities of a given LivingEntity subclass 
	 * within a cuboid radius of a given location.
	 * For a spherical region, use #getNearbySphere
	 * @param entity The entity at the center of the selection.
	 * @param radius The cuboid radius around the location to check in.
	 * @param includeSelf Whether or not to include entity in the resulting Set.
	 * @param clazz The subclass of LivingEntity to check nearby entities for.
	 * @return A Set of LivingEntitys within the cuboid radius specified with range.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends LivingEntity> Set<T> getNearby(LivingEntity entity, double radius, boolean includeSelf, Class<T> clazz) {
		Location loc = entity.getLocation();

		Collection<Entity> entities = entity.getWorld().getNearbyEntities(loc, radius, radius, radius);
		
		Set<LivingEntity> nearbyLiving = new HashSet<>();
		for (Entity targetEntity : entities) {
			if (targetEntity instanceof LivingEntity) {
				if (includeSelf) {
					nearbyLiving.add((LivingEntity) targetEntity);
				} else {
					if (!targetEntity.equals(entity)) {
						nearbyLiving.add((LivingEntity) targetEntity);
					}
				}
			}
		}

		Set<T> targets = new HashSet<>();
		
		if (!clazz.equals(LivingEntity.class)) { // Don't need to repeat for this
			for (LivingEntity filterEntity : nearbyLiving) {
				if (clazz.isInstance(filterEntity)) {
					targets.add((T) filterEntity);
				}
			}
		} else {
			for (LivingEntity filterEntity : nearbyLiving) {
				targets.add((T) filterEntity);
			}
		}
		
		return targets;
	}
	
	public static Set<LivingEntity> getNearbyLivingSpherical(LivingEntity entity, double radius, boolean includeSelf) {
		return getNearbySpherical(entity, radius, includeSelf, LivingEntity.class);
	}
	
	/**
	 * Get all entities of a given LivingEntity subclass 
	 * within a sphere of a given location.
	 * For a cuboid region, use #getNearby
	 * @param entity The entity at the center of the selection.
	 * @param radius The radius around the location to check in.
	 * @param includeSelf Whether or not to include entity in the resulting Set.
	 * @param clazz The subclass of LivingEntity to check nearby entities for.
	 * @return A Set of LivingEntitys within the radius specified with range.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends LivingEntity> Set<T> getNearbySpherical(LivingEntity entity, double radius, boolean includeSelf, Class<T> clazz) {
		Location loc = entity.getLocation();
		Collection<Entity> entities = entity.getWorld().getNearbyEntities(loc, radius, radius, radius);
		
		Set<LivingEntity> nearbyLiving = new HashSet<>();
		for (Entity targetEntity : entities) {
			if (targetEntity instanceof LivingEntity) {
				if (targetEntity.getLocation().distance(loc) <= radius) {
					if (includeSelf) {
						nearbyLiving.add((LivingEntity) targetEntity);
					} else {
						if (!targetEntity.equals(entity)) {
							nearbyLiving.add((LivingEntity) targetEntity);
						}
					}
				}
			}
		}
		
		Set<T> targets = new HashSet<>();
		
		if (!clazz.equals(LivingEntity.class)) { // Don't need to repeat for this
			for (LivingEntity filterEntity : nearbyLiving) {
				if (clazz.isInstance(filterEntity)) {
					targets.add((T) filterEntity);
				}
			}
		} else {
			for (LivingEntity filterEntity : nearbyLiving) {
				targets.add((T) filterEntity);
			}
		}
		
		return targets;
	}

	/**
	 * Gets the location at the block the entity is looking at.
	 * @param range The maximum distance away from the entity the block may be
	 * @return The target location. null if there was no block in range
	 */
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
	 * Returns the location at which the head pivots.
	 * Useful for getting relative coordinates to the
	 * head using facing vector.
	 * @param player The player whose neck pos to get
	 * @return The neck position
	 */
	public static Location getNeckPosition(Player player) {
		return player.getEyeLocation().subtract(0, 0.2, 0);
	}


	/**
	 * Gets a unit vector in the direction the caster is facing
	 * @return The facing vector
	 */
	public static Vector getFacingVector(LivingEntity entity) {
		return getFacingVector(entity, 1);
	}

	/**
	 * Gets a vector in the direction the caster is facing scaled to the
	 * given magnitude
	 * @param magnitude The scale of the resulting vector
	 * @return The facing vector
	 */
	public static Vector getFacingVector(LivingEntity entity, double magnitude) {
		double x, y, z;
		double pitch = Math.toRadians(entity.getLocation().getPitch());
		double yaw = Math.toRadians(entity.getLocation().getYaw());

		y = (magnitude * Math.sin(0 - pitch));

		double planeMagnitude = Math.min(magnitude, Math.abs(y / Math.tan(0 - pitch)));

		x = planeMagnitude * Math.cos(yaw + (Math.PI/2));
		z = planeMagnitude * Math.sin(yaw + (Math.PI/2));

		return new Vector(x, y, z);
	}

	public static Vector getLocalUp(LivingEntity entity) {
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
	 * Pushes the caster in the direction they're facing at a given speed
	 * @param speed The new speed
	 */
	public static void push(LivingEntity entity, double speed) {
		push(entity, getFacingVector(entity, speed));
	}

	/**
	 * Pushes the caster according to a vector
	 * @param direction The new velocity vector
	 */
	public static void push(LivingEntity entity, Vector direction) {
		entity.setVelocity(direction);
	}
	

	/**
	 * Teleports the caster a given distance in the direction they're looking, ensuring
	 * a safe landing (cannot land in non-solid blocks)
	 * @param distance The distance to teleport
	 * @return true if the blink was successful, false if there was no safe landing spot
	 * found
	 */
	public static boolean blink(LivingEntity entity, double distance) {
		Location tryPos = entity.getLocation().clone().add(getFacingVector(entity, distance));
		Location tryPos2 = tryPos.clone().add(0, 1, 0);
		Material pos1Type = tryPos.getBlock().getType();
		Material pos2Type = tryPos.getBlock().getType();
		boolean failed = false;
		while ((pos1Type.isSolid() || pos2Type.isSolid()) && !failed) {
			tryPos.add(0, 1, 0);
			tryPos2.add(0, 1, 0);
			pos1Type = tryPos.getBlock().getType();
			pos2Type = tryPos.getBlock().getType();
			if (tryPos.distance(entity.getLocation()) > distance * 2) {
				failed = true;
			}
		}
		if (!failed) {
			entity.teleport(tryPos);
		}
		return (!failed);
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
	public static void damage(LivingEntity target, double damage, LivingEntity attacker) {
		if (target instanceof EnderDragon) {
			EnderDragon dragon = (EnderDragon) target;
			
			EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(attacker, target, DamageCause.MAGIC, damage);
			
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
	public static Location getMiddleLocation(LivingEntity target) {
		double middleHeight = target.getHeight()/2;
		Location middleLoc = target.getLocation();
		middleLoc.setY(middleLoc.getY() + middleHeight);
		return middleLoc;
	}
	
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
