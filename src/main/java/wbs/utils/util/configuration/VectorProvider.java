package wbs.utils.util.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.plugin.WbsSettings;

public class VectorProvider {

    private NumProvider x, y, z;

    public VectorProvider(Vector vector) {
        this.x = new NumProvider(vector.getX());
        this.y = new NumProvider(vector.getY());
        this.z = new NumProvider(vector.getZ());
    }

    public VectorProvider(ConfigurationSection section, WbsSettings settings, String directory, Vector defaultVector) {
        WbsConfigReader.requireNotNull(section, "x", settings, directory);
        WbsConfigReader.requireNotNull(section, "y", settings, directory);
        WbsConfigReader.requireNotNull(section, "z", settings, directory);

        x = new NumProvider(section, "x", settings, directory, defaultVector.getX());
        y = new NumProvider(section, "y", settings, directory, defaultVector.getY());
        z = new NumProvider(section, "z", settings, directory, defaultVector.getZ());
    }

    public VectorProvider(ConfigurationSection section, WbsSettings settings, String directory) {
        WbsConfigReader.requireNotNull(section, "x", settings, directory);
        WbsConfigReader.requireNotNull(section, "y", settings, directory);
        WbsConfigReader.requireNotNull(section, "z", settings, directory);

        x = new NumProvider(section, "x", settings, directory);
        y = new NumProvider(section, "y", settings, directory);
        z = new NumProvider(section, "z", settings, directory);
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
        return new Vector(x.val(), y.val(), z.val());
    }

    public double getX() {
        return x.val();
    }
    public double getY() {
        return y.val();
    }
    public double getZ() {
        return z.val();
    }

    public void writeToConfig(ConfigurationSection section, String path) {
        x.writeToConfig(section, path + ".x");
        y.writeToConfig(section, path + ".y");
        z.writeToConfig(section, path + ".z");
    }

    public void refresh() {
        x.refresh();
        y.refresh();
        z.refresh();
    }
}
