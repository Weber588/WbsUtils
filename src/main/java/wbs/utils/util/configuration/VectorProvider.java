package wbs.utils.util.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.exceptions.MissingRequiredKeyException;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.configuration.generator.num.DoubleGenerator;
import wbs.utils.util.configuration.generator.vector.VectorGenerator;
import wbs.utils.util.plugin.WbsSettings;

import java.util.Set;

/**
 * Represents a vector that may either be static, or change over time
 */
public class VectorProvider {

    private NumProvider x, y, z;

    private VectorGenerator generator;
    private boolean staticField = true;

    /**
     * Create a static vector provider with the given vector
     * @param vector The static vector to return
     */
    public VectorProvider(Vector vector) {
        this.x = new NumProvider(vector.getX());
        this.y = new NumProvider(vector.getY());
        this.z = new NumProvider(vector.getZ());
    }

    /**
     * Create this provider with a given, pre-configured VectorGenerator
     * @param generator The VectorGenerator to generate numbers
     */
    public VectorProvider(VectorGenerator generator) {
        this.generator = generator;
        staticField = false;
    }

    /**
     * Create this provider from a configuration section, and log any errors
     * against the given settings.
     * @param section The section containing the x, y, z values. These may be NumProviders.
     * @param settings The WbsSettings to log errors against
     * @param directory The path taken through the section to reach this provider, for
     *                  logging purposes
     * @param defaultVector The default vector to use in case any NumProviders are malformed
     */
    public VectorProvider(ConfigurationSection section, WbsSettings settings, String directory, Vector defaultVector) {
        if (section.get("x") != null) {
            WbsConfigReader.requireNotNull(section, "x", settings, directory);
            WbsConfigReader.requireNotNull(section, "y", settings, directory);
            WbsConfigReader.requireNotNull(section, "z", settings, directory);

            staticField = true;

            x = new NumProvider(section, "x", settings, directory, defaultVector.getX());
            y = new NumProvider(section, "y", settings, directory, defaultVector.getY());
            z = new NumProvider(section, "z", settings, directory, defaultVector.getZ());
        } else {
            staticField = false;

            Set<String> providerKeys = section.getKeys(false);
            if (providerKeys.isEmpty()) {
                settings.logError("You must specify either a provider or a vector.", directory);
                throw new MissingRequiredKeyException();
            } else if (providerKeys.size() > 1) {
                settings.logError(
                        "Too many sections. Choose a single provider from the following: "
                        + String.join(", ", WbsEnums.toStringList(VectorGenerator.VectorGeneratorType.class)),
                        directory);
                throw new InvalidConfigurationException();
            }

            String typeString = (String) providerKeys.toArray()[0];

            generator = VectorGenerator.buildGenerator(typeString, section.getConfigurationSection(typeString), settings, directory);
        }
    }

    /**
     * Create this provider from a configuration section, and log any errors
     * against the given settings.
     * @param section The section containing the x, y, z values. These may be NumProviders.
     * @param settings The WbsSettings to log errors against
     * @param directory The path taken through the section to reach this provider, for
     *                  logging purposes
     */
    public VectorProvider(ConfigurationSection section, WbsSettings settings, String directory) {
        this(section, settings, directory, new Vector(0, 1, 0));
    }

    /**
     * Create this vector provider with given NumProviders directly
     * @param x The x provider
     * @param y The y provider
     * @param z The z provider
     */
    public VectorProvider(NumProvider x, NumProvider y, NumProvider z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Create a static provider using static values
     * @param x The x component
     * @param y The y component
     * @param z The z component
     */
    public VectorProvider(double x, double y, double z) {
        this.x = new NumProvider(x);
        this.y = new NumProvider(y);
        this.z = new NumProvider(z);
    }

    /**
     * @return The current value
     */
    public Vector val() {
        if (staticField) {
            return new Vector(x.val(), y.val(), z.val());
        }

        return generator.getValue();
    }

    /**
     * @return The current x component
     */
    public double getX() {
        return val().getX();
    }
    /**
     * @return The current y component
     */
    public double getY() {
        return val().getY();
    }
    /**
     * @return The current z component
     */
    public double getZ() {
        return val().getZ();
    }

    /**
     * Save this provider in a config that can be read by the constructor
     * @param section The section to write to
     * @param path The field/path inside the given section
     */
    public void writeToConfig(ConfigurationSection section, String path) {
        if (staticField) {
            x.writeToConfig(section, path + ".x");
            y.writeToConfig(section, path + ".y");
            z.writeToConfig(section, path + ".z");
        } else {
            generator.writeToConfig(section, path);
        }
    }

    /**
     * Refresh the providers that make up the vector, or the generator
     */
    public void refresh() {
        if (staticField) {
            x.refresh();
            y.refresh();
            z.refresh();
        } else {
            generator.refresh();
        }
    }
}
