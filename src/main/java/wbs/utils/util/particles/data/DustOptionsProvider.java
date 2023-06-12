package wbs.utils.util.particles.data;

import com.google.common.annotations.Beta;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.exceptions.MissingRequiredKeyException;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.plugin.WbsSettings;
import wbs.utils.util.providers.Provider;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.providers.VectorProvider;

import java.util.function.Function;

/**
 *  Provider that implements {@link org.bukkit.Particle.DustOptions} for the purpose of allowing
 *  particle data to be refreshed and written to a config.
 */
@Beta
@SuppressWarnings("unused")
public class DustOptionsProvider extends Particle.DustOptions implements Provider {


    @NotNull
    private final VectorProvider colourProvider;
    @NotNull
    private final NumProvider sizeProvider;
    @NotNull
    private final ColourType type;

    /**
     * @param colourProvider A vector provider that represents a colour,
     *                       stored in format defined by {@link ColourType}.
     * @param sizeProvider A number provider to represent the size of the redstone dust,
     *                     between 0 (exclusive) and 2 (inclusive).
     * @param type The format of the colour vector.
     * @see ColourType
     */
    public DustOptionsProvider(@NotNull VectorProvider colourProvider, NumProvider sizeProvider, ColourType type) {
        super(type.parse(colourProvider), (float) sizeProvider.val());

        this.colourProvider = colourProvider;
        this.sizeProvider = sizeProvider;
        this.type = type;
    }

    /**
     * @param colourProvider A vector provider that represents a colour,
     *                       stored in format defined by {@link ColourType}.
     * @param size A number to represent the size of the redstone dust,
     *                     between 0 (exclusive) and 2 (inclusive).
     * @param type The format of the colour vector.
     * @see ColourType
     */
    public DustOptionsProvider(VectorProvider colourProvider, float size, ColourType type) {
        this(colourProvider, new NumProvider(size), type);
    }

    /**
     * @param section The config to read from.
     * @param path The path within the given config section to read from.
     * @param settings The settings for logging purposes.
     * @param directory The path taken so far, for logging purposes.
     * @throws InvalidConfigurationException If the config is misconfigured in an unrecoverable way.
     */
    public DustOptionsProvider(ConfigurationSection section, String path, WbsSettings settings, String directory) throws InvalidConfigurationException {
        super(Color.RED, 1);

        sizeProvider = new NumProvider(section, "size", settings, directory + "/size", 1);

        ConfigurationSection colourSection = section.getConfigurationSection("colour");
        if (colourSection == null)
            throw new MissingRequiredKeyException("colour is a required field.");

        colourProvider = new VectorProvider(colourSection, settings, directory + "/colour");

        String typeString = section.getString("type");
        if (typeString == null)
            throw new MissingRequiredKeyException("type is a required field.");

        //noinspection ConstantConditions
        type = WbsEnums.getEnumFromString(ColourType.class, typeString);

        if (type == null)
            throw new InvalidConfigurationException("Invalid type: " + typeString + ".");
    }

    @NotNull
    @Override
    public Color getColor() {
        return type.parse(colourProvider);
    }

    @Override
    public float getSize() {
        return (float) sizeProvider.val();
    }

    @Override
    public void refresh() {
        colourProvider.refresh();
        sizeProvider.refresh();
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        sizeProvider.writeToConfig(section, path + ".size");
        section.set(path + ".type", type.name());
        colourProvider.writeToConfig(section, path + ".colour");
    }

    /**
     * Determines how the {@link VectorProvider} is treated as a colour
     */
    public enum ColourType {
        /**
         * Uses {@link VectorProvider#colourVal255()}
         */
        INT255(VectorProvider::colourVal255),
        /**
         * Uses {@link VectorProvider#colourValDecimal()}
         */
        DECIMAL(VectorProvider::colourValDecimal),
        /**
         * Uses {@link VectorProvider#colourValHSB()}
         */
        HSB(VectorProvider::colourValHSB)
        ;

        private final Function<VectorProvider, Color> function;

        ColourType(Function<VectorProvider, Color> function) {
            this.function = function;
        }

        /**
         * Gets a colour from the given provider, in the format of this enum instance.
         * @param provider The provider to be queried and immediately converted into the appropriate {@link Color}
         * @return The colour represented by the current value of the given provider.
         */
        public Color parse(VectorProvider provider) {
            return function.apply(provider);
        }
    }
}
