package wbs.utils.util.configuration.conditions.item;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.configuration.WbsValueReader;
import wbs.utils.util.configuration.conditions.ItemCondition;
import wbs.utils.util.configuration.conditions.comparisons.BinaryComparison;
import wbs.utils.util.configuration.conditions.comparisons.ComparisonManager;
import wbs.utils.util.configuration.conditions.comparisons.ComparisonOperator;
import wbs.utils.util.configuration.conditions.item.component.ItemComponentReader;
import wbs.utils.util.configuration.conditions.item.component.ItemComponentReaderManager;
import wbs.utils.util.plugin.WbsSettings;

import java.util.Objects;

///```yaml
/// component:
///     operation: 'compare'
///     comparison:
///         greater_than: 5
///     component-type: "minecraft:max_damage"
///
/// component:
///     operation: 'compare'
///     comparison:
///         greater_than: 5
///     component-type: "minecraft:max_damage"
///
/// component:
///     operation: 'is_present'
///     component-type: "minecraft:unbreakable"
///     component-value:
///         attribute:
///             attribute: "minecraft:armor"
///             method: derive_from_player # Makes the value return the resulting value a player will have after all attributes are applied
///             operation: add_value
///             slot: head
/// ```
@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class ItemComponentCondition implements ItemCondition {
    private final Operation operation;
    @Nullable
    protected final ComparisonOperator comparison;

    private final DataComponentType componentType;
    @Nullable
    private ItemComponentReader<?> componentReader;

    public ItemComponentCondition(ConfigurationSection section, @Nullable WbsSettings settings, @Nullable String directory) {
        WbsValueReader configReader = new WbsValueReader(section, "operation", directory)
                .settings(settings)
                .isRequired(true);

        operation = configReader.readEnum(Operation.class, Operation.COMPARE);

        if (operation == Operation.COMPARE) {
            comparison = configReader.getChildReader("comparison") // Default to first/only child key of the found section
                    .isRequired(true)
                    .setRequiredValueString("\"%s\" is required when operation is a comparison.")
                    .constructFrom(ComparisonManager.INSTANCE);
        } else {
            comparison = null;
        }

        String componentTypeKey = "component-type";
        componentType = configReader.updateKey(componentTypeKey)
                .readRegistryEntry(RegistryKey.DATA_COMPONENT_TYPE);

        if (comparison instanceof BinaryComparison && componentType instanceof DataComponentType.NonValued) {
            throw new InvalidConfigurationException(componentTypeKey + " must be a typed value when operation is \"compare\".", directory);
        }

        String requiredForCompareError = "\"%s\" is required when operation is \"compare\".";

        WbsValueReader componentValueReader = configReader.getChildReader("component-value") // Default to first/only child key of the found section
                .isRequired(true)
                .setRequiredValueString(requiredForCompareError);

        componentReader = componentValueReader
                .constructFrom(ItemComponentReaderManager.INSTANCE);

        if (comparison != null && this.componentReader == null) {
            if (comparison.numericOperator()) {
                componentReader = componentValueReader
                        .constructFrom(ItemComponentReaderManager.INSTANCE, componentType.key().value());
            }

            throw new InvalidConfigurationException(requiredForCompareError.formatted("component-value"), directory);
        }
    }

    @Override
    public boolean testItem(ItemStack item) {
        return switch (operation) {
            case IS_PRESENT -> item.hasData(componentType);
            case NOT_PRESENT -> !item.hasData(componentType);
            case COMPARE -> {
                if (comparison == null) {
                    throw new IllegalStateException("Comparison was null in comparison operation. Please report this bug.");
                }
                Object value = Objects.requireNonNull(
                        componentReader,
                        "Component reader was null in a binary comparison. Please report this bug."
                ).read(item);

                yield comparison.compare(value);
            }
        };
    }

    public enum Operation {
        IS_PRESENT,
        NOT_PRESENT,
        COMPARE,
        ;
    }
}
