package wbs.utils.util.configuration.conditions.comparisons;

import wbs.utils.util.configuration.RegexRoutedConstructorManager;

public class ComparisonManager extends RegexRoutedConstructorManager<ComparisonOperator> {
    public static final ComparisonManager INSTANCE = new ComparisonManager(ComparisonOperator.class);

    static {
        INSTANCE.register("(equals|=)", EqualityComparison::new);
        INSTANCE.register("(greater([\\s_]?than)?|>)", GreaterThanComparison::new);
        INSTANCE.register("(greater([\\s_]?than)?(or[_\\s]?)?equal([_\\s]?to)?|>=)", GreaterOrEqualComparison::new);
        INSTANCE.register("(less([\\s_]?than)?|<)", LessThanComparison::new);
        INSTANCE.register("(less([\\s_]?than)?([\\s_]?or[_\\s]?)?equal([_\\s]?to)?|<=)", LessOrEqualComparison::new);
    }

    private ComparisonManager(Class<ComparisonOperator> classToConstruct) {
        super(classToConstruct);
    }
}
