package wbs.utils.util.particles;

import org.bukkit.Particle;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ConfiguredParticle<T> {
    public static ConfiguredParticle<?> withDefaults(Particle particle) {
        return withDefaults(particle, null);
    }
    public static ConfiguredParticle<?> withDefaults(Particle particle, @Nullable Object possibleData) {
        return withDefaults(particle, null, ParticleDataProvider.DEFAULT);
    }

    /**
     * Gets a configured particle with pre-configured defaults
     * @param particle The particle to wrap and use data from
     * @param possibleData Data that should be used, if it's valid for the particle's data type.
     * @param provider A provider to build data for any given particle.
     * @return The wrapped particle with data from the provider, or from the given possibleData parameter.
     */
    public static ConfiguredParticle<?> withDefaults(Particle particle, @Nullable Object possibleData, ParticleDataProvider provider) {
        Object data = possibleData;

        boolean isValidData = ParticleDataProvider.isValidData(data, particle);

        if (!isValidData) {
            data = ParticleDataProvider.getData(particle, provider, null);
        }

        return new ConfiguredParticle<>(particle, data);
    }

    private final Particle particle;
    @UnknownNullability
    private T data;
    private final Class<T> dataType;

    public ConfiguredParticle(Particle particle, @UnknownNullability T data) {
        this.particle = particle;
        this.data = data;

        Class<?> dataType = particle.getDataType();
        boolean isValidData = ParticleDataProvider.isValidData(data, dataType);

        if (!isValidData) {
            throw new IllegalArgumentException("Particle data type does not match provided data.");
        }
        //noinspection unchecked
        this.dataType = (Class<T>) dataType;
    }

    public Particle particle() {
        return particle;
    }

    public @UnknownNullability T data() {
        return data;
    }

    public ConfiguredParticle<T> data(@UnknownNullability T data) {
        this.data = data;
        return this;
    }

    public Class<T> dataType() {
        return dataType;
    }
}
