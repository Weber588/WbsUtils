package wbs.utils.util.database;

public enum WbsFieldType {
    STRING, INT, DOUBLE, BOOLEAN;

    public Class<?> getFieldType() {
        switch (this) {
            case STRING:
                return String.class;
            case INT:
                return Integer.class;
            case DOUBLE:
                return Double.class;
            case BOOLEAN:
                return Boolean.class;
        }

        return null;
    }

    @Override
    public String toString() {
        if (this == STRING) {
            return "VARCHAR";
        }

        return super.toString();
    }
}
