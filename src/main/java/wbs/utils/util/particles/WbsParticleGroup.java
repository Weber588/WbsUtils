package wbs.utils.util.particles;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Particle;

import wbs.utils.util.WbsMath;

/**
 * Class to support multiple WbsParticleEffects with predefined particles
 * running at the same time and location.
 * @author Weber588
 */
public class WbsParticleGroup {

	private Map<WbsParticleEffect, Particle> effects = new HashMap<>();
	private Map<WbsParticleEffect, Double> chances = new HashMap<>();

	public WbsParticleGroup addEffect(WbsParticleEffect effect, Particle particle, double chance) {
		effects.put(effect, particle);
		chances.put(effect, chance);
		return this;
	}
	
	public WbsParticleGroup addEffect(WbsParticleEffect effect, Particle particle) {
		return addEffect(effect, particle, 100);
	}

	/**
	 * Play all effects at a given location.
	 * @param location The location to play the effects at.
	 * if the effect is a line.
	 */
	public void play(Location location) {
		play(location, location);
	}
			
	/**
	 * Play all effects at a given location.
	 * @param location The location to play the effects at.
	 * @param finishLocation The location to use as the end point
	 * if the effect is a line.
	 */
	public void play(Location location, Location finishLocation) {
		for (WbsParticleEffect effect : effects.keySet()) {
			double chance = chances.get(effect);
			
			if (WbsMath.chance(chance)) {
				
				if (effect instanceof LineParticleEffect) {
					((LineParticleEffect) effect).play(effects.get(effect), location, finishLocation);
				} else {
					effect.play(effects.get(effect), location);
				}
			}
		}
	}

	/**
	 * Play a random effect from this group at the given location.
	 * @param location The location to play the effect at.
	 * @return The randomly chosen effect.
	 */
	public WbsParticleEffect playRandom(Location location) {
		return playRandom(location, location);
	}
	
	/**
	 * Play a random effect from this group at the given location.
	 * @param location The location to play the effect at.
	 * @param finishLocation The location to use as the end point
	 * if the effect is a line.
	 * @return The randomly chosen effect.
	 */
	public WbsParticleEffect playRandom(Location location, Location finishLocation) {
		int index = (int) (Math.random() * (effects.size() - 1));
		WbsParticleEffect[] possibleEffects = (WbsParticleEffect[]) effects.keySet().toArray();
		WbsParticleEffect effect = possibleEffects[index];

		if (effect instanceof LineParticleEffect) {
			((LineParticleEffect) effect).play(effects.get(effect), location, finishLocation);
		} else {
			effect.play(effects.get(effect), location);
		}
		
		return effect;
	}
	
	@Override
	public WbsParticleGroup clone() {
		WbsParticleGroup cloned = new WbsParticleGroup();

		for (WbsParticleEffect effect : effects.keySet()) {
			cloned.addEffect(effect, effects.get(effect));
		}
		
		return cloned;
	}
}
