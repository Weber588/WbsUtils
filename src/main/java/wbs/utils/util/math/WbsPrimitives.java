package wbs.utils.util.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

public class WbsPrimitives {
    public static Number add(Number a, Number b) {
        return ADD.apply(a, b);
    }

    public static Number subtract(Number a, Number b) {
        return SUBTRACT.apply(a, b);
    }

    public static Number multiply(Number a, Number b) {
        return MULTIPLY.apply(a, b);
    }

    public static Number divide(Number a, Number b) {
        return DIVIDE.apply(a, b);
    }

    public static Number remainder(Number a, Number b) {
        return REMAINDER.apply(a, b);
    }

    public static Number modulo(Number a, Number b) {
        return MODULO.apply(a, b);
    }

    public static Number pow(Number a, Number b) {
        return EXPONENTIATION.apply(a, b);
    }

    public static String toString(Number a) {
        return TO_STRING.apply(a);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number> T convertNumber(Number number, Class<T> clazz) {
        if (clazz == Double.class) {
            return (T) (Double) number.doubleValue();
        }
        if (clazz == Float.class) {
            return (T) (Float) number.floatValue();
        }
        if (clazz == Long.class) {
            return (T) (Long) number.longValue();
        }
        if (clazz == Integer.class) {
            return (T) (Integer) number.intValue();
        }
        if (clazz == Short.class) {
            return (T) (Short) number.shortValue();
        }
        if (clazz == Byte.class) {
            return (T) (Byte) number.byteValue();
        }
        if (clazz == BigDecimal.class) {
            return (T) new BigDecimal(String.valueOf(number));
        }
        if (clazz == BigInteger.class) {
            return (T) new BigInteger(number.toString());
        }

        throw new IllegalArgumentException("Unknown number type: %s".formatted(clazz.getCanonicalName()));
    }

    public interface BiPrimitiveOperator extends BiFunction<Number, Number, Number> {
        double onDouble(double a, double b);

        default float onFloat(float a, float b) {
            return (float) onDouble(a, b);
        }

        long onLong(long a, long b);

        default int onInt(int a, int b) {
            return (int) onLong(a, b);
        }

        /**
         * Operates on two Numbers of any type, returning the same type that primitive operations do.<br/>
         * The highest value in the below list takes precedence:
         * <li>If either value is Double, the result will be Double</li>
         * <li>If either value is Float, the result will be Float</li>
         * <li>If either value is Long, the result will be Long</li>
         * <li>Otherwise, Integer will be returned</li>
         */
        @Override
        default Number apply(Number a, Number b) {
            if (a instanceof Double || b instanceof Double) {
                return onDouble(a.doubleValue(), b.doubleValue());
            }
            if (a instanceof Float || b instanceof Float) {
                return onFloat(a.floatValue(), b.floatValue());
            }
            if (a instanceof Long || b instanceof Long) {
                return onLong(a.longValue(), b.longValue());
            }
            return onInt(a.intValue(), b.intValue());
        }
    }

    public interface PrimitiveOperator extends Function<Number, Number> {
        double onDouble(double a);

        default float onFloat(float a) {
            return (float) onDouble(a);
        }

        long onLong(long a);

        default int onInt(int a) {
            return (int) onLong(a);
        }

        @Override
        default Number apply(Number a) {
            return operate(a);
        }

        @SuppressWarnings("unchecked")
        default  <T extends Number> T operate(T a) {
            if (a instanceof Double) {
                return (T) (Double) onDouble(a.doubleValue());
            }
            if (a instanceof Float) {
                return (T) (Float) onFloat(a.floatValue());
            }
            if (a instanceof Long) {
                return (T) (Long) onLong(a.longValue());
            }

            return (T) (Integer) onInt(a.intValue());
        }
    }

    public interface GenericPrimitiveOperator<T> extends Function<Number, T> {
        T onDouble(double a);

        default T onFloat(float a) {
            return onDouble(a);
        }

        T onLong(long a);

        default T onInt(int a) {
            return onLong(a);
        }

        @Override
        default T apply(Number a) {
            if (a instanceof Double) {
                return onDouble(a.doubleValue());
            }
            if (a instanceof Float) {
                return onFloat(a.floatValue());
            }
            if (a instanceof Long) {
                return onLong(a.longValue());
            }
            return onInt(a.intValue());
        }
    }
    private static final BiPrimitiveOperator ADD = new BiPrimitiveOperator() {
        @Override
        public double onDouble(double a, double b) {
            return a + b;
        }

        @Override
        public long onLong(long a, long b) {
            return a + b;
        }
    };

    public static final BiPrimitiveOperator SUBTRACT = new BiPrimitiveOperator() {
        @Override
        public double onDouble(double a, double b) {
            return a - b;
        }

        @Override
        public long onLong(long a, long b) {
            return a - b;
        }
    };

    public static final BiPrimitiveOperator MULTIPLY = new BiPrimitiveOperator() {
        @Override
        public double onDouble(double a, double b) {
            return a * b;
        }

        @Override
        public long onLong(long a, long b) {
            return a * b;
        }
    };

    public static final BiPrimitiveOperator DIVIDE = new BiPrimitiveOperator() {
        @Override
        public double onDouble(double a, double b) {
            return a / b;
        }

        @Override
        public long onLong(long a, long b) {
            return a / b;
        }
    };

    public static final BiPrimitiveOperator REMAINDER = new BiPrimitiveOperator() {
        @Override
        public double onDouble(double a, double b) {
            return a % b;
        }

        @Override
        public long onLong(long a, long b) {
            return a % b;
        }
    };

    public static final BiPrimitiveOperator MODULO = new BiPrimitiveOperator() {
        @Override
        public double onDouble(double a, double b) {
            double value = a % b;

            if (value < 0) {
                value += b;
            }

            return value;
        }

        @Override
        public long onLong(long a, long b) {
            return Math.floorMod(a, b);
        }
    };

    public static final BiPrimitiveOperator EXPONENTIATION = new BiPrimitiveOperator() {
        @Override
        public double onDouble(double a, double b) {
            return Math.pow(a, b);
        }

        @Override
        public long onLong(long a, long b) {
            return Math.powExact(a, (int) b);
        }
    };
    public static final GenericPrimitiveOperator<String> TO_STRING = new GenericPrimitiveOperator<>() {
        @Override
        public String onDouble(double a) {
            return String.valueOf(a);
        }

        @Override
        public String onLong(long a) {
            return String.valueOf(a);
        }
    };
}
