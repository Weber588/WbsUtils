package wbs.utils.util.providers.generator.num;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.providers.Provider;

/**
 * A generator that returns a single double until {@link #refresh()} is called,
 * at which point it calculates a new value.
 */
public abstract class DoubleGenerator implements Provider {

    protected DoubleGenerator() {}

    /**
     * Generate a new value to override the current one
     * @return The new value
     */
    protected abstract double getNewValue();

    public abstract void writeToConfig(ConfigurationSection section, String path);

    private double value;

    /**
     * Generate a new value.
     */
    public final void refresh() {
        refreshInternal();
        value = getNewValue();
    }

    /**
     * An internal method called before {@link #getNewValue()}, to allow
     * implementing classes to refresh the provider in a controlled way.
     */
    protected void refreshInternal() {}

    /**
     * @return The most recently generated value
     */
    public double getValue() {
        return value;
    }

    public abstract DoubleGenerator clone();
}
