package wbs.utils.util.colour;

import org.bukkit.Color;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class ColourLinearRGB extends WbsColour<ColourLinearRGB> {
    @Range(from = 0, to = 1)
    private final double red;
    @Range(from = 0, to = 1)
    private final double green;
    @Range(from = 0, to = 1)
    private final double blue;

    public ColourLinearRGB(@Range(from = 0, to = 1) double red, @Range(from = 0, to = 1) double green, @Range(from = 0, to = 1) double blue) {
        this(red, green, blue, 1);
    }

    public ColourLinearRGB(@Range(from = 0, to = 1) double red, @Range(from = 0, to = 1) double green, @Range(from = 0, to = 1) double blue, @Range(from = 0, to = 1) double alpha) {
        super(alpha);
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public ColourLinearRGB(Color color) {
        super(color);

        this.red = toLinearSpace((double) color.getRed() / 255.0);
        this.green = toLinearSpace((double) color.getGreen() / 255.0);
        this.blue = toLinearSpace((double) color.getBlue() / 255.0);
    }

    public ColourLinearRGB(ColourSRGB color) {
        super(color.alpha);

        this.red = toLinearSpace(color.red());
        this.green = toLinearSpace(color.green());
        this.blue = toLinearSpace(color.blue());
    }

    /**
     * Modified from https://bottosson.github.io/posts/oklab/
     */
    private double toLinearSpace(double component) {
        if (component >= 0.0031308) {
            return (1.055) * Math.pow(component,(1.0/2.4)) - 0.055;
        } else {
            return 12.92 * component;
        }
    }

    /**
     * Modified from https://bottosson.github.io/posts/oklab/
     */
    private double toStandardSpace(double component) {
        if (component >= 0.04045) {
            return Math.pow(((component + 0.055)/(1 + 0.055)), 2.4);
        } else {
            return component / 12.92;
        }
    }

    @Range(from = 0, to = 1)
    public double red() {
        return red;
    }

    @Range(from = 0, to = 1)
    public double green() {
        return green;
    }

    @Range(from = 0, to = 1)
    public double blue() {
        return blue;
    }

    @Override
    public ColourSRGB toSRGB() {
        return new ColourSRGB(toStandardSpace(red), toStandardSpace(green), toStandardSpace(blue), alpha);
    }

    @Override
    public ColourLinearRGB mix(List<@UnknownNullability ColourLinearRGB> toMix) {
        double sumR = this.red;
        double sumG = this.green;
        double sumB = this.blue;
        double sumA = this.alpha;

        int count = 1;
        for (ColourLinearRGB other : toMix) {
            if (other == null) {
                continue;
            }
            count++;
            sumR += other.red;
            sumG += other.green;
            sumB += other.blue;
            sumA += other.alpha;
        }

        return new ColourLinearRGB(sumR / count, sumG / count, sumB / count, sumA / count);
    }

    @Override
    protected ColourLinearRGB fromSRGB(ColourSRGB other) {
        return new ColourLinearRGB(other);
    }
}
