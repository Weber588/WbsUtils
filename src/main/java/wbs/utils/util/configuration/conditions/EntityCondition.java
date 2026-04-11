package wbs.utils.util.configuration.conditions;

import org.bukkit.entity.Entity;

public interface EntityCondition<T extends Entity> extends ConfigurableCondition {
    boolean testEntity(T t);
    Class<T> getEntityClass();
}
