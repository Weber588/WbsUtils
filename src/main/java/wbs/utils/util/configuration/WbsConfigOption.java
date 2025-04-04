package wbs.utils.util.configuration;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

@Deprecated
public abstract class WbsConfigOption<T, A extends Annotation> {

    protected T value;
    protected T defaultValue;

    public WbsConfigOption(@NotNull WbsConfig config, @NotNull A annotation) {

    }

    public void setValue(T value) {
        this.value = value;
    }

    @NotNull
    public T getValue() {
        return value != null ? value : defaultValue;
    }

    @NotNull
    public T getDefaultValue() {
        return defaultValue;
    }

    public boolean isValueSet() {
        return value != null;
    }
}
