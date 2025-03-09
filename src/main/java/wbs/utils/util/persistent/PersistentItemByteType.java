package wbs.utils.util.persistent;

import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PersistentItemByteType implements PersistentDataType<byte[], ItemStack> {
    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<ItemStack> getComplexType() {
        return ItemStack.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull ItemStack itemStack, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        //noinspection ConstantValue
        if (itemStack == null || itemStack.isEmpty()) {
            return new byte[0];
        }

        return itemStack.serializeAsBytes();
    }

    @Override
    public @NotNull ItemStack fromPrimitive(byte @NotNull [] bytes, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        if (bytes.length == 0) {
            return ItemStack.empty();
        }
        return ItemStack.deserializeBytes(bytes);
    }
}
