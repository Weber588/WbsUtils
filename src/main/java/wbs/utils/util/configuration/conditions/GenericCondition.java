package wbs.utils.util.configuration.conditions;

/**
 * A configurable condition that's capable of testing ANY object. Always checked last.
 */
public interface GenericCondition extends ConfigurableCondition {
    boolean testGeneric(Object object);
}
