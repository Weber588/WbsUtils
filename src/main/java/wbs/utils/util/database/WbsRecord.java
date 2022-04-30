package wbs.utils.util.database;

import org.jetbrains.annotations.NotNull;
import wbs.utils.exceptions.WbsDatabaseException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class WbsRecord {

    private final WbsDatabase database;
    private final Map<WbsField, Object> fields = new HashMap<>();
    private final Map<String, Object> anonymousFields = new HashMap<>();

    public WbsRecord(WbsDatabase database) {
        this.database = database;
    }

    public WbsRecord(WbsDatabase database, Map<WbsField, Object> fieldsMap) {
        this.database = database;
        this.fields.putAll(fieldsMap);
    }

    public WbsRecord(WbsDatabase database, ResultSet set) throws SQLException, WbsDatabaseException {
        this.database = database;

        ResultSetMetaData metaData = set.getMetaData();

        for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
            String tableName = metaData.getTableName(i);
            String fieldName = metaData.getColumnName(i).split(":")[0];

            WbsField field = database.getField(tableName, fieldName);

            if (field == null) {
                anonymousFields.put(fieldName, set.getObject(i));
            } else {
                setField(field, set.getObject(i));
            }
        }
    }

    public boolean setField(WbsField field, Object value) {
        if (value == null) return false;

        fields.put(field, parseValue(field, value));
        return true;
    }

    private Object parseValue(WbsField field, Object object) {
        if (object == null) return null;

        Object value = object;

        if (field.getType().getFieldType() != value.getClass()) {
            if (field.getType() == WbsFieldType.STRING) {
                value = String.valueOf(value);
            } else if (field.getType() == WbsFieldType.DOUBLE && value instanceof Integer) {
                Integer intVal = (Integer) value;
                value = new Double(intVal);
            } else if (field.getType() == WbsFieldType.BOOLEAN && value instanceof Integer) {
                value = ((Integer) value) == 1;
            } else {
                throw new IllegalArgumentException("Invalid type for field " + field.getFieldName() + "; " +
                        "Type is specified as " + field.getType() + ", but " + value.getClass().getSimpleName() + " given.");
            }
        }

        return value;
    }

    public <T> T getValue(WbsField field, Class<T> clazz) {
        return clazz.cast(parseValue(field, fields.get(field)));
    }

    public <T> T getOrDefault(WbsField field, Class<T> clazz) {
        return clazz.cast(getOrDefault(field));
    }

    public Object getValue(WbsField field) {
        return fields.get(field);
    }

    public Object getOrDefault(WbsField field) {
        return parseValue(field, fields.getOrDefault(field, field.getDefaultValue()));
    }

    public Object getAnonymousField(String anonName) {
        return anonymousFields.get(anonName);
    }

    public boolean upsert(WbsTable table) {
        return table.upsert(Collections.singletonList(this));
    }

    /**
     * Inserts this record into the given table, ignoring fields not present
     * on the table.
     * @param table The table to insert into.
     * @return True if the record was inserted without issue, false if an error was encountered.
     */
    public boolean insert(WbsTable table) {
        return table.insert(Collections.singletonList(this));
    }

    /**
     * Conditionally inserts this record into the given table, ignoring fields not present
     * on the table. The record will only be inserted if its fields do not match the defaults
     * specified on the table's fields.
     * @param table The table to insert into.
     * @return True if the record was inserted without issue, false if an error was encountered or
     * if the record was default.
     */
    public boolean insertIfNotDefault(WbsTable table) {
        if (isNonPrimaryDefault(table)) {
            return false;
        }
        return insert(table);
    }

    public boolean update(WbsTable table) {
        return table.update(Collections.singletonList(this));
    }

    public boolean hasValue(WbsField field) {
        return fields.containsKey(field);
    }

    /**
     * Checks if a given field on this object matches the default for
     * the field. If no field default is set, and this record has a
     * non-null value, this will return false.
     * @param field The field to check
     * @return True if the set field matches the field default.
     */
    public boolean isDefault(@NotNull WbsField field) {
        return Objects.equals(field.getDefaultValue(), getValue(field));
    }

    /**
     * Returns true if this record is a default record for the given table, excluding the
     * table's primary key.
     * @return True if all fields except the primary key are default.
     */
    public boolean isNonPrimaryDefault(WbsTable table) {
        for (WbsField field : table.getFields()) {
            if (!field.isPrimaryKey()) {
                if (!isDefault(field)) return false;
            }
        }
        return true;
    }

    public int size() {
        return fields.size();
    }
}
