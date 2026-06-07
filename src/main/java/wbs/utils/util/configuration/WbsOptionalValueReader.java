package wbs.utils.util.configuration;

import io.papermc.paper.registry.RegistryKey;
import org.apache.commons.lang3.NumberRange;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import wbs.utils.exceptions.InvalidConfigurationException;

import java.util.function.Function;

/**
 * Helper class for {@link WbsValueReader}, where isRequired is always false, allowing annotations & code hints to be more reliable
 */
@NullMarked
public class WbsOptionalValueReader extends WbsValueReader {
    public WbsOptionalValueReader(ConfigurationSection section, String key, @Nullable String sectionDirectory) {
        super(section, key, sectionDirectory);
        
        super.isRequired(false);
    }

    @Contract("_ -> fail")
    @Override
    public WbsValueReader isRequired(boolean required) {
        throw new UnsupportedOperationException("Required value reader is always in required mode.");
    }

    @Override
    public <T> @Nullable T validateNullability(@Nullable T value) throws InvalidConfigurationException {
        return super.validateNullability(value);
    }

    @Override
    public @Nullable <T> T validateNullability(@Nullable T value, @Nullable String customRequiredError) throws InvalidConfigurationException {
        return super.validateNullability(value, customRequiredError);
    }

    @Override
    public @Nullable <T> T validateNullability(@Nullable T value, @Nullable String customRequiredError, String errorIfKeyExists) throws InvalidConfigurationException {
        return super.validateNullability(value, customRequiredError, errorIfKeyExists);
    }

    @Override
    public <T> @Nullable T readFromChildSection(ConfigurationSection section, String key, Function<WbsValueReader, @UnknownNullability T> function) {
        return super.readFromChildSection(section, key, function);
    }

    @Override
    public @Nullable Object read() {
        return super.read();
    }

    @Override
    public <T> @Nullable T readFromOtherSection(ConfigurationSection section, String key, @Nullable String sectionDirectory, Function<WbsValueReader, @UnknownNullability T> function) {
        return super.readFromOtherSection(section, key, sectionDirectory, function);
    }

    @Override
    public @Nullable ConfigurationSection readSection() {
        return super.readSection();
    }

    @Override
    public <E extends Enum<E>> @Nullable E readEnum(Class<E> clazz) {
        return super.readEnum(clazz);
    }

    @Override
    public <E extends Enum<E>> @Nullable E readEnum(Class<E> clazz, @Nullable E defaultValue) {
        return super.readEnum(clazz, defaultValue);
    }

    @Override
    public @Nullable NamespacedKey readNamespacedKey(@Nullable Plugin defaultNamespace) {
        return super.readNamespacedKey(defaultNamespace);
    }

    @Override
    public @Nullable NamespacedKey readNamespacedKey(@Nullable Plugin defaultNamespace, @Nullable Keyed defaultKeyHolder) {
        return super.readNamespacedKey(defaultNamespace, defaultKeyHolder);
    }

    @Override
    public @Nullable NamespacedKey readNamespacedKey(@Nullable Plugin defaultNamespace, @Nullable NamespacedKey defaultValue) {
        return super.readNamespacedKey(defaultNamespace, defaultValue);
    }

    @Override
    public <T extends Keyed> @Nullable T readRegistryEntry(RegistryKey<T> registryKey) {
        return super.readRegistryEntry(registryKey);
    }

    @Override
    public <T extends Keyed> @Nullable T readRegistryEntry(RegistryKey<T> registryKey, @Nullable T defaultValue) {
        return super.readRegistryEntry(registryKey, defaultValue);
    }

    @Override
    public @Nullable Vector readVector() {
        return super.readVector();
    }

    @Override
    public @Nullable Vector readVector(@Nullable Vector defaultValue) {
        return super.readVector(defaultValue);
    }

    @Override
    public @Nullable Vector readVector(@Nullable Vector defaultValue, String xName, String yName, String zName) {
        return super.readVector(defaultValue, xName, yName, zName);
    }

    @Override
    public @Nullable Number readNumber(@Nullable Number defaultValue) {
        return super.readNumber(defaultValue);
    }

    @Override
    public @Nullable Number readNumber() {
        return super.readNumber();
    }

    @Override
    public @Nullable NumberRange<Number> readNumberRange(@Nullable NumberRange<Number> defaultValue) {
        return super.readNumberRange(defaultValue);
    }
}
