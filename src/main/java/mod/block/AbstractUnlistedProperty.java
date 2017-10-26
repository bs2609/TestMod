package mod.block;

import net.minecraftforge.common.property.IUnlistedProperty;

public abstract class AbstractUnlistedProperty<V> implements IUnlistedProperty<V> {
	
	protected final String name;
	
	protected AbstractUnlistedProperty(String name) {
		this.name = name;
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
	public String valueToString(V value) {
		return value.toString();
	}
}
