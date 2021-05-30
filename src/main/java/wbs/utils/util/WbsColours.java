package wbs.utils.util;

import org.bukkit.Color;

public class WbsColours {


    /**
     * Adapted method from @java.awt.Color
     * @param hue The hue in range 0-1
     * @param saturation The saturation in range 0-1
     * @param brightness The brightness in range 0-1
     * @return The bukkit compatible Color object.
     */
    private static Color fromHSB(float hue, float saturation, float brightness) {

        int scaledBrightness = (int) (brightness * 255);
        if (saturation == 0) {
            return Color.fromRGB(scaledBrightness, scaledBrightness, scaledBrightness);
        }

        hue = hue - (float) Math.floor(hue);
        int i = (int) (6 * hue);
        float f = 6 * hue - i;
        float p = brightness * (1 - saturation);
        float q = brightness * (1 - saturation * f);
        float t = brightness * (1 - saturation * (1 - f));

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


    public static float[] getHSV(Color colour) {
        float red = colour.getRed() / 255.0f;
        float green = colour.getGreen() / 255.0f;
        float blue = colour.getBlue() / 255.0f;

        float hue, saturation, value;

        float min, max, delta;

        min = Math.min(Math.min(red, green), blue);
        max = Math.max(Math.max(red, green), blue);

        value = max;
        delta = max - min;

        // Saturation
        if (max == 0) return new float[]{-1, 0, value};

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
        return new float[] { hue, saturation, value };
    }

    public static Color colourLerp(Color start, Color end, float interval) {
        float[] hsv1 = getHSV(start);

        float[] hsv2 = getHSV(end);

        float lerpedH = lerp(hsv1[0], hsv2[0], interval);
        float lerpedS = lerp(hsv1[1], hsv2[1], interval);
        float lerpedV = lerp(hsv1[2], hsv2[2], interval);

        return fromHSB(lerpedH / 360.0f, lerpedS, lerpedV);
    }

    private static float lerp(float a, float b, float interval) {
        return (b - a) * interval + a;
    }

    public static void testHsVConversion(Color color) {
        System.out.printf("%06X%n", color.asRGB());
        float[] hsv = getHSV(color);
        System.out.println("hsv: " + hsv[0] + ", " + hsv[1] + ", " + hsv[2]);
        Color converted = fromHSB(hsv[0] / 360, hsv[1], hsv[2]);
        System.out.printf("%06X%n", converted.asRGB());
    }
}
