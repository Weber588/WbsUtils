package wbs.utils.util.string;

import org.jetbrains.annotations.Nullable;

import java.util.TreeMap;

@SuppressWarnings("unused")
public final class RomanNumerals {
    private RomanNumerals() {}

    private final static TreeMap<Integer, String> intToNumeralMap = new TreeMap<>();
    private final static TreeMap<String, Integer> numeralToIntMap = new TreeMap<>();

    private static void registerPair(int value, String numeral) {
        intToNumeralMap.put(value, numeral);
        numeralToIntMap.put(numeral, value);
    }

    static {
        registerPair(1000, "M");
        registerPair(900, "CM");
        registerPair(500, "D");
        registerPair(400, "CD");
        registerPair(100, "C");
        registerPair(90, "XC");
        registerPair(50, "L");
        registerPair(40, "XL");
        registerPair(10, "X");
        registerPair(9, "IX");
        registerPair(5, "V");
        registerPair(4, "IV");
        registerPair(1, "I");
    }

    /**
     * Convert an integer to roman numerals
     * @param number The number to convert
     * @return The roman numeral of the given number.<br/>
     * If the given number is 0, then "0" is returned.<br/>
     * If the given number is negative, then the roman numeral
     * of the absolute value is returned, prepended with "-".
     * @author Ben-Hur Langoni Junior on stackoverflow.com, modified by Weber588
     */
    public static String toRoman(int number) {
        if (number == 0) {
            return "0";
        } else if (number < 0) {
            return "-" + toRoman(Math.abs(number));
        }

        int l =  intToNumeralMap.floorKey(number);
        if ( number == l ) {
            return intToNumeralMap.get(number);
        }
        return intToNumeralMap.get(l) + toRoman(number-l);
    }

    /**
     * Converts a double to roman numerals with a decimal point,
     * despite that not being "true" roman numerals. If the double
     * has no decimal component, this method functions the same as
     * {@link #toRoman(int)}.
     * @param number The double to convert
     * @return The roman numeral version
     */
    public static String toRoman(double number) {
        int whole = (int) number;
        double decimal = (number - whole);
        String decimalString = String.valueOf(decimal).substring(2);

        int decimalAsWhole = -1;
        if (decimalString.length() > 0) {
            try {
                decimalAsWhole = Integer.parseInt(decimalString);
            } catch (NumberFormatException ignored) {}
        }

        if (decimalAsWhole <= 0) {
            return toRoman(whole);
        } else {
            return toRoman(whole) + "." + toRoman(decimalAsWhole);
        }
    }

    /**
     * Get the number associated with a given numeral.
     * @param numeral The numeral to convert to an integer.
     * @return The integer version, 0 if the numeral was '0'.
     * @throws IllegalArgumentException If the char is not a valid roman numeral
     */
    public static int numeralOf(char numeral) throws IllegalArgumentException {
        if (numeral == '0')
            return 0;

        Integer value = numeralToIntMap.get(String.valueOf(numeral));
        if (value == null) throw new IllegalArgumentException("Invalid numeral \"" + numeral + "\".");

        return value;
    }

    /**
     * Convert a given Roman numeral string to an int
     * @param numerals The Roman numeral string to convert
     * @return The value of the given roman numeral
     * @throws IllegalArgumentException If the string contained any invalid characters,
     * or if the given string contained a decimal place.
     */
    public static int fromRomanNumerals(String numerals) throws IllegalArgumentException {
        if (numerals.startsWith("-")) {
            return -fromRomanNumerals(numerals.substring(1));
        }
        if (numerals.equalsIgnoreCase("0"))
            return 0;

        if (numerals.contains("."))
            throw new IllegalArgumentException("Decimal place found in int conversion. Use #fromRomanNumeralsDecimal(String).");


        int value = 0;

        for (int i = 0; i < numerals.length(); i++) {
            int current = numeralOf(numerals.charAt(i));

            if (i + 1 < numerals.length()) {
                int next = numeralOf(numerals.charAt(i + 1));

                if (current < next) {
                    value += next;
                    value -= current;
                    i++; // Skip next, already processed it
                } else {
                    value = value + current;
                }
            } else {
                value += current;
            }
        }

        return value;
    }

    /**
     * Convert a given Roman numeral string to a double
     * @param numerals The Roman numeral string to convert
     * @return The value of the given roman numeral
     * @throws IllegalArgumentException If the string contained any invalid characters,
     * or if there more than 1 "." to denote a decimal point.
     */
    public static double fromRomanNumeralsDecimal(String numerals) {
        if (numerals.contains(".")) {
            String[] components = numerals.split("\\.");
            if (components.length != 2) {
                throw new IllegalArgumentException("Too many decimal points.");
            }

            String wholeNumerals = components[0];
            String decimalNumerals = components[1];
            if (decimalNumerals.length() == 0) {
                return fromRomanNumerals(wholeNumerals);
            }

            int whole = fromRomanNumerals(wholeNumerals);
            int wholeDecimal = fromRomanNumerals(decimalNumerals);

            return wholeDecimal / (10.0 * decimalNumerals.length());
        }

        return fromRomanNumerals(numerals);
    }
}
