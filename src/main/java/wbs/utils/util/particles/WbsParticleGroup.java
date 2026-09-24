package wbs.utils.util.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.WbsMath;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class to support multiple WbsParticleEffects with predefined particles
 * running at the same time and location.
 */
public class WbsParticleGroup {

	private final Map<WbsParticleEffect, Particle> effects = new HashMap<>();
	private final Map<WbsParticleEffect, Double> chances = new HashMap<>();
	private boolean perEffectChance = false;
	@NotNull
	private PlayLine linePlay = ((effect, location, finishLocation, particle) -> {
		effect.play(particle, location, finishLocation);
	});
	@NotNull
	private Play play = ((effect, location, particle) -> {
		effect.play(particle, location);
	});

	public WbsParticleGroup setLinePlayFunction(@NotNull PlayLine linePlay) {
		this.linePlay = linePlay;
		return this;
	}

	public WbsParticleGroup setPlayFunction(@NotNull Play play) {
		this.play = play;
		return this;
	}

	public boolean perEffectChance() {
		return perEffectChance;
	}

	/**
	 * When true, the contained effects are mutated to always have chance matching the chance
	 * configured in the group, and then run 100% of the time. This allows the effect to handle chances
	 * instead of the group.
	 * @param perEffectChance The new value of the chance behavior
	 */
	public WbsParticleGroup perEffectChance(boolean perEffectChance) {
		this.perEffectChance = perEffectChance;
		return this;
	}

	/**
	 * Add an effect to play with a given chance
	 * @param effect The effect to play
	 * @param particle The particle to use when playing the given effect
	 * @param chance The chance for the
	 * @return The same particle group
	 */
	public WbsParticleGroup addEffect(WbsParticleEffect effect, Particle particle, double chance) {
		effects.put(effect, particle);
		chances.put(effect, chance);
		return this;
	}

	/**
	 * Adds a copy of all effects from another particle group
	 * @param other The particle group to copy effects from
	 * @return The same particle group
	 */
	public WbsParticleGroup addEffects(WbsParticleGroup other) {
		Set<WbsParticleEffect> effects = other.effects.keySet();

		for (WbsParticleEffect effect : effects) {
			Particle particle = other.effects.get(effect);
			double chance = other.chances.getOrDefault(effect, 0d);
			addEffect(effect, particle, chance);
		}

		return this;
	}

	/**
	 * Add an effect to play 100% of the time
	 * @param effect The effect to play
	 * @param particle The particle to use when playing the given effect
	 * @return The same particle group
	 */
	public WbsParticleGroup addEffect(WbsParticleEffect effect, Particle particle) {
		return addEffect(effect, particle, 100);
	}

	/**
	 * Builds and then plays all effects at a given location.
	 * @param location The location to play the effects at.
	 */
	public void buildAndPlay(Location location) {
		buildAndPlay(location, location);
	}

	/**
	 * Builds and then plays all effects at a given location.
	 * @param location The location to play the effects at.
	 * @param finishLocation The location to use as the end point
	 * if the effect is a line.
	 */
	public void buildAndPlay(Location location, Location finishLocation) {
		for (WbsParticleEffect effect : effects.keySet()) {
			double chance = chances.get(effect);

			if (perEffectChance || WbsMath.chance(chance)) {
				Particle particle = effects.get(effect);
				if (perEffectChance) {
					effect.setChance(chance);
				}
				effect.build();
				if (effect instanceof LineParticleEffect lineEffect) {
					linePlay.playLine(lineEffect, location, finishLocation, particle);
				} else {
					play.play(effect, location, particle);
				}
			}
		}
	}

	private void play(Location location, WbsParticleEffect effect) {
		effect.play(effects.get(effect), location);
	}

	/**
	 * Play all effects at a given location.
	 * @param location The location to play the effects at.
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

			if (perEffectChance || WbsMath.chance(chance)) {
				Particle particle = effects.get(effect);
				if (perEffectChance) {
					effect.setChance(chance);
				}
				if (effect instanceof LineParticleEffect lineEffect) {
					linePlay.playLine(lineEffect, location, finishLocation, particle);
				} else {
					play.play(effect, location, particle);
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
		WbsParticleEffect[] possibleEffects =  effects.keySet().toArray(WbsParticleEffect[]::new);
		WbsParticleEffect effect = possibleEffects[index];

		Particle particle = effects.get(effect);
		if (effect instanceof LineParticleEffect lineEffect) {
			linePlay.playLine(lineEffect, location, finishLocation, particle);
		} else {
			play.play(effect, location, particle);
		}
		
		return effect;
	}
	
	@Override
	public WbsParticleGroup clone() {
		WbsParticleGroup cloned = new WbsParticleGroup();

		for (WbsParticleEffect effect : effects.keySet()) {
			Double chance = chances.get(effect);
			if (chance != null) {
				cloned.addEffect(effect, effects.get(effect), chance);
			} else {
				cloned.addEffect(effect, effects.get(effect));
			}
		}
		
		return cloned;
	}

	public Map<WbsParticleEffect, Particle> effects() {
		return Map.copyOf(effects);
	}

	@FunctionalInterface
	public interface PlayLine {
		void playLine(LineParticleEffect effect, Location location, Location finishLocation, Particle particle);
	}

	@FunctionalInterface
	public interface Play {
		void play(WbsParticleEffect effect, Location location, Particle particle);
	}
}
