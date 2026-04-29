package wbs.utils.util.configuration.conditions.item.component;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import wbs.utils.util.configuration.RegexRoutedConstructorManager;

@SuppressWarnings({"rawtypes", "UnstableApiUsage"})
public class ItemComponentReaderManager extends RegexRoutedConstructorManager<ItemComponentReader> {
    public static final ItemComponentReaderManager INSTANCE = new ItemComponentReaderManager(ItemComponentReader.class);

    static {
        RegistryAccess.registryAccess().getRegistry(RegistryKey.DATA_COMPONENT_TYPE).stream().forEach(type -> {
            if (type instanceof DataComponentType.Valued<?> valued) {
                //noinspection unchecked
                INSTANCE.register(getRegexForKey(valued), () -> new RawComponentReader(valued));
            }
        });

        INSTANCE.register("(minecraft:)?attribute((_|\\s)modifiers?)?", AttributeValueReader::new);
        INSTANCE.register(getRegexForKey(DataComponentTypes.DAMAGE), DamageReader::new);
    }

    private static String getRegexForKey(Keyed keyed) {
        return getRegexForKey(keyed.getKey());
    }
    private static String getRegexForKey(NamespacedKey key) {
        //noinspection RedundantEscapeInRegexReplacement
        return "(" + key.namespace() + ":)?" + (key.getKey().replaceAll("_", "(_|\\s)"));
    }

    private ItemComponentReaderManager(Class<ItemComponentReader> classToConstruct) {
        super(classToConstruct);
    }
}
