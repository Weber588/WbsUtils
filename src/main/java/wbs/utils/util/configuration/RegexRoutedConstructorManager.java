package wbs.utils.util.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import wbs.utils.util.plugin.WbsSettings;

import java.util.LinkedList;
import java.util.Optional;

@NullMarked
public class RegexRoutedConstructorManager<T> {
    private final LinkedList<RegisteredRegexRoute> registeredRegexRoutes = new LinkedList<>();

    private final Class<T> classToConstruct;

    public RegexRoutedConstructorManager(Class<T> classToConstruct) {
        this.classToConstruct = classToConstruct;
    }

    public void register(String regex, TrivialConfigConstructor<T> constructor) {
        register(regex, (ConfigConstructor<T>) constructor);
    }
    public void register(String regex, SectionConfigConstructor<T> constructor) {
        register(regex, (ConfigConstructor<T>) constructor);
    }
    public void register(String regex, ConfigConstructor<T> constructor) {
        registeredRegexRoutes.add(new RegisteredRegexRoute(regex, constructor));
    }

    @Nullable
    public T build(ConfigurationSection parent, String key, @Nullable WbsSettings settings, @Nullable String directory) {
        return getRegistration(key)
                .map(registration -> registration.constructor.construct(parent, key, settings, directory))
                .orElse(null);
    }

    public Optional<RegisteredRegexRoute> getRegistration(String key) {
        return registeredRegexRoutes.stream()
                .filter(reg -> reg.matches(key))
                .findFirst();
    }

    @Nullable
    public ConfigConstructor<T> getConstructor(String key) {
        return getRegistration(key).map(RegisteredRegexRoute::constructor).orElse(null);
    }

    public final class RegisteredRegexRoute {
        private final String regex;
        private final ConfigConstructor<T> constructor;

        public RegisteredRegexRoute(
                String regex,
                ConfigConstructor<T> constructor
        ) {
            this.regex = regex;
            this.constructor = constructor;
        }

        public boolean matches(String key) {
                return key.matches(regex);
            }

        public String regex() {
            return regex;
        }

        public ConfigConstructor<T> constructor() {
            return constructor;
        }
    }
}
