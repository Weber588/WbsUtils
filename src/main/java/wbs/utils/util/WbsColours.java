package wbs.utils.util;

import org.bukkit.Color;

@SuppressWarnings("unused")
public final class WbsColours {
    private WbsColours() {}

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
        if (max == 0) return new double[]{-1, 0, value};

        saturation = delta / max;

        // H
        if (red == max) {
            hue = (green - blue) / delta;
        } else if (green == max) {
            hue = 2 + (blue - red) / delta;
        } else {
            hue = 4 + (red - green) / delta;
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

        double lerpedH = lerp(hsv1[0], hsv2[0], interval);
        double lerpedS = lerp(hsv1[1], hsv2[1], interval);
        double lerpedV = lerp(hsv1[2], hsv2[2], interval);

        return fromHSB(lerpedH / 360.0f, lerpedS, lerpedV);
    }

    private static double lerp(double a, double b, double interval) {
        return (b - a) * interval + a;
    }

    public static void testHsVConversion(Color color) {
        System.out.printf("%06X%n", color.asRGB());
        double[] hsv = getHSV(color);
        System.out.println("hsv: " + hsv[0] + ", " + hsv[1] + ", " + hsv[2]);
        Color converted = fromHSB(hsv[0] / 360, hsv[1], hsv[2]);
        System.out.printf("%06X%n", converted.asRGB());
    }
}
