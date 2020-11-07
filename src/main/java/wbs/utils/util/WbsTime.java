package wbs.utils.util;

import java.time.Duration;
import java.time.LocalDateTime;

public final class WbsTime {
	private WbsTime() {
		
	}

	public static Duration timeLeft(LocalDateTime start, Duration after) {
		LocalDateTime untilTime = start.plus(after);

		return Duration.between(LocalDateTime.now(), untilTime);
	}
}
