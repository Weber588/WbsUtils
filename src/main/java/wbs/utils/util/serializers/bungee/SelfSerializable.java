// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
package wbs.utils.util.serializers.bungee;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

interface SelfSerializable {
    void write(JsonWriter out) throws IOException;

    class AdapterFactory implements TypeAdapterFactory {
        public AdapterFactory() {
        }

        public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
            return !SelfSerializable.class.isAssignableFrom(type.getRawType()) ? null : new SelfSerializable.AdapterFactory.SelfSerializableTypeAdapter<>(type);
        }

        static {
            SelfSerializable.AdapterFactory.SelfSerializableTypeAdapter.class.getName();
        }

        static class SelfSerializableTypeAdapter<T> extends TypeAdapter<T> {
            private final TypeToken<T> type;

            SelfSerializableTypeAdapter(final TypeToken<T> type) {
                this.type = type;
            }

            public void write(final JsonWriter out, final T value) throws IOException {
                ((SelfSerializable)value).write(out);
            }

            public T read(final JsonReader in) {
                throw new UnsupportedOperationException("Cannot load values of type " + this.type.getType().getTypeName());
            }
        }
    }
}
