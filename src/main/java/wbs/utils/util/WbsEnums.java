package wbs.utils.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import org.jetbrains.annotations.Nullable;
import wbs.utils.util.string.WbsStrings;

/**
 * Enum-related utilities, including for configuration and rendering.
 */
@SuppressWarnings("unused")
public final class WbsEnums {
	private WbsEnums() {}

	private static Map<Class<? extends Enum<?>>, String[]> stringArrays = new HashMap<>();

	public static String joiningPrettyStrings(Class<? extends Enum<?>> type) {
		return joiningPrettyStrings(type, ", ");
	}

	public static String joiningPrettyStrings(Class<? extends Enum<?>> type, String delimiter) {
		return Arrays.stream(type.getEnumConstants())
				.map(WbsEnums::toPrettyString)
				.collect(Collectors.joining(delimiter));
	}
	
	public static String toPrettyString(Enum<?> enumConstant) {
		return WbsStrings.capitalizeAll(enumConstant.name().replace('_', ' '));
	}
	
	/**
	 * Get all enum constants in a string array entirely in lower case.
	 * @param type The enum class to convert
	 * @return An array of strings representing each enum constant
	 */
	public static String[] toStringArray(Class<? extends Enum<?>> type) {
		if (stringArrays.containsKey(type)) {
			return stringArrays.get(type);
		}

		String[] typeArray = Arrays.stream(type.getEnumConstants())
				.map(Enum::toString)
				.map(String::toLowerCase)
				.toArray(String[]::new);
		stringArrays.put(type, typeArray);
		return typeArray;
	}
	
	/**
	 * Same as {@link #toStringArray(Class)} but as a List
	 */
	public static List<String> toStringList(Class<? extends Enum<?>> type) {
		return Arrays.asList(toStringArray(type));
	}
	
	/**
	 * A common method for all enums since they can't have another base class
	 * @param <T> Enum type
	 * @param clazz Enum class
	 * @param string The string that represents the value of the enum to return
	 * @return corresponding enum, or null
	 */
	@Nullable
	public static <T extends Enum<T>> T getEnumFromString(Class<T> clazz, String string) {
		
		string = string.toUpperCase();
		string = string.replaceAll(" ", "_");
		string = string.replaceAll("-", "_");

		T value;
		try {
			value = Enum.valueOf(clazz, string.trim().toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			return null;
		}

		return value;
	}


	/**
	 * Converts from a string to a GameMode Enum.
	 * @param from The string to convert
	 * @return The GameMode enum. Returns null if the string could not be parsed.
	 */
	public static GameMode gameModeFromString(String from) {
		for (String modeString : toStringArray(GameMode.class)) {
			if (modeString.equalsIgnoreCase(from)) {
				return GameMode.valueOf(modeString.toUpperCase());
			}
		}
		return null;
	}

	/**
	 * Converts from a string to a Particle Enum.
	 * @param from The string to convert
	 * @return The Particle enum. Returns null if the string could not be parsed.
	 */
	public static Particle particleFromString(String from) {
		for (String particleString : toStringArray(Particle.class)) {
			if (particleString.equalsIgnoreCase(from)) {
				return Particle.valueOf(particleString.toUpperCase());
			}
		}
		return null;
	}

	/**
	 * Same as {@link #toStringArray(Class)} but returns null if not found.
	 */
	public static Material materialFromString(String from) {
		return materialFromString(from, null);
	}

	/**
	 * Converts from a string to a Material Enum.
	 * @param from The string to convert
	 * @param defaultMaterial The default if the string could not be parsed.
	 * @return The Material enum. Returns defaultMaterial if the string could not be parsed.
	 */
	public static Material materialFromString(String from, Material defaultMaterial) {
		if (from == null) {
			return defaultMaterial;
		}
		for (String materialString : toStringArray(Material.class)) {
			if (materialString.equalsIgnoreCase(from)) {
				return Material.valueOf(materialString.toUpperCase());
			}
		}
		return defaultMaterial;
	}
	
}
