package wbs.utils.util.colour;

import org.bukkit.Color;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class ColourSRGB extends WbsColour<ColourSRGB> {
    @Range(from = 0, to = 1)
    private final double red;
    @Range(from = 0, to = 1)
    private final double green;
    @Range(from = 0, to = 1)
    private final double blue;

    public ColourSRGB(@Range(from = 0, to = 1) double red, @Range(from = 0, to = 1) double green, @Range(from = 0, to = 1) double blue) {
        this(red, green, blue, 1);
    }

    public ColourSRGB(@Range(from = 0, to = 1) double red, @Range(from = 0, to = 1) double green, @Range(from = 0, to = 1) double blue, @Range(from = 0, to = 1) double alpha) {
        super(alpha);
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public ColourSRGB(Color color) {
        super(color);

        this.red = (double) color.getRed() / 255.0;
        this.green = (double) color.getGreen() / 255.0;
        this.blue = (double) color.getBlue() / 255.0;
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
        return this;
    }

    @Override
    public ColourSRGB mix(List<@UnknownNullability ColourSRGB> toMix) {
        double sumR = this.red;
        double sumG = this.green;
        double sumB = this.blue;
        double sumA = this.alpha;

        int count = 1;
        for (ColourSRGB other : toMix) {
            if (other == null) {
                continue;
            }
            count++;
            sumR += other.red;
            sumG += other.green;
            sumB += other.blue;
            sumA += other.alpha;
        }

        return new ColourSRGB(sumR / count, sumG / count, sumB / count, sumA / count);
    }

    @Override
    protected ColourSRGB fromSRGB(ColourSRGB other) {
        return this;
    }
}
