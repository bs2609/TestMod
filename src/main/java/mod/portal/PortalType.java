package mod.portal;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum PortalType implements IStringSerializable {

	IN("inward"),
	OUT("outward");

	public static final Set<PortalType> VALUES = Collections.unmodifiableSet(EnumSet.allOf(PortalType.class));

	public static final PropertyEnum<PortalType> PROPERTY = PropertyEnum.create("type", PortalType.class, VALUES);

	private final String name;

	PortalType(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
