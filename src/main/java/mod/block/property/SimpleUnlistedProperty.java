package mod.block.property;

import net.minecraftforge.common.property.IUnlistedProperty;

public class SimpleUnlistedProperty<V> implements IUnlistedProperty<V> {
	
	protected final String name;
	protected final Class<V> type;
	
	public SimpleUnlistedProperty(String name, Class<V> type) {
		this.name = name;
		this.type = type;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean isValid(V value) {
		return value != null;
	}
	
	@Override
	public Class<V> getType() {
		return type;
	}
	
	@Override
	public String valueToString(V value) {
		return value.toString();
	}
}
