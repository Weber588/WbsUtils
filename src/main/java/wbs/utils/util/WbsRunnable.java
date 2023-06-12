package wbs.utils.util;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Extension of {@link BukkitRunnable} to create a "finish" method that runs after {@link BukkitRunnable#cancel()}.
 * @deprecated Use {@link wbs.utils.util.plugin.WbsPlugin} threading utilities instead.
 */
@Deprecated
public abstract class WbsRunnable extends BukkitRunnable {

	@Override
	public void cancel() {
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
