package wbs.utils.util.configuration.options;

import wbs.utils.util.configuration.WbsOption;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(IntOptions.class)
@WbsOption(WbsIntOption.class)
public @interface IntOption {
    String value();
    int defaultValue() default 0;
    String[] aliases() default {};
}
