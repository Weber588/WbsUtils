package wbs.utils.util.particles.entity.interpolation;

import org.bukkit.entity.Entity;
import wbs.utils.util.particles.entity.EntityParticle;

@FunctionalInterface
public interface ParticleSetter<T extends Entity, V> {
    void set(EntityParticle<T> particle, V value);
}
