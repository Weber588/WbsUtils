package wbs.utils.util.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class WbsField {

    private final String fieldName;
    private final WbsFieldType type;
    private Object defaultValue;

    private boolean primaryKey = false;
    private boolean notNull = false;

    public WbsField(String fieldName, WbsFieldType type) {
        this.fieldName = fieldName;
        this.type = type;
    }

    public WbsField(String fieldName, WbsFieldType type, Object defaultValue) {
        this.fieldName = fieldName;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public void prepare(PreparedStatement statement, int index, Object value) throws SQLException {
        switch (type) {
            case STRING:
                statement.setString(index, (String) value);
                break;
            case INT:
                statement.setInt(index, (Integer) value);
                break;
            case DOUBLE:
                statement.setDouble(index, (Double) value);
                break;
        }
    }


    public String getFieldName() {
        return fieldName;
    }

    public WbsFieldType getType() {
        return type;
    }

    public String getCreationPhrase() {
        String creationPhrase = "`" + fieldName + "` " + type;

        if (notNull) creationPhrase += " NOT NULL";

        return creationPhrase;
    }

    public WbsField setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }

    public WbsField setNotNull(boolean notNull) {
        this.notNull = notNull;
        return this;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public boolean requiresNotNull() {
        return notNull;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
}
