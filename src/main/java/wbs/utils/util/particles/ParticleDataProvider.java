package wbs.utils.util.particles;

import net.kyori.adventure.util.Ticks;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * A utility interface that can provide a default value for all known
 * Particle data types ({@link Particle#getDataType()}). Includes some
 * default values to build more complex values from other methods.
 */
@NullMarked
public interface ParticleDataProvider {
    static boolean isValidData(@org.jspecify.annotations.Nullable Object data, Particle particle) {
        return isValidData(data, particle.getDataType());
    }
    static boolean isValidData(@Nullable Object data, Class<?> dataType) {
        return (data == null && dataType == Void.class) || (data != null && dataType.isAssignableFrom(data.getClass()));
    }

    @Contract(mutates = "param1")
    static void playEffectSafely(WbsParticleEffect effect, Location location, Particle particle) {
        playEffectSafely(effect, location, particle, DEFAULT);
    }
    @Contract(mutates = "param1")
    static void playEffectSafely(WbsParticleEffect effect, Location location, Particle particle, ParticleDataProvider provider) {
        withSafeData(effect, location, particle, provider, () -> {
            effect.play(particle, location);
        });
    }

    static void withSafeData(WbsParticleEffect effect, Location location, Particle particle, ParticleDataProvider provider, Runnable runner) {
        Class<?> dataType = particle.getDataType();
        Object oldData = effect.getData();
        Object newData = oldData;

        boolean isValidData = isValidData(oldData, dataType);
        if (!isValidData) {
            newData = getData(particle, provider, location);
        }

        effect.setData(newData);
        runner.run();
        effect.setData(oldData);
    }

    default Color getColor(Particle particle, @Nullable Location location) {
        return Color.RED;
    }
    default Color getSecondaryColor(Particle particle, @Nullable Location location) {
        return getColor(particle, location);
    }

    default float getFloat(Particle particle, @Nullable Location location) {
        return 0;
    }
    default float getSize(Particle particle, @Nullable Location location) {
        return 1f;
    }
    default float getPower(Particle particle, @Nullable Location location) {
        return 1f;
    }

    default int getInt(Particle particle, @Nullable Location location) {
        return 0;
    }
    default int getDuration(Particle particle, @Nullable Location location) {
        return Ticks.TICKS_PER_SECOND;
    }

    default BlockData getBlockData(Particle particle, @Nullable Location location) {
        return Material.BARRIER.createBlockData();
    }

    default ItemStack getItemStack(Particle particle, @Nullable Location location) {
        return ItemStack.of(Material.BARRIER);
    }

    default Particle.DustOptions getDustOptions(Particle particle, @Nullable Location location) {
        return new Particle.DustOptions(getColor(particle, location), getSize(particle, location));
    }

    default Particle.Spell getSpell(Particle particle, @Nullable Location location) {
        return new Particle.Spell(getColor(particle, location), getPower(particle, location));
    }

    default Particle.DustTransition getDustTransition(Particle particle, @Nullable Location location) {
        return new Particle.DustTransition(getColor(particle, location), getSecondaryColor(particle, location), getSize(particle, location));
    }

    default Particle.Trail getTrail(Particle particle, @Nullable Location location) {
        return new Particle.Trail(getNonNullLocation(particle, location), getColor(particle, location), getDuration(particle, location));
    }

    default Location getNonNullLocation(Particle particle, @Nullable Location location) {
        return location != null ? location : Bukkit.getWorlds().getFirst().getSpawnLocation();
    }

    default Vibration getVibration(Particle particle, @Nullable Location location) {
        return new Vibration(new Vibration.Destination.BlockDestination(getNonNullLocation(particle, location)), getDuration(particle, location));
    }

    default Particle.Geyser getGeyser(Particle particle, @Nullable Location location) {
        return new Particle.Geyser(getInt(particle, location));
    }

    default Particle.GeyserBase getGeyserBase(Particle particle, @Nullable Location location) {
        return new Particle.GeyserBase(getInt(particle, location), getPower(particle, location));
    }

    ParticleDataProvider DEFAULT = new ParticleDataProvider() {};

    static Object getData(Particle particle, ParticleDataProvider provider, @Nullable Location location) {
        Object data;
        Class<?> dataType = particle.getDataType();
        if (dataType == Float.class) {
            data = provider.getFloat(particle, location);
        } else if (dataType == Integer.class) {
            data = provider.getInt(particle, location);
        } else if (dataType == BlockData.class) {
            data = provider.getBlockData(particle, location);
        } else if (dataType == Color.class) {
            data = provider.getColor(particle, location);
        } else if (dataType == Particle.DustOptions.class) {
            data = provider.getDustOptions(particle, location);
        } else if (dataType == Particle.Spell.class) {
            data = provider.getSpell(particle, location);
        } else if (dataType == Particle.DustTransition.class) {
            data = provider.getDustTransition(particle, location);
        } else if (dataType == Particle.Trail.class) {
            data = provider.getTrail(particle, location);
        } else if (dataType == Vibration.class) {
            data = provider.getVibration(particle, location);
        } else if (dataType == Particle.Geyser.class) {
            data = provider.getGeyser(particle, location);
        } else if (dataType == Particle.GeyserBase.class) {
            data = provider.getGeyserBase(particle, location);
        } else {
            throw new NotImplementedException(
                    "An unknown particle data type was requested! Has ParticleDataProvider been updated to the latest release?"
            );
        }

        return data;
    }
}
