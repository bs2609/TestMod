package mod.block.property;

import net.minecraft.block.properties.IProperty;
import net.minecraft.util.IStringSerializable;

import java.util.EnumSet;

public class Properties {
	
	public static <E extends Enum<E> & IStringSerializable> IProperty<E> create(String name, Class<E> type) {
		return new SimpleProperty<>(name, type, EnumSet.allOf(type));
	}
}
