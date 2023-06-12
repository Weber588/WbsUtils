package wbs.utils.util.particles;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.WbsMath;
import wbs.utils.util.plugin.WbsSettings;

import java.util.LinkedList;
import java.util.List;

/**
 * A particle effect that creates a Fibonacci sphere
 */
public class SphereParticleEffect extends CircleParticleEffect {

    public SphereParticleEffect() {
        super();
    }

    /**
     * Create this effect from a ConfigurationSection, logging errors in the given settings
     * @param section The section where this effect is defined
     * @param settings The settings to log errors against
     * @param directory The path taken through the config to get to this point, for logging purposes
     */
    public SphereParticleEffect(ConfigurationSection section, WbsSettings settings, String directory) {
        super(section, settings, directory);
    }

    @Override
    public SphereParticleEffect build() {
        points.clear();
        refreshProviders();

        List<Vector> tempPoints = new LinkedList<>(
                WbsMath.getFibonacciSphere(amount.intVal(), radius.val())
        );

        tempPoints = WbsMath.rotateVectors(tempPoints, about.val(), rotation.val());

        points.addAll(tempPoints);
        return this;
    }

    @Override
    public SphereParticleEffect clone() {
        SphereParticleEffect cloned = new SphereParticleEffect();
        this.cloneInto(cloned);

        return cloned;
    }
}
