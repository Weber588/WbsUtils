package wbs.utils.util.configuration;

import io.papermc.paper.registry.RegistryKey;
import org.apache.commons.lang.math.NumberRange;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import wbs.utils.exceptions.InvalidConfigurationException;

import java.util.function.Function;

/**
 * Helper class for {@link WbsValueReader}, where isRequired is always true, allowing annotations & code hints to be more reliable
 */
@SuppressWarnings("NullableProblems")
@NullMarked
public class WbsRequiredValueReader extends WbsValueReader {
    public WbsRequiredValueReader(ConfigurationSection section, String key, @Nullable String sectionDirectory) {
        super(section, key, sectionDirectory);
        
        super.isRequired(true);
    }

    @Contract("_ -> fail")
    @Override
    public @NotNull WbsValueReader isRequired(boolean required) {
        throw new UnsupportedOperationException("Required value reader is always in required mode.");
    }

    @Override
    public <T> @NotNull T validateNullability(@Nullable T value) throws InvalidConfigurationException {
        return super.validateNullability(value);
    }

    @Override
    public @NotNull <T> T validateNullability(@Nullable T value, @Nullable String customRequiredError) throws InvalidConfigurationException {
        return super.validateNullability(value, customRequiredError);
    }

    @Override
    public @NotNull <T> T validateNullability(@Nullable T value, @Nullable String customRequiredError, String errorIfKeyExists) throws InvalidConfigurationException {
        return super.validateNullability(value, customRequiredError, errorIfKeyExists);
    }

    @Override
    public <T> T readFromChildSection(ConfigurationSection section, String key, Function<WbsValueReader, @UnknownNullability T> function) {
        return super.readFromChildSection(section, key, function);
    }

    @Override
    public @NotNull Object read() {
        return super.read();
    }

    @Override
    public <T> T readFromOtherSection(ConfigurationSection section, String key, @Nullable String sectionDirectory, Function<WbsValueReader, @UnknownNullability T> function) {
        return super.readFromOtherSection(section, key, sectionDirectory, function);
    }

    @Override
    public @NotNull ConfigurationSection readSection() {
        return super.readSection();
    }

    @Override
    public <E extends Enum<E>> @NotNull E readEnum(Class<E> clazz) {
        return super.readEnum(clazz);
    }

    @Override
    public <E extends Enum<E>> @NotNull E readEnum(Class<E> clazz, @NotNull E defaultValue) {
        return super.readEnum(clazz, defaultValue);
    }

    @Override
    public @NotNull NamespacedKey readNamespacedKey(@Nullable Plugin defaultNamespace) {
        return super.readNamespacedKey(defaultNamespace);
    }

    @Override
    public @NotNull NamespacedKey readNamespacedKey(@Nullable Plugin defaultNamespace, @NotNull Keyed defaultKeyHolder) {
        return super.readNamespacedKey(defaultNamespace, defaultKeyHolder);
    }

    @Override
    public @NotNull NamespacedKey readNamespacedKey(@Nullable Plugin defaultNamespace, @NotNull NamespacedKey defaultValue) {
        return super.readNamespacedKey(defaultNamespace, defaultValue);
    }

    @Override
    public <T extends Keyed> @NotNull T readRegistryEntry(RegistryKey<T> registryKey) {
        return super.readRegistryEntry(registryKey);
    }

    @Override
    public <T extends Keyed> @NotNull T readRegistryEntry(RegistryKey<T> registryKey, @NotNull T defaultValue) {
        return super.readRegistryEntry(registryKey, defaultValue);
    }

    @Override
    public @NotNull Vector readVector() {
        return super.readVector();
    }

    @Override
    public @NotNull Vector readVector(@NotNull Vector defaultValue) {
        return super.readVector(defaultValue);
    }

    @Override
    public @NotNull Vector readVector(@NotNull Vector defaultValue, String xName, String yName, String zName) {
        return super.readVector(defaultValue, xName, yName, zName);
    }

    @Override
    public @NotNull Number readNumber(@NotNull Number defaultValue) {
        return super.readNumber(defaultValue);
    }

    @Override
    public @NotNull Number readNumber() {
        return super.readNumber();
    }

    @Override
    public @NotNull NumberRange readNumberRange(@NotNull NumberRange defaultValue) {
        return super.readNumberRange(defaultValue);
    }
}
