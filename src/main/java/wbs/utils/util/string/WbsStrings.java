package wbs.utils.util.string;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.VersionUtil;
import wbs.utils.util.WbsColours;


public final class WbsStrings {
	
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
			display.append(" ").append(capitalize(word));
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
			charList[i] = '§';
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
		String[] newStringList = new String[strings.length-index];
		if (strings.length - index >= 0)
			System.arraycopy(strings, index, newStringList, 0, strings.length - index);
		return String.join(" ", newStringList);
	}

	public static String combineFirst(String[] strings, int length) {
		String[] newStringList = new String[length];
		System.arraycopy(strings, 0, newStringList, 0, length);
		return String.join(" ", newStringList);
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
		int length = string.length();

		float step = 1.0f / (length - 1);

		StringBuilder newMessageBuilder = new StringBuilder();

		float progress = 0;
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

		return WbsStrings.colourise(newMessageBuilder.toString());
	}

	public static String addColourGradient(String string, Color... colors) {
		int length = string.length();

		StringBuilder builder = new StringBuilder();

		int charsPerColour = length / colors.length;
		int remainder = length % colors.length;

		int beginIndex = 0;
		int endIndex = charsPerColour + 1;
		remainder--;
		for (int i = 0; i < colors.length; i++) {
			String substring = string.substring(beginIndex, endIndex);

			builder.append(substring);

			beginIndex += (endIndex - beginIndex);
			endIndex += charsPerColour;
			if (remainder > 0) {
				endIndex += 1;
				remainder--;
			}
		}

		return builder.toString();
	}

}
