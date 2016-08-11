package mod.portal;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

import java.util.EnumSet;

public enum PortalType implements IStringSerializable {

	IN("inward"),
	OUT("outward");

	public static final PropertyEnum<PortalType> PROPERTY = PropertyEnum.create("type", PortalType.class, EnumSet.allOf(PortalType.class));

	private final String name;

	PortalType(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
