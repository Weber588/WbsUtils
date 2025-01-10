//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wbs.utils.util.serializers.bungee;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Excluder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

final class GsonInjections {
    private GsonInjections() {
    }

    public static Field field(final @NotNull Class<?> klass, final @NotNull String name) throws NoSuchFieldException {
        Field field = klass.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    @SuppressWarnings("unchecked")
    public static boolean injectGson(final @NotNull Gson existing, final @NotNull Consumer<GsonBuilder> accepter) {
        try {
            Field factoriesField = field(Gson.class, "factories");
            Field builderFactoriesField = field(GsonBuilder.class, "factories");
            Field builderHierarchyFactoriesField = field(GsonBuilder.class, "hierarchyFactories");
            GsonBuilder builder = new GsonBuilder();
            accepter.accept(builder);
            List<TypeAdapterFactory> existingFactories = (List<TypeAdapterFactory>) factoriesField.get(existing);
            List<TypeAdapterFactory> newFactories = new ArrayList<>((List<TypeAdapterFactory>) builderFactoriesField.get(builder));
            Collections.reverse(newFactories);
            newFactories.addAll((List<TypeAdapterFactory>)builderHierarchyFactoriesField.get(builder));
            List<TypeAdapterFactory> modifiedFactories = new ArrayList<>(existingFactories);
            int index = findExcluderIndex(modifiedFactories);
            Collections.reverse(newFactories);

            for (TypeAdapterFactory newFactory : newFactories) {
                modifiedFactories.add(index, newFactory);
            }

            factoriesField.set(existing, modifiedFactories);
            return true;
        } catch (IllegalAccessException | NoSuchFieldException var12) {
            return false;
        }
    }

    private static int findExcluderIndex(final @NotNull List<TypeAdapterFactory> factories) {
        int i = 0;

        for(int size = factories.size(); i < size; ++i) {
            TypeAdapterFactory factory = factories.get(i);
            if (factory instanceof Excluder) {
                return i + 1;
            }
        }

        return 0;
    }
}
