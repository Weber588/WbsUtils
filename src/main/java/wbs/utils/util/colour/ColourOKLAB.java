package wbs.utils.util.colour;

import org.bukkit.Color;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class ColourOKLAB extends WbsColour<ColourOKLAB> {
    private final double l;
    private final double a;
    private final double b;

    public ColourOKLAB(@Range(from = 0, to = 1) double l, @Range(from = 0, to = 1) double a, @Range(from = 0, to = 1) double b) {
        this(l, a, b, 1);
    }

    public ColourOKLAB(@Range(from = 0, to = 1) double l, @Range(from = 0, to = 1) double a, @Range(from = 0, to = 1) double b, @Range(from = 0, to = 1) double alpha) {
        super(alpha);
        this.l = l;
        this.a = a;
        this.b = b;
    }

    public ColourOKLAB(Color color) {
        super(color);

        double r = (double) color.getRed() / 255.0;
        double g = (double) color.getGreen() / 255.0;
        double b = (double) color.getBlue() / 255.0;

        double[] components = getComponents(r, g, b);

        this.l = components[0];
        this.a = components[1];
        this.b = components[2];
    }

    public ColourOKLAB(ColourSRGB color) {
        super(color.alpha);

        double[] components = getComponents(color.red(), color.green(), color.blue());

        this.l = components[0];
        this.a = components[1];
        this.b = components[2];
    }

    /**
     * Modified from https://bottosson.github.io/posts/oklab/
     */
    private double[] getComponents(double r, double g, double b) {
        double red = toLinearSpace(r);
        double green = toLinearSpace(g);
        double blue = toLinearSpace(b);

        double l = 0.4122214708f * red + 0.5363325363f * green + 0.0514459929f * blue;
        double m = 0.2119034982f * red + 0.6806995451f * green + 0.1073969566f * blue;
        double s = 0.0883024619f * red + 0.2817188376f * green + 0.6299787005f * blue;

        double l_ = Math.cbrt(l);
        double m_ = Math.cbrt(m);
        double s_ = Math.cbrt(s);

        double l1 = 0.2104542553f * l_ + 0.7936177850f * m_ - 0.0040720468f * s_;
        double a1 = 1.9779984951f * l_ - 2.4285922050f * m_ + 0.4505937099f * s_;
        double b1 = 0.0259040371f * l_ + 0.7827717662f * m_ - 0.8086757660f * s_;

        return new double[] {l1, a1, b1};
    }

    private record Result(double l1, double a1, double b1) {
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
    public double l() {
        return l;
    }

    @Range(from = 0, to = 1)
    public double a() {
        return a;
    }

    @Range(from = 0, to = 1)
    public double b() {
        return b;
    }

    /**
     * Modified from https://bottosson.github.io/posts/oklab/
     */
    @Override
    public ColourSRGB toSRGB() {
        double l_ = this.l + 0.3963377774f * this.a + 0.2158037573f * this.b;
        double m_ = this.l - 0.1055613458f * this.a - 0.0638541728f * this.b;
        double s_ = this.l - 0.0894841775f * this.a - 1.2914855480f * this.b;

        double l = l_*l_*l_;
        double m = m_*m_*m_;
        double s = s_*s_*s_;

        double red = +4.0767416621f * l - 3.3077115913f * m + 0.2309699292f * s;
        double green = -1.2684380046f * l + 2.6097574011f * m - 0.3413193965f * s;
        double blue = -0.0041960863f * l - 0.7034186147f * m + 1.7076147010f * s;
        
        return new ColourSRGB(toStandardSpace(red), toStandardSpace(green), toStandardSpace(blue), alpha);
    }

    @Override
    public ColourOKLAB mix(List<@UnknownNullability ColourOKLAB> toMix) {
        double sumL = this.l;
        double sumA = this.a;
        double sumB = this.b;
        double sumAlpha = this.alpha;

        int count = 1;
        for (ColourOKLAB other : toMix) {
            if (other == null) {
                continue;
            }
            count++;
            sumL += other.l;
            sumA += other.a;
            sumB += other.b;
            sumAlpha += other.alpha;
        }

        return new ColourOKLAB(sumL / count, sumA / count, sumB / count, sumAlpha / count);
    }

    @Override
    protected ColourOKLAB fromSRGB(ColourSRGB other) {
        return new ColourOKLAB(other);
    }
}
