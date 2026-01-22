package wbs.utils.util.string;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.map.MinecraftFont;
import org.jetbrains.annotations.NotNull;
import wbs.utils.WbsUtils;
import wbs.utils.util.VersionUtil;
import wbs.utils.util.WbsColours;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings("unused")
public final class WbsStrings {

	public static final String REGEX = "^[(\\[\"'].*$";

	private WbsStrings() {}
	
	/**
	 * Capitalize all words in a string, separated by spaces
	 * @param str The string to process
	 * @return The string with all words capitalized
	 */
	public static String capitalizeAll(String str) {
		String[] words = str.toLowerCase().split(" ");
		StringBuilder display = new StringBuilder();
		for (String word : words) {
			if (word.matches(REGEX)) {
				display.append(" ").append(word.charAt(0)).append(capitalize(word.substring(1)));
			} else {
				display.append(" ").append(capitalize(word));
			}
		}
		return display.substring(1);
	}
	
	/**
	 * Capitalize the first letter a string
	 * @param str The string to capitalize
	 * @return The capitalized string
	 */
	public static String capitalize(@NotNull String str) {
		if (str.length() == 0) {
			return str;
		}
		String display = str.substring(1).toLowerCase();
		display = str.substring(0, 1).toUpperCase() + display;
		return display;
	}
	
	/**
	 * Get the first string in a Collection that contains another string.
	 * @param find The string to find
	 * @param in The Collection to search for the string in
	 * @return The full string that contains the find parameter.
	 * null if the Collection does not contain a string that contains find.
	 */
	public static String getLineWith(String find, Collection<String> in) {
		for (String node : in) {
			if (node.contains(find)) {
				return node;
			}
		}
		return null;
	}
	
	/**
	 * Get a string that, when rendered in minecraft, will be invisible.
	 * @param original The string to hide
	 * @return The string that will render invisibly in game.
	 */
	public static String getInvisibleString(String original) {
		char[] charList = new char[original.length()*2];
		int i = 0;
		for (char c : original.toCharArray()) {
			charList[i] = ChatColor.COLOR_CHAR;
			charList[i+1] = c;
			i+=2;
		}
		return new String(charList);
	}

	private static final Pattern HEX_CODES = Pattern.compile("&#([0-9a-fA-F]{6})");

	public static String colourise(String string) {
		if (VersionUtil.getVersion() >= 16) {
			Matcher rgbMatcher = HEX_CODES.matcher(string);

			while (rgbMatcher.find()) {
				String colour = string.substring(rgbMatcher.start(), rgbMatcher.end());

				string = string.replace(colour, ChatColor.of(colour.substring(1)).toString());
				rgbMatcher = HEX_CODES.matcher(string);
			}
		}

		return ChatColor.translateAlternateColorCodes('&', string);
	}
	/**
	 * Reveal a string concealed by {@link #getInvisibleString(String)}
	 * @param invisibleString An "invisible" string created by {@link #getInvisibleString(String)}
	 * @return The original string
	 */
	public static String revealString(String invisibleString) {
		return invisibleString.replaceAll("�", "");
	}

	/**
	 * Combine the final elements of a string array starting at a specified index
	 * @param strings The array of Strings to combine
	 * @param index The index to start combining at
	 * @return A single String containing all entries in {strings} split with " ", excluding the first {index} entries
	 */
	public static String combineLast(String[] strings, int index) {
		return combineLast(strings, index, " ");
	}

	public static String combineLast(String[] strings, int index, String delimiter) {
		String[] newStringList = new String[strings.length-index];
		if (strings.length - index >= 0)
			System.arraycopy(strings, index, newStringList, 0, strings.length - index);
		return String.join(delimiter, newStringList);
	}

	public static Set<String> getNextNodes(String current, Collection<String> options) {
		return getNextNodes(current, options, ".");
	}

	public static Set<String> getNextNodes(String current, Collection<String> options, String delimiter) {
		Set<String> nodes = new HashSet<>();

		String[] currentNodes = current.split(Pattern.quote(delimiter));
		int currentLength = currentNodes.length;

		if (current.endsWith(".")) {
			String[] temp = new String[currentLength+1];
			System.arraycopy(currentNodes, 0, temp, 0, currentLength);
			temp[currentLength] = "";
			currentLength++;

			currentNodes = temp;
		}

		String latestArg = currentNodes[currentLength-1];

		main: for (String path: options) {
			String[] nodesInPath = path.split("\\.");

			if (nodesInPath.length >= currentLength) {
				for (int i = 0; i < currentLength-1; i++) {
					if (!currentNodes[i].equals(nodesInPath[i])) {
						continue main;
					}
				}

				// We now know that currentNodes is the starting args of nodesInPath
				// Set will handle duplicates
				String toAdd = nodesInPath[currentLength-1];

				if (toAdd.startsWith(latestArg)) {
					toAdd = toAdd.substring(latestArg.length());

					if (toAdd.length() == 0) {
						if (nodesInPath.length > currentLength) {
							return getNextNodes(current + delimiter, options, delimiter);
						}
					} else {
						nodes.add(current + toAdd);
					}
				}
			}
		}

		return nodes;
	}


	public static String combineFirst(String[] strings, int length) {
		return combineFirst(strings, length, " ");
	}
	public static String combineFirst(String[] strings, int length, String delimiter) {
		String[] newStringList = new String[length];
		System.arraycopy(strings, 0, newStringList, 0, length);
		return String.join(delimiter, newStringList);
	}
	
	private static final String[] colourCodes = {
			"&0", "&1", "&2", "&3", "&4", "&5",
			"&6", "&7", "&8", "&9",
			
			"&a", "&b", "&c",
			"&d", "&e", "&f",
			
			"&o", "&l", "&n",
			"&m", "&k"
	};
	
	public static String undoColourText(String colouredText) {
        char[] uncolouredArray = colouredText.toCharArray();
        for (int i = 0; i < uncolouredArray.length - 1; i++) {
            if (uncolouredArray[i] == ChatColor.COLOR_CHAR && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(uncolouredArray[i+1]) > -1) {
            	uncolouredArray[i] = '&';
            	uncolouredArray[i+1] = Character.toLowerCase(uncolouredArray[i+1]);
            }
        }
        String uncoloured = new String(uncolouredArray);
        if (uncoloured.endsWith("&r")) {
        	uncoloured = uncoloured.substring(0, uncolouredArray.length);
        }
        return uncoloured;
	}

	public static String addColourGradient(String string, Color startColour, Color endColour) {
		return addColourGradient(string, startColour, endColour, true);
	}

	public static String addColourGradient(String string, Color startColour, Color endColour, boolean colourise) {
		int length = string.length();

		double step = 1.0f / (length - 1);

		StringBuilder newMessageBuilder = new StringBuilder();

		double progress = 0;
		Color currentColour;
		for (int i = 0; i < length; i++) {
			// TODO: Make colour cycle ignore spaces
			newMessageBuilder.append("&#");

			currentColour = WbsColours.colourLerp(startColour, endColour, progress);

			newMessageBuilder.append(String.format("%06X", currentColour.asRGB()))
					.append(string.charAt(i));

			progress += step;
			if (progress > 1) progress = 1; // Yay floating point error
		}

		if (colourise) {
			return WbsStrings.colourise(newMessageBuilder.toString());
		} else {
			return newMessageBuilder.toString();
		}
	}

	/**
	 * Filter a collection of strings by those that start with the given string
	 * @param choices The collection to filter
	 * @param filter The string to check startsWith
	 * @return A list of strings that contains only elements of choices that
	 * start with the filter string, ignoring case
	 */
	public static List<String> filterStartsWith(Collection<String> choices, String filter) {
		List<String> result = new ArrayList<>();
		for (String add : choices) {
			if (add.toLowerCase().startsWith(filter.toLowerCase())) {
				result.add(add);
			}
		}
		return result;
	}

	/**
	 * Wrap a string into multiple lines of text, each with a maximum width.
	 * @param toWrap The string to split into multiple lines.
	 * @param maxWidth The maximum width of each line, according to its pixel width in the default Minecraft font (Mojangles).
	 * @return A list of strings that, when read sequentially, will have the same content as the given string.
	 */
	public static List<String> wrapText(String toWrap, int maxWidth) {
		return wrapText(toWrap, maxWidth, MinecraftFont.Font::getWidth);
	}

	/**
	 * Wrap a string into multiple lines of text, each with a maximum width.
	 * @param toWrap The string to split into multiple lines.
	 * @param maxWidth The maximum width of each line, according to the given widthFunction.
	 * @param widthFunction A function that accepts a string and returns its width, such as for a font or number of characters.
	 * @return A list of strings that, when read sequentially, will have the same content as the given string.
	 */
	public static List<String> wrapText(String toWrap, int maxWidth, Function<String, Integer> widthFunction) {
		String remaining = toWrap.trim();
		List<String> lines = new LinkedList<>();

		String[] split = remaining.split("\n");
		if (split.length > 1) {
			for (String forcedLine : split) {
				lines.addAll(wrapText(forcedLine, maxWidth, widthFunction));
			}

			return lines;
		}

		while (!remaining.isEmpty()) {
			String line = null;
			int currentWidth;
			do {
				int wordEnd = remaining.indexOf(" ");
				if (wordEnd == -1) {
					wordEnd = remaining.length();
				}

				String word = remaining.substring(0, wordEnd);

				remaining = remaining.substring(word.length()).trim();

				if (line == null) {
					line = word;
				} else {
					line += " " + word.trim();
				}
				currentWidth = widthFunction.apply(line);
			} while (currentWidth <= maxWidth && !remaining.isEmpty());
			lines.add(line);
		}

		return lines;
	}
}
