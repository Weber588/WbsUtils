package wbs.utils.util;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class WbsRunnable extends BukkitRunnable {

	public void cancelSafely() {
		super.cancel();
		
		finish();
	}

	/**
	 * Called after cancel() is invoked.
	 * To be overridden for tasks that need 
	 * to clean up after themselves
	 */
	protected void finish() {
		
	}
}
