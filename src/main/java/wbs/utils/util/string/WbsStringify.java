package wbs.utils.util.string;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import wbs.utils.util.WbsEnums;
import wbs.utils.util.WbsMath;

@SuppressWarnings("unused")
public final class WbsStringify {
	private WbsStringify() {}

	public static String toString(ItemStack item) {
		return item.getAmount() + "x " + WbsEnums.toPrettyString(item.getType());
	}

	public static String toString(Location location, boolean includeWorld) {
		String asString = location.getBlockX() + ", " +
				location.getBlockY() + ", " +
				location.getBlockZ();

		if (includeWorld) {
			asString += ", " + Objects.requireNonNull(location.getWorld()).getName();
		}
		return asString;
	}

	/**
	 * Get the duration as a human readable string.
	 * @param duration The duration to convert
	 * @param longFormat Whether or not the duration is longer than 1 hour -
	 * not required, but supports up to years when long, but will show
	 * "259 minutes and 25.35 seconds" if not long and more than an hour.
	 * @return The duration as a string.
	 */
	public static String toString(Duration duration, boolean longFormat) {
		if (longFormat) {
			return longStringify(duration);
		}
		
		String prettyTime;
		double inMillis = duration.toMillis();

		prettyTime = WbsMath.roundTo((inMillis % 60000)/1000, 2) + " seconds";
		if (inMillis > 60000) {
			int minutes = ((int) inMillis/60000);
			if (minutes == 1 || minutes == 0) {
				prettyTime = minutes + " minute and " + prettyTime;
			} else {
				prettyTime = minutes + " minutes and " + prettyTime;
			}
		}
		
		return prettyTime;
	}


	public static String toString(LocalDateTime timeStamp) {
		String prettyTime;
		
		int year = timeStamp.getYear();
		int month = timeStamp.getMonthValue();
		int dayOfMonth = timeStamp.getDayOfMonth();
		int hour = timeStamp.getHour();
		String twelveHourDenominator;
		if (hour % 12 != hour) { // PM
			hour = hour - 12;
			twelveHourDenominator = "PM";
		} else {
			twelveHourDenominator = "AM";
		}
		if (hour == 0) {
			hour = 12;
		}
		
		int minute = timeStamp.getMinute();
		String minuteString = minute + "";
		if (minuteString.length() == 1) {
			minuteString = "0" + minuteString;
		}
		
		prettyTime =  dayOfMonth + "/" + month + "/" + year + " " + hour + ":" + minuteString + " " + twelveHourDenominator;
		
		return prettyTime;
	}
	
	private static final int maxYears = 100000;
	
	private static String longStringify(Duration duration) {
		Calendar from = new GregorianCalendar();
		LocalDateTime now = LocalDateTime.now();
		Date date = Date.from(now.minus(duration).atZone(ZoneId.systemDefault()).toInstant());
		from.setTime(date);
		
		Calendar to = new GregorianCalendar();
		date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
		to.setTime(date);
		
		return formatDateDiff(from, to);
	}
	
	/**
	 * The essentials method for date formatting,
	 * altered for utils.
	 * @author EssentialsX Team (https://github.com/EssentialsX/Essentials)
	 * @param fromDate The start date
	 * @param toDate The end date
	 * @return The formatted duration between the two dates
	 */
	private static String formatDateDiff(Calendar fromDate, Calendar toDate) {
	        boolean future = false;
	        if (toDate.equals(fromDate)) {
	            return "now";
	        }
	        if (toDate.after(fromDate)) {
	            future = true;
	        }
	        StringBuilder sb = new StringBuilder();
	        int[] types = new int[]{Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND};
	        String[] names = new String[]{"year", "years", "month", "months", "day", "days", "hour", "hours", "minute", "minutes", "second", "seconds"};
	        int accuracy = 0;
	        for (int i = 0; i < types.length; i++) {
	            if (accuracy > 2) {
	                break;
	            }
	            int diff = dateDiff(types[i], fromDate, toDate, future);
	            if (diff > 0) {
	                accuracy++;
	                sb.append(" ").append(diff).append(" ").append(names[i * 2 + (diff > 1 ? 1 : 0)]);
	            }
	        }
	        if (sb.length() == 0) {
	            return "now";
	        }
	        return sb.toString().trim();
	    }
	
	static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future) {
        int year = Calendar.YEAR;

        int fromYear = fromDate.get(year);
        int toYear = toDate.get(year);
        if (Math.abs(fromYear - toYear) > maxYears) {
            toDate.set(year, fromYear +
                    (future ? maxYears : -maxYears));
        }

        int diff = 0;
        long savedDate = fromDate.getTimeInMillis();
        while ((future && !fromDate.after(toDate)) || (!future && !fromDate.before(toDate))) {
            savedDate = fromDate.getTimeInMillis();
            fromDate.add(type, future ? 1 : -1);
            diff++;
        }
        diff--;
        fromDate.setTimeInMillis(savedDate);
        return diff;
    }
}
