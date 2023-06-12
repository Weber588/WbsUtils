package wbs.utils.util;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * A collection of time-related utilities.
 * <br/>
 * Some methods in {@link wbs.utils.util.string.WbsStringify} are planned to be moved into this class.
 */
@SuppressWarnings("unused")
public final class WbsTime {
	private WbsTime() {}

	public static Duration timeLeft(LocalDateTime start, Duration after) {
		LocalDateTime untilTime = start.plus(after);

		return Duration.between(LocalDateTime.now(), untilTime);
	}
}
