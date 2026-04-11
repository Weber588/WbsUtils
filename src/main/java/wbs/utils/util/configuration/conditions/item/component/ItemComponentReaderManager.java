package wbs.utils.util.configuration.conditions.item.component;

import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.Nullable;
import wbs.utils.util.configuration.ConfigConstructor;
import wbs.utils.util.configuration.SectionConfigConstructor;
import wbs.utils.util.plugin.WbsSettings;

import java.util.HashSet;
import java.util.Set;

public class ItemComponentReaderManager {
    private static final Set<RegisteredReader> READERS = new HashSet<>();

    static {
        register("attribute", AttributeValueReader::new);
        register("damage", DamageReader::new);
    }

    public static void register(String regex, SectionConfigConstructor<? extends ItemComponentReader<?>> constructor) {
        READERS.add(new RegisteredReader(regex, constructor));
    }
    public static void register(String regex, ConfigConstructor<? extends ItemComponentReader<?>> constructor) {
        READERS.add(new RegisteredReader(regex, constructor));
    }

    public static ItemComponentReader<?> buildReader(ConfigurationSection parent, String key, @Nullable WbsSettings settings, @Nullable String directory) {
        return READERS.stream()
                .filter(reader -> key.matches(reader.regex))
                .findFirst()
                .map(registration -> registration.constructor.construct(parent, key, settings, directory))
                .orElse(null);
    }

    private record RegisteredReader(
            String regex,
            ConfigConstructor<? extends ItemComponentReader<?>> constructor
    ) {}
}
