package wbs.utils.util.configuration;

import com.google.common.annotations.Beta;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.exceptions.InvalidWbsConfigDefinitionException;
import wbs.utils.util.configuration.options.WbsIntOption;

import java.io.InvalidClassException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Similar to {@link ConfigurationSection}, but with support
 * for runtime configuration and class-specific options
 * with an annotation system. <p/>
 * Also serves as a wrapper for ConfigurationSections.
 */
@Beta
public class WbsConfig {
    public WbsConfig() {}

    private final Map<String, WbsConfigOption<?, ?>> options = new HashMap<>();

    public void configure(@NotNull ConfigurationSection section) {
        // TODO: Iterate through options.keySet() and try reading values
    }

    public void addAnnotations(@NotNull Class<?> clazz) {
        Annotation[] annotations = clazz.getAnnotations();

        for (Annotation annotation : annotations) {
            try {
                addAnnotation(annotation, clazz);
            } catch (InvalidClassException e) {
                e.printStackTrace();
            }
        }
    }

    private void addAnnotation(Annotation annotation, Class<?> clazz) throws InvalidClassException {
        if (annotation.annotationType().isAnnotationPresent(WbsOption.class)) {
            WbsOption optionClassDeclaration = annotation.annotationType().getAnnotation(WbsOption.class);

            Class<? extends WbsConfigOption<?, ?>> optionClass = optionClassDeclaration.value();

            Method valueMethod;
            try {
                valueMethod = annotation.annotationType().getMethod("value");
            } catch (NoSuchMethodException e) {
                throw new InvalidClassException(
                        "Annotation type " + annotation.annotationType().getSimpleName() +
                                " is annotated " + WbsOption.class.getSimpleName() + // Easier for refactoring if it comes up
                                " but lacks the value() method.");
            }

            if (valueMethod.getReturnType() != String.class) {
                throw new InvalidClassException(
                        "Annotation type " + annotation.annotationType().getSimpleName() +
                                " implements the value() method, but the return value is not String.");
            }

            String optionName;
            try {
                optionName = (String) valueMethod.invoke(annotation.annotationType());
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return;
            }

            Constructor<? extends WbsConfigOption<?, ?>> constructor;
            try {
                constructor = optionClass.getConstructor(WbsConfig.class, annotation.annotationType());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                return;
            }

            WbsConfigOption<?, ?> newOption;
            try {
                newOption = constructor.newInstance(this, annotation);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return;
            }

            optionName = optionName.toLowerCase();

            if (options.containsKey(optionName)) {
                throw new InvalidWbsConfigDefinitionException(
                        "A duplicate config option was defined for "
                                + clazz.getSimpleName() + ": " + optionName + ".");
            }

            addAnnotations(newOption.getClass());

            options.put(optionName, newOption);
        }
    }

    public <T extends WbsConfigOption<?, ?>> T getOption(String key, Class<T> clazz) {
        WbsConfigOption<?, ?> option = options.get(key);

        if (clazz.isInstance(option)) {
            return clazz.cast(option);
        } else {
            throw new InvalidConfigurationException("Given key (" + key +
                    ") does not match requested option type (" + clazz.getSimpleName() +
                    ").");
        }
    }

    public int getInt(String key) {
        return getOption(key, WbsIntOption.class).getValue();
    }
}
