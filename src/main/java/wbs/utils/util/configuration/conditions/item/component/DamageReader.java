package wbs.utils.util.configuration.conditions.item.component;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

@SuppressWarnings("UnstableApiUsage")
public class DamageReader implements ItemComponentReader<Integer> {
    private final Method method;

    public DamageReader(ConfigurationSection parent, String key, @Nullable WbsSettings settings, @Nullable String directory) {
        ConfigurationSection section = parent.getConfigurationSection(key);

        if (section != null) {
            method = WbsConfigReader.getRequiredEnum(section, "method", settings, directory, Method.class);
        } else {
            method = WbsConfigReader.getRequiredEnum(parent, key, settings, directory, Method.class);
        }
    }

    @Override
    @Nullable
    public Integer read(ItemStack item) {
        final Integer damage = item.getData(DataComponentTypes.DAMAGE);
        return switch (method) {
            case TAKEN -> damage;
            case REMAINING -> {
                Integer maxDamage = item.getData(DataComponentTypes.MAX_DAMAGE);
                if (damage != null && maxDamage != null) {
                    yield maxDamage - damage;
                }
                yield null;
            }
        };
    }

    public enum Method {
        TAKEN,
        REMAINING,
    }
}
