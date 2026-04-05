package wbs.utils.util;

import org.bukkit.Color;
import org.jetbrains.annotations.Range;

public class WbsColour {
    @Range(from = 0, to = 1)
    private double hue;
    @Range(from = 0, to = 1)
    private double saturation;
    @Range(from = 0, to = 1)
    private double value;

    public WbsColour(@Range(from = 0, to = 1) double hue, @Range(from = 0, to = 1) double saturation, @Range(from = 0, to = 1) double value) {
        this.hue = hue;
        this.saturation = saturation;
        this.value = value;
    }

    public WbsColour(Color color) {
        double[] hsv = WbsColours.getHSV(color);

        this.hue = hsv[0];
        this.saturation = hsv[1];
        this.value = hsv[2];
    }

    @Range(from = 0, to = 1)
    public double getHue() {
        return hue;
    }

    public WbsColour setHue(@Range(from = 0, to = 1) double hue) {
        this.hue = hue;
        return this;
    }

    public WbsColour shiftHue(@Range(from = -1, to = 1) double shiftAmount) {
        this.hue += shiftAmount;
        this.hue = WbsMath.modulo(this.hue, 1);

        return this;
    }

    public double getSaturation() {
        return saturation;
    }

    public WbsColour setSaturation(@Range(from = 0, to = 1) double saturation) {
        this.saturation = saturation;
        return this;
    }

    @Range(from = 0, to = 1)
    public double getValue() {
        return value;
    }

    public WbsColour setValue(@Range(from = 0, to = 1) double value) {
        this.value = value;
        return this;
    }

    @Override
    public WbsColour clone() {
        return new WbsColour(hue, saturation, value);
    }

    public Color toBukkitColor() {
        return WbsColours.fromHSB(hue, saturation, value);
    }
}
