package wbs.utils.util.particles.data;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.exceptions.MissingRequiredKeyException;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.plugin.WbsSettings;
import wbs.utils.util.providers.DataProvider;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.providers.VectorProvider;

import java.util.function.Function;

@SuppressWarnings("unused")
public class DustOptionsProvider extends Particle.DustOptions implements DataProvider {


    @NotNull
    private final VectorProvider colourProvider;
    @NotNull
    private final NumProvider sizeProvider;
    @NotNull
    private final ColourType type;

    public DustOptionsProvider(@NotNull VectorProvider colourProvider, NumProvider sizeProvider, ColourType type) {
        super(type.parse(colourProvider), (float) sizeProvider.val());

        this.colourProvider = colourProvider;
        this.sizeProvider = sizeProvider;
        this.type = type;
    }

    public DustOptionsProvider(VectorProvider colourProvider, float size, ColourType type) {
        this(colourProvider, new NumProvider(size), type);
    }

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

        public Color parse(VectorProvider provider) {
            return function.apply(provider);
        }
    }
}
