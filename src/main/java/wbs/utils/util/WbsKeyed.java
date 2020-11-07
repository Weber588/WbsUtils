package wbs.utils.util;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

public abstract class WbsKeyed {

	private WbsKeyed() {}

	/**
	 * Gets the enum as represented by the given string. Failing that,
	 * if the enum implements Keyed, it will attempt to find a key that
	 * is represented by the string.
	 * @param clazz The class of the enum to attempt to get a value from. May or
	 *              may not extend {@link Keyed}
	 * @param string A string that may or may not represent a value of the enum,
	 *               or the Keyed equivalent if that fails.
	 * @return The value of the enum or Keyed field if it exists and is represented by the string.
	 */
	public static <T extends Enum<T>> T getEnumFromKeyed(Class<T> clazz, String string) {
		T enumConstant;
		try {
			enumConstant = WbsEnums.getEnumFromString(clazz, string);
		} catch (IllegalArgumentException ex) {
			if (Keyed.class.isAssignableFrom(clazz)) { // If clazz extends/implements Keyed
				for (T constant : clazz.getEnumConstants()) {
					try {
						if (((Keyed) constant).getKey().getKey().equalsIgnoreCase(string)) {
							return constant;
						}
					} catch (IllegalArgumentException ignored) {
						// Just continue
					}
				}
			}
			return null;
		}

		return enumConstant;
	}
}
