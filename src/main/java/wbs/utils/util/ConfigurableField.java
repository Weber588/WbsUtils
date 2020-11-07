package wbs.utils.util;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import wbs.utils.exceptions.MissingConfigurationException;
import wbs.utils.exceptions.UndefinedClassConfigurationException;

public class ConfigurableField<T> {

	private final Class<T> type;

	public ConfigurableField(Class<T> type) {
		this.type = type;
	}
	
	private T value;
	
	public ConfigurableField<T> setValue(T value) {
		this.value = value;
		return this;
	}
	
	public T getValue() {
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public <E extends Enum<E>> ConfigurableField<T> configure(ConfigurationSection section, String key) throws MissingConfigurationException {
		String stringValue = section.getString(key);
		if (stringValue == null) {
			throw new MissingConfigurationException();
		}
		
		if (Enum.class.isAssignableFrom(type)) {
			Class<E> enumType = (Class<E>) type;
			value = (T) WbsEnums.getEnumFromString(enumType, section.getString(key));
			return this;
		}

		if (Double.class.isAssignableFrom(type)) {
			value = (T) (Double) section.getDouble(key);
			return this;
		}
		
		if (Integer.class.isAssignableFrom(type)) {
			value = (T) (Integer) section.getInt(key);
			return this;
		}
		
		if (Boolean.class.isAssignableFrom(type)) {
			value = (T) (Boolean) section.getBoolean(key);
			return this;
		}
		
		if (String.class.isAssignableFrom(type)) {
			value = (T) stringValue;
			return this;
		}
		
		throw new UndefinedClassConfigurationException("This class may not be used with ConfigurationSection configuring");
	}
	
	public ConfigurableField<T> configure(String[] pairList, String key) {
		for (int index = 0; index < pairList.length + 1; index += 2) {
			if (key.equalsIgnoreCase(pairList[index])) {
				return configure(pairList[index + 1]);
			}
		}
		
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <E extends Enum<E>> ConfigurableField<T> configure(String asString) {
		if (Enum.class.isAssignableFrom(type)) {
			Class<E> enumType = (Class<E>) type;
			value = (T) WbsEnums.getEnumFromString(enumType, asString);
			return this;
		}

		if (Double.class.isAssignableFrom(type)) {
			value = (T) (Double) Double.parseDouble(asString);
			return this;
		}
		
		if (Integer.class.isAssignableFrom(type)) {
			value = (T) (Integer) Integer.parseInt(asString);
			return this;
		}
		
		if (Boolean.class.isAssignableFrom(type)) {
			value = (T) (Boolean) Boolean.parseBoolean(asString);
			return this;
		}
		
		if (String.class.isAssignableFrom(type)) {
			value = (T) asString;
			return this;
		}
		
		throw new UndefinedClassConfigurationException("This class may not be used with ConfigurationSection configuring");
	}

	public <K> ConfigurableField<T> configure(Map<K, T> map, K key) {
		value = map.get(key);
		return this;
	}
}
