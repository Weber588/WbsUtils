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
