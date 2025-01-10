package wbs.utils.util.serializers.bungee;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

@SuppressWarnings({"deprecation", "unused"})
public final class BungeeComponentSerializer implements ComponentSerializer<Component, Component, BaseComponent[]> {
    private static boolean SUPPORTED = true;
    private static final BungeeComponentSerializer MODERN;
    private static final BungeeComponentSerializer PRE_1_16;
    private final GsonComponentSerializer serializer;
    private final LegacyComponentSerializer legacySerializer;

    public static boolean isNative() {
        return SUPPORTED;
    }

    public static BungeeComponentSerializer get() {
        return MODERN;
    }

    public static BungeeComponentSerializer legacy() {
        return PRE_1_16;
    }

    public static BungeeComponentSerializer of(final GsonComponentSerializer serializer, final LegacyComponentSerializer legacySerializer) {
        return serializer != null && legacySerializer != null ? new BungeeComponentSerializer(serializer, legacySerializer) : null;
    }

    public static boolean inject(final Gson existing) {
        boolean result = GsonInjections.injectGson(Objects.requireNonNull(existing, "existing"), (builder) -> {
            GsonComponentSerializer.gson().populator().apply(builder);
            builder.registerTypeAdapterFactory(new SelfSerializable.AdapterFactory());
        });
        SUPPORTED &= result;
        return result;
    }

    private BungeeComponentSerializer(final GsonComponentSerializer serializer, final LegacyComponentSerializer legacySerializer) {
        this.serializer = serializer;
        this.legacySerializer = legacySerializer;
    }

    private static void bind() {
        try {
            Field gsonField = GsonInjections.field(ComponentSerializer.class, "gson");
            inject((Gson)gsonField.get(null));
        } catch (Throwable var1) {
            SUPPORTED = false;
        }

    }

    public @NotNull Component deserialize(final @NotNull BaseComponent @NotNull [] input) {
        Objects.requireNonNull(input, "input");
        return input.length == 1 && input[0] instanceof AdapterComponent ? ((AdapterComponent)input[0]).component : this.serializer.deserialize(net.md_5.bungee.chat.ComponentSerializer.toString(input));
    }

    public @NotNull BaseComponent @NotNull [] serialize(final @NotNull Component component) {
        Objects.requireNonNull(component, "component");
        return SUPPORTED ? new BaseComponent[]{new AdapterComponent(component)} : net.md_5.bungee.chat.ComponentSerializer.parse(this.serializer.serialize(component));
    }

    static {
        bind();
        MODERN = new BungeeComponentSerializer(GsonComponentSerializer.gson(), LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build());
        PRE_1_16 = new BungeeComponentSerializer(GsonComponentSerializer.builder().downsampleColors().emitLegacyHoverEvent().build(), LegacyComponentSerializer.legacySection());
    }

    class AdapterComponent extends BaseComponent implements SelfSerializable {
        private final Component component;
        private volatile String legacy;

        AdapterComponent(final Component component) {
            this.component = component;
        }

        public String toLegacyText() {
            if (this.legacy == null) {
                this.legacy = BungeeComponentSerializer.this.legacySerializer.serialize(this.component);
            }

            return this.legacy;
        }

        public @NotNull BaseComponent duplicate() {
            return this;
        }

        public void write(final JsonWriter out) throws IOException {
            BungeeComponentSerializer.this.serializer.serializer().getAdapter(Component.class).write(out, this.component);
        }
    }
}
