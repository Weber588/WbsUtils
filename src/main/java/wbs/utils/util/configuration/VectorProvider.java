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

public class VectorProvider {

    private NumProvider x, y, z;

    private VectorGenerator generator;
    private boolean staticField = true;

    public VectorProvider(Vector vector) {
        this.x = new NumProvider(vector.getX());
        this.y = new NumProvider(vector.getY());
        this.z = new NumProvider(vector.getZ());
    }

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

    public VectorProvider(ConfigurationSection section, WbsSettings settings, String directory) {
        this(section, settings, directory, new Vector(0, 1, 0));
    }

    public VectorProvider(NumProvider x, NumProvider y, NumProvider z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public VectorProvider(double x, double y, double z) {
        this.x = new NumProvider(x);
        this.y = new NumProvider(y);
        this.z = new NumProvider(z);
    }

    public Vector val() {
        if (staticField) {
            return new Vector(x.val(), y.val(), z.val());
        }

        return generator.getValue();
    }

    public double getX() {
        return val().getX();
    }
    public double getY() {
        return val().getY();
    }
    public double getZ() {
        return val().getZ();
    }

    public void writeToConfig(ConfigurationSection section, String path) {
        if (staticField) {
            x.writeToConfig(section, path + ".x");
            y.writeToConfig(section, path + ".y");
            z.writeToConfig(section, path + ".z");
        } else {
            generator.writeToConfig(section, path);
        }
    }

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
