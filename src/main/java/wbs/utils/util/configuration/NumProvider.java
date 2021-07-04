package wbs.utils.util.configuration;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.exceptions.MissingRequiredKeyException;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.configuration.generator.num.DoubleGenerator;
import wbs.utils.util.plugin.WbsSettings;

import java.util.Set;

public class NumProvider {

    private DoubleGenerator generator;
    private boolean staticField = true;
    private double staticValue = Double.MIN_VALUE;

    private NumProvider() {}

    public NumProvider(DoubleGenerator generator) {
        this.generator = generator;
        staticField = false;
    }

    public NumProvider(double staticValue) {
        this.staticValue = staticValue;
    }

    public NumProvider(ConfigurationSection section, String field, WbsSettings settings, String directory) {
        this(section, field, settings, directory, 0);
    }

    public NumProvider(ConfigurationSection section, String field, WbsSettings settings, String directory, double defaultValue) {
        if (section.getDouble(field, Double.MIN_VALUE) != Double.MIN_VALUE) {
            staticField = true;
            staticValue = section.getDouble(field, defaultValue);
        } else {
            WbsConfigReader.requireNotNull(section, field, settings, directory);
            ConfigurationSection providerSection = section.getConfigurationSection(field);
            assert providerSection != null;

            staticField = false;

            Set<String> providerKeys = providerSection.getKeys(false);
            if (providerKeys.isEmpty()) {
                settings.logError("You must specify either a provider or a number.", directory);
                throw new MissingRequiredKeyException();
            } else if (providerKeys.size() > 1) {
                settings.logError("Too many sections. Choose a single provider from the following: " + String.join(", ", WbsEnums.toStringList(DoubleGenerator.GeneratorType.class)), directory);
                throw new InvalidConfigurationException();
            }

            String typeString = (String) providerKeys.toArray()[0];

            generator = DoubleGenerator.buildGenerator(typeString, providerSection.getConfigurationSection(typeString), settings, directory);
        }
    }

    /**
     * Generate a new value
     */
    public void refresh() {
        if (!staticField)
            generator.refresh();
    }

    public double val() {
        if (staticField) {
            return staticValue;
        }

        return generator.getValue();
    }

    public int intVal() {
        if (staticField) {
            return (int) staticValue;
        }

        return (int) generator.getValue();
    }

    public void writeToConfig(ConfigurationSection section, String path) {
        if (staticField) {
            section.set(path, staticValue);
        } else {
            generator.writeToConfig(section, path);
        }
    }
}
