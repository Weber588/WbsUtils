package wbs.utils.util.configuration.conditions.comparisons;

public interface ComparisonOperator {
    boolean compare(Object object);

    boolean numericOperator();
}
