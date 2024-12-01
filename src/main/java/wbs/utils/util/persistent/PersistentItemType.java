package wbs.utils.util.persistent;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

public class PersistentItemType implements PersistentDataType<String, ItemStack> {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Byte.class, TypeAdapters.BYTE)
            .create();

    @NotNull
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<ItemStack> getComplexType() {
        return ItemStack.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull ItemStack itemStack, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("item", itemStack);
        return yaml.saveToString();
    }

    @NotNull
    @Override
    public ItemStack fromPrimitive(@NotNull String asString, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.loadFromString(asString);
        } catch (InvalidConfigurationException e) {
            throw new IllegalArgumentException("Invalid yaml string: " + asString);
        }

        return Objects.requireNonNull(yaml.getItemStack("item"));
    }
}
