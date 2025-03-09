package wbs.utils.util;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.string.WbsStrings;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Quasi-extension to {@link WbsEnums} for classes implementing {@link Keyed}, to allow more flexibility
 * in certain configurations.
 */
@SuppressWarnings("unused")
public abstract class WbsKeyed {
	private WbsKeyed() {}

	public static boolean isValidNamespaceChar(char c) {
		return ((c >= 'a') && (c <= 'z')) || ((c >= '0') && (c <= '9')) || (c == '.') || (c == '_') || (c == '-');
	}

	public static boolean isValidKeyChar(char c) {
		return isValidNamespaceChar(c) || c == '/';
	}

	public static boolean isValidNamespace(String namespace) {
		if (namespace.isEmpty()) {
			return false;
		}

		for (char c : namespace.toCharArray()) {
			if (!isValidNamespaceChar(c)) {
				return false;
			}
		}

        return true;
    }

	public static boolean isValidKey(String key) {
		if (key.isEmpty()) {
			return false;
		}

		for (char c : key.toCharArray()) {
			if (!isValidKeyChar(c)) {
				return false;
			}
		}

        return true;
    }

	@Nullable
	public static NamespacedKey parseKey(@NotNull String keyString, @NotNull String defaultNamespace) {
        if (keyString.isEmpty() || keyString.length() > 32767) {
            return null;
        }

		if (defaultNamespace.isEmpty()) {
			return null;
		}

        String[] components = keyString.split(":", 3);
        String key;

        if (components.length == 1) {
            key = components[0];
            if (key.isEmpty() || !isValidKey(key)) {
                return null;
            }

            return new NamespacedKey(defaultNamespace, key);
        } else if (components.length == 2) {
			key = components[1];
			if (!isValidKey(key)) {
				return null;
			}

            String namespace = components[0];
            if (namespace.isEmpty()) {
                return new NamespacedKey(defaultNamespace, key);
            } else {
                if (!isValidNamespace(namespace)) {
                    return null;
                }

                return new NamespacedKey(namespace, key);
            }
        } else {
			return null;
		}
    }

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
		enumConstant = WbsEnums.getEnumFromString(clazz, string);
		if (enumConstant == null) {
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
		}

		return enumConstant;
	}

	public static String joiningPrettyStrings(@NotNull Class<? extends Keyed> clazz) {
		return joiningPrettyStrings(clazz, ", ");
	}

	public static String joiningPrettyStrings(@NotNull Class<? extends Keyed> clazz, String delimiter) {
		Registry<?> registry = Bukkit.getRegistry(clazz);
		if (registry == null) {
			throw new IllegalArgumentException("Not a valid registry: " + clazz);
		}

		return joiningPrettyStrings(registry, delimiter);
	}

	public static String joiningPrettyStrings(@NotNull Registry<? extends Keyed> registry) {
		return joiningPrettyStrings(registry, ", ");
	}

	public static String joiningPrettyStrings(@NotNull Registry<? extends Keyed> registry, String delimiter) {
		return registry.stream()
				.map(WbsKeyed::toPrettyString)
				.collect(Collectors.joining(delimiter));
	}

	public static String toPrettyString(Keyed keyed) {
		return WbsStrings.capitalizeAll(keyed.getKey().getKey().replaceAll("[_\\-]", " "));
	}

	/**
	 * A common method for all enums since they can't have another base class
	 * @param <T> The Keyed type to find a registry for.
	 * @param clazz The class that extends Keyed.
	 * @param string The string that represents the value of the keyed value to return.
	 * @return corresponding enum, or null
	 */
	public static <T extends Keyed> T getKeyedFromString(@NotNull Class<T> clazz, @NotNull String string) {
		Registry<T> registry = Bukkit.getRegistry(clazz);

		if (registry == null) {
			throw new IllegalArgumentException("Not a valid registry: " + clazz);
		}

		string = string.toUpperCase().replaceAll("[\\s-]", "_");

		NamespacedKey key = NamespacedKey.fromString(string);

		if (key == null) {
			return null;
		}

		return registry.get(key);
	}
}
