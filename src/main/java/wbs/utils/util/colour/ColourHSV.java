package wbs.utils.util.colour;

import org.bukkit.Color;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;
import wbs.utils.WbsUtils;
import wbs.utils.util.WbsColours;
import wbs.utils.util.WbsMath;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.List;

@NullMarked
public class ColourHSV extends WbsColour<ColourHSV> {
    @Range(from = 0, to = 1)
    private final double hue;
    @Range(from = 0, to = 1)
    private final double saturation;
    @Range(from = 0, to = 1)
    private final double value;

    public ColourHSV(@Range(from = 0, to = 1) double hue, @Range(from = 0, to = 1) double saturation, @Range(from = 0, to = 1) double value) {
        this(hue, saturation, value, 1);
    }

    public ColourHSV(@Range(from = 0, to = 1) double hue, @Range(from = 0, to = 1) double saturation, @Range(from = 0, to = 1) double value, double alpha) {
        super(alpha);
        this.hue = hue;
        this.saturation = saturation;
        this.value = value;
    }

    public ColourHSV(Color color) {
        double[] hsv = WbsColours.getHSV(color);

        this.hue = hsv[0] / 360.0;
        this.saturation = hsv[1];
        this.value = hsv[2];

        super(color);
    }

    public ColourHSV(ColourSRGB color) {
        double[] hsv = WbsColours.getHSV(color.red(), color.green(), color.blue());

        this.hue = hsv[0] / 360.0;
        this.saturation = hsv[1];
        this.value = hsv[2];

        super(color.alpha);
    }

    @Range(from = 0, to = 1)
    public double hue() {
        return hue;
    }

    public ColourHSV withHue(@Range(from = 0, to = 1) double hue) {
        return new ColourHSV(hue, saturation, value, alpha());
    }

    public double saturation() {
        return saturation;
    }

    public ColourHSV withSaturation(@Range(from = 0, to = 1) double saturation) {
        return new ColourHSV(hue, saturation, value, alpha);
    }

    @Range(from = 0, to = 1)
    public double value() {
        return value;
    }

    public ColourHSV withValue(@Range(from = 0, to = 1) double value) {
        return new ColourHSV(hue, saturation, value, alpha);
    }

    @Override
    public ColourSRGB toSRGB() {
        return new ColourSRGB(WbsColours.fromHSB(hue, saturation, value));
    }

    public ColourHSV mix(ColourHSV ... others) {
        return mix(List.of(others));
    }

    @Override
    public ColourHSV mix(List<@UnknownNullability ColourHSV> others) {
        double sumSinH = Math.sin(hue * Math.TAU);
        double sumCosH = Math.cos(hue * Math.TAU);
        double sumS = saturation;
        double sumV = value;
        double sumA = alpha;

        int count = 1;
        for (ColourHSV other : others) {
            if (other == null) {
                continue;
            }
            count++;
            sumSinH += Math.sin(other.hue * Math.TAU);
            sumCosH += Math.cos(other.hue * Math.TAU);
            sumS += other.saturation;
            sumV += other.value;
            sumA += other.alpha;
        }

        // Circular mean
        double finalHue = WbsMath.modulo(Math.atan2(sumSinH / count, sumCosH / count), Math.TAU) / Math.TAU;

        return new ColourHSV(finalHue, sumS / count, sumV / count, sumA / count);
    }

    @Override
    protected ColourHSV fromSRGB(ColourSRGB other) {
        return new ColourHSV(other);
    }
}
