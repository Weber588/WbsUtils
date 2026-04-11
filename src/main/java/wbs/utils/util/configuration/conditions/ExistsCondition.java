package wbs.utils.util.configuration.conditions;

public class ExistsCondition implements GenericCondition {
    @Override
    public boolean testGeneric(Object object) {
        return object != null;
    }
}
