package wbs.utils.util.configuration.conditions;

import org.jetbrains.annotations.Nullable;

public interface ConfigurableCondition {
    default boolean canTest(@Nullable Object object) {
        return ConfigurableConditionManager.canTest(this, object);
    }
    default boolean test(Object object) {
        return test(object, false);
    }
    default boolean test(Object object, boolean defaultWhenNoCorrectType) {
        return ConfigurableConditionManager.test(this, object, defaultWhenNoCorrectType);
    }
}
