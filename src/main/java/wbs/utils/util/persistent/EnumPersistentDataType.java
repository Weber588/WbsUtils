package wbs.utils.util.persistent;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class EnumPersistentDataType<T extends Enum<T>> implements PersistentDataType<String, T> {
    private final Class<T> tClass;

    public EnumPersistentDataType(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<T> getComplexType() {
        return tClass;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull T enumValue, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return enumValue.name();
    }

    @Override
    public @NotNull T fromPrimitive(@NotNull String enumName, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return Arrays.stream(tClass.getEnumConstants()).filter(constant -> constant.name().equals(enumName)).findFirst().orElseThrow();
    }
}
