package wbs.utils.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.WbsUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for dealing with colours, including chat colours and colour format conversions.
 */
@SuppressWarnings("unused")
public final class WbsColours {
    private WbsColours() {}

    // Backwards compatibility with pre-1.16, since ChatColor.of didn't exist
    private static final Map<ChatColor, Integer> legacyColours = new HashMap<>();

    static {
        legacyColours.put(ChatColor.BLACK, 0);
        legacyColours.put(ChatColor.DARK_BLUE, 170);
        legacyColours.put(ChatColor.DARK_GREEN, 43520);
        legacyColours.put(ChatColor.DARK_AQUA, 43690);
        legacyColours.put(ChatColor.DARK_RED, 11141120);
        legacyColours.put(ChatColor.DARK_PURPLE, 11141290);
        legacyColours.put(ChatColor.GOLD, 16755200);
        legacyColours.put(ChatColor.GRAY, 11184810);
        legacyColours.put(ChatColor.DARK_GRAY, 5592405);
        legacyColours.put(ChatColor.BLUE, 5592575);
        legacyColours.put(ChatColor.GREEN, 5635925);
        legacyColours.put(ChatColor.AQUA, 5636095);
        legacyColours.put(ChatColor.RED, 16733525);
        legacyColours.put(ChatColor.LIGHT_PURPLE, 16733695);
        legacyColours.put(ChatColor.YELLOW, 16777045);
        legacyColours.put(ChatColor.WHITE, 16777215);
    }

    @Nullable
    public static Color fromHexOrDyeString(String input) {
        Color colour;
        if (input.equalsIgnoreCase("")) {
            return null;
        } else {
            int colourInt;
            try {
                colourInt = Integer.parseInt(input, 16);
            } catch (NumberFormatException e) {
                DyeColor exact = WbsEnums.getEnumFromString(DyeColor.class, input);
                if (exact != null) {
                    return exact.getColor();
                } else {
                    return null;
                }
            }
            return Color.fromRGB(colourInt);
        }
    }

    @NotNull
    public static Color fromHexOrDyeString(String input, Color defaultColour) {
        Color colour =  fromHexOrDyeString(input);
        return colour != null ? colour : defaultColour;
    }

    /**
     * Backwards compatible version of {@link ChatColor#of(java.awt.Color)} that
     * accepts a Bukkit {@link Color} instead.
     * @param colour The colour to convert
     * @return The closest ChatColor if pre-1.16, or the {@link ChatColor} associated with the
     * RGB of the colour
     */
    public static ChatColor toChatColour(Color colour) {
        if (VersionUtil.getVersion() >= 16.0) {
            return ChatColor.of(new java.awt.Color(colour.asRGB()));
        }

        double[] inputHSV = getHSV(colour);

        double shortestDistance = Double.MAX_VALUE; // distance in HSV space
        ChatColor closest = ChatColor.BLACK;
        for (ChatColor chatColour : ChatColor.values()) {
            double[] hsv = getHSV(Color.fromRGB(chatColour.getColor().getRGB()));

            double distanceSquared = arrayVectorDistance(inputHSV, hsv);
            if (distanceSquared < shortestDistance) {
                closest = chatColour;
                shortestDistance = distanceSquared;
            }
        }

        return closest;
    }

    @NotNull
    public static DyeColor toDyeColour(Color colour) {
        DyeColor closest = DyeColor.getByColor(colour);
        if (closest != null) return closest;

        double[] inputHSV = getHSV(colour);

        double shortestDistance = Double.MAX_VALUE; // distance in HSV space
        closest = DyeColor.BLACK;
        for (DyeColor dyeColor : DyeColor.values()) {
            double[] hsv = getHSV(dyeColor.getColor());

            double distanceSquared = arrayVectorDistance(inputHSV, hsv);
            if (distanceSquared < shortestDistance) {
                closest = dyeColor;
                shortestDistance = distanceSquared;
            }
        }

        return closest;
    }

    private static double arrayVectorDistance(double[] arr1, double[] arr2) {
        if (arr1.length != arr2.length) throw new IllegalArgumentException("Arrays must be equal length to treat as vectors");

        double distanceSquared = 0;
        for (int i = 0; i < arr1.length; i++) {
            double sum = arr1[i] - arr2[i];
            distanceSquared += sum * sum;
        }

        return distanceSquared;
    }

    /**
     * Adapted method from @java.awt.Color
     * @param hue The hue in range 0-1
     * @param saturation The saturation in range 0-1
     * @param brightness The brightness in range 0-1
     * @return The bukkit compatible Color object.
     */
    public static Color fromHSB(double hue, double saturation, double brightness) {

        int scaledBrightness = (int) (brightness * 255);
        if (saturation == 0) {
            return Color.fromRGB(scaledBrightness, scaledBrightness, scaledBrightness);
        }

        hue = hue - (float) Math.floor(hue);
        int i = (int) (6 * hue);
        double f = 6 * hue - i;
        double p = brightness * (1 - saturation);
        double q = brightness * (1 - saturation * f);
        double t = brightness * (1 - saturation * (1 - f));

        switch (i) {
            case 0:
                return Color.fromRGB(scaledBrightness, (int) (t * 255), (int) (p * 255));
            case 1:
                return Color.fromRGB((int) (q * 255), scaledBrightness, (int) (p * 255));
            case 2:
                return Color.fromRGB((int) (p * 255), scaledBrightness, (int) (t * 255));
            case 3:
                return Color.fromRGB((int) (p * 255), (int) (q * 255), scaledBrightness);
            case 4:
                return Color.fromRGB((int) (t * 255), (int) (p * 255), scaledBrightness);
            case 5:
                return Color.fromRGB(scaledBrightness, (int) (p * 255), (int) (q * 255));
            default:
                return Color.BLACK;
        }
    }


    public static double[] getHSV(Color colour) {
        double red = colour.getRed() / 255.0f;
        double green = colour.getGreen() / 255.0f;
        double blue = colour.getBlue() / 255.0f;

        double hue, saturation, value;

        double min, max, delta;

        min = Math.min(Math.min(red, green), blue);
        max = Math.max(Math.max(red, green), blue);

        value = max;
        delta = max - min;

        // Saturation
        if (max == 0) return new double[]{0, 0, value};

        saturation = delta / max;

        if (delta == 0) {
            hue = 0;
        } else {
            // H
            if (red == max) {
                hue = (green - blue) / delta;
            } else if (green == max) {
                hue = 2 + (blue - red) / delta;
            } else {
                hue = 4 + (red - green) / delta;
            }
        }

        hue *= 60;

        if (hue < 0) hue += 360;
    //    saturation = saturation * 100;
    //    value = (value / 256) * 100;
        return new double[] { hue, saturation, value };
    }

    public static Color colourLerp(Color start, Color end, double interval) {
        double[] hsv1 = getHSV(start);
        double[] hsv2 = getHSV(end);

        double lerpedH = WbsMath.lerp(hsv1[0], hsv2[0], interval);
        double lerpedS = WbsMath.lerp(hsv1[1], hsv2[1], interval);
        double lerpedV = WbsMath.lerp(hsv1[2], hsv2[2], interval);

/*
        DecimalFormat format = new DecimalFormat("0.00");
        WbsUtils.getInstance().getLogger().info(format.format(hsv1[0]) + ", " + format.format(hsv1[1]) + ", " + format.format(hsv1[2])
                + " -> " + format.format(hsv2[0]) + ", " + format.format(hsv2[1]) + ", " + format.format(hsv2[2]) + "; "
                + format.format(interval) + " ==> "
                + format.format(lerpedH) + ", " + format.format(lerpedS) + ", " + format.format(lerpedV));
*/

        return fromHSB(lerpedH / 360.0f, lerpedS, lerpedV)
                .setAlpha((int) WbsMath.lerp(start.getAlpha(), end.getAlpha(), interval));
    }

    public static Color colourCircularLerp(Color start, Color end, double interval) {
        double[] hsv1 = getHSV(start);
        double[] hsv2 = getHSV(end);

        double lerpedH = WbsMath.moduloLerp(hsv1[0], hsv2[0], interval, 360);
        double lerpedS = WbsMath.lerp(hsv1[1], hsv2[1], interval);
        double lerpedV = WbsMath.lerp(hsv1[2], hsv2[2], interval);

        return fromHSB(lerpedH / 360.0f, lerpedS, lerpedV)
                .setAlpha((int) WbsMath.lerp(start.getAlpha(), end.getAlpha(), interval));
    }
}
