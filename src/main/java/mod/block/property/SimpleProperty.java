package mod.block.property;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.util.IStringSerializable;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public class SimpleProperty<T extends Comparable<T> & IStringSerializable> extends PropertyHelper<T> {
	
	protected final ImmutableSet<T> allowedValues;
	protected final ImmutableMap<String, T> valuesByName;
	
	private final int hashCode;
	
	public SimpleProperty(String name, Class<T> valueClass, Set<T> allowedValues) {
		super(name, valueClass);
		this.allowedValues = ImmutableSet.copyOf(allowedValues);
		ImmutableMap.Builder<String, T> builder = ImmutableMap.builder();
		allowedValues.forEach(value -> builder.put(value.getName(), value));
		this.valuesByName = builder.build();
		this.hashCode = Objects.hash(super.hashCode(), allowedValues, valuesByName);
	}
	
	@Override
	public Collection<T> getAllowedValues() {
		return allowedValues;
	}
	
	@Override
	public Optional<T> parseValue(String name) {
		return Optional.fromNullable(valuesByName.get(name));
	}
	
	@Override
	public String getName(T value) {
		return value.getName();
	}
	
	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SimpleProperty)) return false;
		if (!super.equals(o)) return false;
		SimpleProperty<?> that = (SimpleProperty<?>) o;
		return Objects.equals(allowedValues, that.allowedValues) &&
				Objects.equals(valuesByName, that.valuesByName);
	}
	
	@Override
	public final int hashCode() {
		return hashCode;
	}
}
