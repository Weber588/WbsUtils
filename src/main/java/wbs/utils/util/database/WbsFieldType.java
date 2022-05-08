package wbs.utils.util.database;

import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public enum WbsFieldType {
    STRING, INT, DOUBLE, BOOLEAN, LONG;

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
            case LONG:
                return Long.class;
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

    public void prepare(PreparedStatement statement, int index, Object value, WbsField field) throws SQLException {
        switch (this) {
            case STRING:
                statement.setString(index, value == null ? (String) field.getDefaultValue() : (String) value);
                break;
            case INT:
                statement.setInt(index, value == null ? (Integer) field.getDefaultValue() : (Integer) value);
                break;
            case DOUBLE:
                statement.setDouble(index, value == null ? (Double) field.getDefaultValue() : (Double) value);
                break;
            case BOOLEAN: // Also prepare booleans as integers
                if (value == null) {
                    value = field.getDefaultValue();
                }
                statement.setInt(index, (Boolean) value ? 1 : 0);
                break;
            case LONG:
                statement.setLong(index, value == null ? (Long) field.getDefaultValue() : (Long) value);
                break;
            default:
                throw new UnsupportedOperationException("A field type failed to prepare a statement!");
        }
    }

    @NotNull
    public Object getDefaultValue() {
        switch (this) {
            case STRING:
                return "";
            case INT:
            case DOUBLE:
            case LONG:
            case BOOLEAN:
                return 0;
            default:
                throw new UnsupportedOperationException("A field type failed to provide a default value!");
        }
    }
}
