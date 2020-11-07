package wbs.utils.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WbsSound implements Serializable {
	
	private static final long serialVersionUID = 6055541956675411491L;
	
	private Sound sound;
	private float pitch = 1;
	private float volume = 1;

	public WbsSound(Sound sound) {
		this.sound = sound;
	}
	public WbsSound(Sound sound, float pitch) {
		this.sound = sound;
		this.pitch = pitch;
	}
	public WbsSound(Sound sound, float pitch, float volume) {
		this.sound = sound;
		this.pitch = pitch;
		this.volume = volume;
	}
	
	/**
	 * Play the sound.
	 * @param loc The location at which to play the sound
	 */
	public void play(Location loc) {
		World world = loc.getWorld();

		if (world != null) {
			world.playSound(loc, sound, volume, pitch);
		}
	}
	
	/**
	 * Set the pitch to play the sound at.
	 * @param pitch The desired pitch
	 */
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	/**
	 * Set the volume to play the sound at.
	 * @param volume The desired volume
	 */
	public void setVolume(float volume) {
		this.volume = volume;
	}
	
	
}
