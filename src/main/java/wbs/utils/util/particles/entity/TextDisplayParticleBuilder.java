package wbs.utils.util.particles.entity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import wbs.utils.WbsUtils;
import wbs.utils.util.WbsColours;
import wbs.utils.util.WbsMath;
import wbs.utils.util.particles.entity.interpolation.InterpolatedFrameGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class TextDisplayParticleBuilder extends DisplayParticleBuilder<TextDisplay> {
    private static final Vector3f SCALE_TO_SQUARE = new Vector3f(2f, 1f, 2f);
    public static final @NotNull TextComponent DEFAULT_TEXT = Component.text(" ");
    private @Nullable TextComponent text;

    public static Vector3f getScaling() {
        return new Vector3f(SCALE_TO_SQUARE);
    }

    public static final float DEFAULT_TEXT_DISPLAY_HEIGHT = 4f;

    /**
     * In vanilla, text displays are 4/10th of a block pixel (1/40th of a block) too far to the left.
     * SOMETIMES it's 1/80th if the total pixels in the characters it contains is even.
     */
    public static final Vector3f DUMB_TEXT_DISPLAY_FIX = new Vector3f(-1/40f, 0, 0);

    private final Map<Integer, Float> rotationFrames = new HashMap<>();
    private final Map<Integer, Color> colorFrames = new HashMap<>();

    @Nullable
    protected Color backgroundColor;

    public TextDisplayParticleBuilder() {
        super(TextDisplay.class);

        // Set as default; bypass dynamic setter
        super.setScale(SCALE_TO_SQUARE);
    }

    private static Float interpolateRotation(Float start, Float end, double progress) {
        return (float) WbsMath.moduloLerp(start, end, progress, Math.TAU);
    }

    @Override
    public DisplayParticleBuilder<TextDisplay> setScale(Vector3f scale) {
        // Treat square as default
        return super.setScale(new Vector3f(scale).mul(SCALE_TO_SQUARE));
    }

    @Override
    protected void configure(TextDisplay display) {
        display.setBillboard(Display.Billboard.CENTER);

        display.text(Objects.requireNonNullElse(text, DEFAULT_TEXT));

        display.setBackgroundColor(backgroundColor);

        if (!rotationFrames.isEmpty()) {
            InterpolatedFrameGenerator<TextDisplay, Float> rotationBuilder = buildInterpolatedKeyframes(TextDisplayParticleBuilder::interpolateRotation, 0f)
                    .setEntitySetter((textDisplay, rotation) -> {
                        Transformation existing = textDisplay.getTransformation();

                        textDisplay.setTransformation(new Transformation(
                                new Vector3f(0, -1 / DEFAULT_TEXT_DISPLAY_HEIGHT / existing.getScale().y / 2, 0)
                                        .add(DUMB_TEXT_DISPLAY_FIX)
                                        .rotateZ(rotation),
                                new Quaternionf().rotateZ(rotation),
                                existing.getScale(),
                                existing.getRightRotation()
                        ));
                    });

            rotationFrames.forEach(rotationBuilder::setFrame);
            fillKeyframes("rotation", rotationBuilder);
        }

        if (!colorFrames.isEmpty()) {
            InterpolatedFrameGenerator<TextDisplay, Color> colourBuilder =
                    buildInterpolatedKeyframes(WbsColours::colourCircularLerp, Objects.requireNonNullElse(backgroundColor, Color.WHITE))
                            .setEntitySetter(TextDisplay::setBackgroundColor);

            colorFrames.forEach(colourBuilder::setFrame);
            fillKeyframes("color", colourBuilder);
        }

        super.configure(display);
    }

    public TextDisplayParticleBuilder setBackgroundColor(@Nullable Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public TextDisplayParticleBuilder setText(@Nullable TextComponent text) {
        this.text = text;
        return this;
    }

    public TextDisplayParticleBuilder setRotationDynamicKeyframe(int tick, float radians) {
        rotationFrames.put(tick, radians);

        return this;
    }

    public TextDisplayParticleBuilder setRotationDynamicKeyframe(@Range(from = 0, to = 1) double progress, float radians) {
        if (maxAge <= 0) {
            throw new IllegalStateException("Cannot set relative keyframe before maxAge is set.");
        }

        int closestTick = (int) Math.clamp((((double) maxAge) * progress), 0, maxAge);

        return setRotationDynamicKeyframe(closestTick, radians);
    }

    public TextDisplayParticleBuilder setColorKeyframe(int tick, Color color) {
        colorFrames.put(tick, color);

        return this;
    }

    public TextDisplayParticleBuilder setColorKeyframe(@Range(from = 0, to = 1) double progress, Color color) {
        if (maxAge <= 0) {
            throw new IllegalStateException("Cannot set relative keyframe before maxAge is set.");
        }

        int closestTick = (int) Math.clamp((((double) maxAge) * progress), 0, maxAge);

        return setColorKeyframe(closestTick, color);
    }
}
