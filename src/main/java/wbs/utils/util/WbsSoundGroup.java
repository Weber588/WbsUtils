package wbs.utils.util;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import wbs.utils.WbsUtils;

public class WbsSoundGroup implements Serializable {
	private static final long serialVersionUID = 4828609960725383015L;
	
	private ArrayList<WbsSound> allSounds = new ArrayList<>();
	private ArrayList<Long> delay = new ArrayList<>();
	
	public WbsSoundGroup(WbsSound ... sounds) {
		for (WbsSound sound : sounds) {
			addSound(sound, 0);
		}
	}
	
	/**
	 * Add a WbsSound to this sound group
	 * @param sound The sound to add
	 * @param ticks Optionally, the delay between this sound and the
	 * previously added one
	 */
	public void addSound(WbsSound sound, long ticks) {
		allSounds.add(sound);
		delay.add(ticks);
	}

	/**
	 * Add a WbsSound to this sound group
	 * @param sound The sound to add
	 */
	public void addSound(WbsSound sound) {
		allSounds.add(sound);
		delay.add(0L);
	}
	
	/**
	 * Play the sound.
	 * @param loc The location at which to play the sound
	 */
	public void play(Location loc) {
		playNext(loc, 0);
	}
	
	private void playNext(Location loc, int index) {
		if (allSounds.size() > index) {
			long tickDelay = delay.get(index);
			if (tickDelay == 0) { // Don't schedule if no delay
				
				allSounds.get(index).play(loc);
				playNext(loc, index + 1);
				
			} else {
				
				new BukkitRunnable() {
					@Override
					public void run() {
						allSounds.get(index).play(loc);
						playNext(loc, index + 1);
					}
				}.runTaskLater(WbsUtils.getInstance(), tickDelay);
				
			}
		}
	}
}