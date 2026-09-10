package wbs.utils.util.colour;

import com.google.errorprone.annotations.Immutable;
import org.bukkit.Color;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
@Immutable
public abstract class WbsColour<T extends WbsColour<T>> {
    protected final double alpha;

    public WbsColour(double alpha) {
        this.alpha = alpha;
    }

    public WbsColour(Color color) {
        this.alpha = (double) color.getAlpha() / 255.0;
    }

    public Color toBukkitColor() {
        ColourSRGB srgb = toSRGB();

        return Color.fromARGB(
                (int) Math.round(srgb.alpha * 255),
                (int) Math.round(srgb.red() * 255),
                (int) Math.round(srgb.green() * 255),
                (int) Math.round(srgb.blue() * 255)
        );
    }

    public abstract ColourSRGB toSRGB();

    public double alpha() {
        return alpha;
    }

    protected T mixFrom(WbsColour<?> ... toMix) {
        return mixFrom(List.of(toMix));
    }
    protected T mixFrom(List<WbsColour<?>> toMix) {
        return mix(toMix.stream()
                .map(this::from)
                .toList());
    }

    public abstract T mix(List<@UnknownNullability T> toMix);

    protected T from(WbsColour<?> other) {
        ColourSRGB srgb = other.toSRGB();

        return fromSRGB(srgb);
    }

    protected abstract T fromSRGB(ColourSRGB other);
}
