package wbs.utils.util.configuration.conditions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.configuration.ConfigConstructor;
import wbs.utils.util.configuration.TrivialConfigConstructor;
import wbs.utils.util.configuration.conditions.item.ItemComponentCondition;
import wbs.utils.util.plugin.WbsSettings;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class ConfigurableConditionManager {
    private static final List<RegisteredConditionType<?, ?>> CONDITION_TYPES = new LinkedList<>();
    private static final LinkedList<RegisteredCondition> CONDITIONS = new LinkedList<>();

    static {
        registerConditionType(BlockCondition.class, Block.class, BlockCondition::testBlock);

        registerConditionType(LocationCondition.class, Location.class, LocationCondition::testLocation);
        registerConditionType(ItemCondition.class, ItemStack.class, ItemCondition::testItem);
        registerConditionType(MaterialCondition.class, Material.class, MaterialCondition::testMaterial);
        registerConditionType(WorldCondition.class, World.class, WorldCondition::testWorld);
        registerConditionType(GenericCondition.class, Object.class, GenericCondition::testGeneric);
        registerConditionType(ExistsCondition.class, Object.class, ExistsCondition::testGeneric);

        //noinspection unchecked
        registerConditionType(EntityCondition.class, Entity.class, (condition, obj) -> {
            if (condition instanceof EntityCondition<?> entityCondition) {
                return entityCondition.getEntityClass().isAssignableFrom(obj.getClass());
            }
            return false;
        }, EntityCondition::testEntity);

        registerCondition("ALL_?(OF)?", AllCondition::new);
        registerCondition("ANY_?(OF)?", AnyCondition::new);
        registerCondition("COMPONENT", ItemComponentCondition::new);
        registerCondition("EXISTS", (TrivialConfigConstructor<ConfigurableCondition>) ExistsCondition::new);
    }

    private static boolean registerCondition(String regex, ConfigConstructor<ConfigurableCondition> constructor) {
        return CONDITIONS.add(new RegisteredCondition(regex, constructor));
    }

    public static <C extends ConfigurableCondition, T> void registerConditionType(Class<C> conditionClass, Class<T> testObjClass, BiFunction<C, T, Boolean> tester) {
        registerConditionType(
                conditionClass,
                testObjClass,
                (condition, toTest) -> testObjClass.isAssignableFrom(toTest.getClass()),
                tester
        );
    }

    public static <C extends ConfigurableCondition, T> void registerConditionType(Class<C> conditionClass, Class<T> testObjClass, BiPredicate<ConfigurableCondition, Object> predicate, BiFunction<C, T, Boolean> tester) {
        CONDITION_TYPES.add(new RegisteredConditionType<>(conditionClass,
                testObjClass,
                predicate,
                tester)
        );
    }

    public static boolean canTest(ConfigurableCondition condition, @Nullable Object object) {
        return CONDITION_TYPES.stream().anyMatch(reg -> {
            if (reg.conditionClass.isAssignableFrom(condition.getClass())) {
                return reg.testObjClass.isAssignableFrom(object == null ? Object.class : object.getClass());
            }
            return false;
        });
    }

    public static boolean test(ConfigurableCondition condition, Object object) {
        return test(condition, object, false);
    }
    public static boolean test(ConfigurableCondition condition, Object object, boolean defaultWhenNoCorrectType) {
        List<RegisteredConditionType<?, ?>> types = new ArrayList<>();
        for (RegisteredConditionType<?, ?> type : CONDITION_TYPES) {
            if (type.conditionClass.isAssignableFrom(condition.getClass())) {
                types.add(type);
            }
        }

        if (types.isEmpty()) {
            return defaultWhenNoCorrectType;
        }

        return types.stream().anyMatch(reg -> reg.test(condition, object));
    }

    @Nullable
    public static ConfigurableCondition buildCondition(ConfigurationSection parent, String key, @Nullable WbsSettings settings, @Nullable String directory) {
        return CONDITIONS.stream()
                .filter(reg -> reg.matches(key))
                .findFirst()
                .map(registration -> registration.constructor.construct(parent, key, settings, directory))
                .orElse(null);
    }

    /**
     * Represents a testable type (T) that can be tested by some class extending a configurable condition.
     * Individual checks (such as checking if an entity is on fire) are generated by finding the {@link RegisteredCondition},
     * related to a class extending conditionClass
     * @param conditionClass The class that can be extended to test testObjClass
     * @param testObjClass The object that will be tested
     * @param testObjChecker A function that checks if a condition instance can test an instance of type testObjClass
     * @param tester The function to actually run against a provided condition.
     * @param <C> The condition class
     * @param <T> The tested object class
     */
    public record RegisteredConditionType<C extends ConfigurableCondition, T>(
            Class<C> conditionClass,
            Class<T> testObjClass,
            BiPredicate<ConfigurableCondition, Object> testObjChecker,
            BiFunction<C, T, Boolean> tester
    ) {
        public boolean test(ConfigurableCondition condition, Object object) {
            if (canTest(condition, object)) {
                return tester.apply(conditionClass.cast(condition), testObjClass.cast(object));
            }
            return false;
        }
    }

    public record RegisteredCondition(
            String regex,
            ConfigConstructor<ConfigurableCondition> constructor
    ) {
        public boolean matches(String key) {
            return key.matches(regex);
        }
    }
}
