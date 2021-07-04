package wbs.utils.util.configuration.generator.vector;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.WbsMath;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.VectorProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.configuration.generator.num.PulseGenerator;
import wbs.utils.util.plugin.WbsSettings;

public class VectorGeneratorRotate extends VectorGenerator {

    private VectorProvider about;
    private NumProvider period;
    private NumProvider radius;
    private Vector value;
    private double progress;
    private double step;

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
    public void refresh() {
        about.refresh();
        period.refresh();

        step = 1 / period.val();

        super.refresh();
    }

    @Override
    protected Vector getNewValue() {
        progress += step;

        progress %= 1;

        // Rotate around the y axis, and then rotate to the about vector
        double x = Math.sin((progress + 0.25) * 2 * Math.PI) * radius.val();
        double y = 0;
        double z = Math.sin((progress) * 2 * Math.PI) * radius.val();

        value = new Vector(x, y, z);

        return WbsMath.rotateFrom(value, new Vector(0, 1, 0), about.val());
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        period.writeToConfig(section, path);
        about.writeToConfig(section, path);
        radius.writeToConfig(section, path);
    }
}
