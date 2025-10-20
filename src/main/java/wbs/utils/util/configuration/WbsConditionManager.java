package wbs.utils.util.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.Nullable;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.configuration.conditions.EntityCondition;
import wbs.utils.util.configuration.conditions.LocationCondition;
import wbs.utils.util.configuration.conditions.WorldCondition;
import wbs.utils.util.plugin.WbsSettings;

import java.util.HashMap;
import java.util.Map;

public class WbsConditionManager {
    private static final Map<String, ConditionGenerator> REGISTERED_CONDITIONS = new HashMap<>();

    public static void loadConditionTypes() {
        REGISTERED_CONDITIONS.put("world", WorldCondition::new);
        REGISTERED_CONDITIONS.put("location", LocationCondition.ExactLocationCondition::new);
        REGISTERED_CONDITIONS.put("eye_location", LocationCondition.EntityEyeLocationCondition::new);
        REGISTERED_CONDITIONS.put("below_location", LocationCondition.BelowLocationCondition::new);
        REGISTERED_CONDITIONS.put("above_location", LocationCondition.AboveLocationCondition::new);
        REGISTERED_CONDITIONS.put("entity", EntityCondition::new);
    }

    @Nullable
    public static ConfigurableCondition getCondition(WbsSettings settings, String directory, ConfigurationSection parent, String key) {
        ConditionGenerator generator = REGISTERED_CONDITIONS.get(key);

        if (generator != null) {
            try {
                return generator.generate(settings, directory, parent, key);
            } catch (InvalidConfigurationException ex) {
                settings.logError(ex.getMessage(), ex.getDirectory());
            }
        }

        settings.logError("Condition key not recognised: " + key, directory + "/key");

        return null;
    }

    @FunctionalInterface
    public interface ConditionGenerator {
        ConfigurableCondition generate(WbsSettings settings, String directory, ConfigurationSection parent, String key) throws InvalidConfigurationException;
    }
}
