package wbs.utils.util.configuration.conditions.item.component;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

import java.util.Optional;
import java.util.stream.Stream;

/// ```yaml
/// attribute:
///     attribute: "minecraft:armor"
///     method: derive_from_player # Makes the value return the resulting value a player will have after all attributes are applied
///     operation: add_value
///     slot: head
/// ```
@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class AttributeValueReader implements ItemComponentReader<Double> {
    private final Attribute attribute;
    private final Method checkMethod;
    @Nullable
    private final AttributeModifier.Operation requireOperation;
    @Nullable
    private final EquipmentSlot requireSlot;

    public AttributeValueReader(ConfigurationSection section, @Nullable WbsSettings settings, @Nullable String directory) {
        Attribute checkAttribute = WbsConfigReader.getRegistryEntry(section, "attribute", RegistryKey.ATTRIBUTE);
        if (checkAttribute == null) {
            throw new InvalidConfigurationException("", directory + "/attribute");
        }
        this.attribute = checkAttribute;

        Method checkMethod = WbsConfigReader.getEnum(section, "method", null, directory, Method.class);
        if (checkMethod == null) {
            checkMethod = Method.DERIVE_FROM_PLAYER;
        }
        this.checkMethod = checkMethod;
        String operationString = section.getString("operation");
        if (operationString == null) {
            requireOperation = null;
        } else {
            requireOperation = switch (operationString.toLowerCase()) {
                case "add_number", "add_value" -> AttributeModifier.Operation.ADD_NUMBER;
                case "add_scalar", "add_multiplied_base" -> AttributeModifier.Operation.ADD_SCALAR;
                case "multiply_scalar_1", "add_multiplied_total" -> AttributeModifier.Operation.MULTIPLY_SCALAR_1;
                default -> throw new InvalidConfigurationException("Invalid operation: " + operationString, directory);
            };
        }

        requireSlot = WbsConfigReader.getEnum(section, "slot", settings, directory, EquipmentSlot.class);
    }

    @Override
    @Nullable
    public Double read(ItemStack item) {
        return Optional.ofNullable(item.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS))
                .flatMap(values -> {
                    Stream<ItemAttributeModifiers.Entry> entryStream = values.modifiers().stream()
                            .filter(entry -> entry.attribute().equals(attribute));

                    if (requireOperation != null) {
                        entryStream = entryStream.filter(entry ->
                                entry.modifier().getOperation().equals(requireOperation)
                        );
                    }
                    if (requireSlot != null) {
                        entryStream = entryStream.filter(entry ->
                                entry.modifier().getSlotGroup().test(requireSlot)
                        );
                    }

                    return switch (checkMethod) {
                        case FIRST -> entryStream
                                .findFirst()
                                .map(entry -> entry.modifier().getAmount());
                        case SUM -> Optional.of(entryStream
                                .mapToDouble(entry -> entry.modifier().getAmount())
                                .sum());
                        case MIN -> entryStream
                                .mapToDouble(entry -> entry.modifier().getAmount())
                                .min()
                                .stream().boxed().findFirst();
                        case MAX -> entryStream
                                .mapToDouble(entry -> entry.modifier().getAmount())
                                .max()
                                .stream().boxed().findFirst();
                        case DERIVE_FROM_PLAYER -> {
                            AttributeInstance defaultInstance = EntityType.PLAYER.getDefaultAttributes()
                                    .getAttribute(attribute);

                            if (defaultInstance == null) {
                                yield Optional.empty();
                            }

                            entryStream.forEachOrdered(entry -> {
                                defaultInstance.addModifier(entry.modifier());
                            });

                            yield Optional.of(defaultInstance.getValue());
                        }
                    };
                })
                .orElse(null);
    }

    public enum Method {
        DERIVE_FROM_PLAYER,
        FIRST,
        SUM,
        MIN,
        MAX,
    }
}
