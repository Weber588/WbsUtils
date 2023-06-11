package wbs.utils.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("unused")
public final class ReflectionUtil {
    private ReflectionUtil() {}

    @Nullable
    public static <T> T callGetterIfValid(@NotNull Object obj, @NotNull Class<T> clazz) {
        return callGetterIfValid(obj, clazz, "get" + clazz.getSimpleName());
    }

    /**
     * Invoke a method that takes no parameters and returns
     * an object of type T
     * @param obj The object to invoke the method on
     * @param clazz The class of the return type
     * @param getterName The name of the method to invoke
     * @param <T> The return type
     * @return The result of invoking the method, or null if:
     * <ul>
     *     <li>The result of the invocation was itself null, or</li>
     *     <li>The method specified by the given name did not exist on the object, or</li>
     *     <li>Another, unspecified exception is thrown</li>
     * </ul>
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T callGetterIfValid(@NotNull Object obj, @NotNull Class<T> clazz, @NotNull String getterName) {
        Class<?> objectClass = obj.getClass();

        try {
            Method method = objectClass.getMethod(getterName);

            if (method.getReturnType() == clazz) {
                return (T) method.invoke(obj);
            } else {
                return null;
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            return null;
        }


    }

}
