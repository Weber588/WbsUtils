package wbs.utils.util.providers.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.WbsMath;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.providers.VectorProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A generator that, over a defined period, returns a vector that rotates around the
 * origin with a radius defined by a NumProvider, where the axis about which it rotates
 * is defined by a VectorProvider
 */
public class VectorGeneratorRotate extends VectorGenerator {

    private final VectorProvider about;
    private final NumProvider period;
    private final NumProvider radius;
    private double progress;
    private double step;

    public VectorGeneratorRotate(VectorGeneratorRotate clone) {
        about = new VectorProvider(clone.about);
        period = new NumProvider(clone.period);
        radius = new NumProvider(clone.radius);

        progress = clone.progress;
        step = clone.step;
    }

    public VectorGeneratorRotate(VectorProvider about, NumProvider period, NumProvider radius, double initialProgress) {
        this.about = about;
        this.period = period;
        this.radius = radius;

        progress = Math.abs(initialProgress);
        step = 1.0 / period.val();
    }

    public VectorGeneratorRotate(Vector about, double period, double radius, double initialProgress) {
        this.about = new VectorProvider(about);
        this.period = new NumProvider(period);
        this.radius = new NumProvider(radius);

        progress = Math.abs(initialProgress);
        step = 1.0 / this.period.val();
    }

    /**
     * Create this generator from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this generator is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public VectorGeneratorRotate(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory);

        period = new NumProvider(section, "period", settings, directory, 20);
        // Don't log errors for the second one as it's just repeating the first

        ConfigurationSection aboutSection = WbsConfigReader.getRequiredSection(section, "about", settings, directory);
        about = new VectorProvider(aboutSection, settings, directory, new Vector(0, 1, 0));

        if (section.get("initialProgress") != null) {
            progress = section.getDouble("initialProgress", 0);
        } else {
            progress = 0;
        }

        if (section.get("radius") != null) {
            radius = new NumProvider(section, "radius", settings, directory, 1);
        } else {
            radius = new NumProvider(1);
        }

        step = 1 / period.val();
    }

    @Override
    public void refreshInternal() {
        about.refresh();
        period.refresh();

        step = 1 / period.val();
    }

    @Override
    protected Vector getNewValue() {
        progress += step;

        progress %= 1;

        // Rotate around the y axis, and then rotate to the about vector
        double x = Math.sin((progress + 0.25) * 2 * Math.PI) * radius.val();
        double y = 0;
        double z = Math.sin((progress) * 2 * Math.PI) * radius.val();

        Vector value = new Vector(x, y, z);

        return WbsMath.rotateFrom(value, new Vector(0, 1, 0), about.val());
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        period.writeToConfig(section, path);
        about.writeToConfig(section, path);
        radius.writeToConfig(section, path);
    }

    @Override
    public VectorGeneratorRotate clone() {
        return new VectorGeneratorRotate(this);
    }
}
