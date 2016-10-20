package mod.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

import java.util.EnumSet;

public enum MaterialType implements IStringSerializable {
	
	SOLID("solid"),
	TRANSLUCENT("translucent"),
	PASSABLE("passable"),
	CLEAR("clear");
	
	public static final PropertyEnum<MaterialType> PROPERTY = PropertyEnum.create("material", MaterialType.class, EnumSet.allOf(MaterialType.class));
	
	private final String name;
	
	MaterialType(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public static MaterialType getType(Material material) {
		if (material.isOpaque()) return SOLID;
		if (material.blocksMovement()) return TRANSLUCENT;
		if (material.blocksLight() || material.isSolid()) return PASSABLE;
		return CLEAR;
	}

	public Material getExample() {
		switch (this) {
			case SOLID: return Material.ROCK;
			case TRANSLUCENT: return Material.ICE;
			case PASSABLE: return Material.WEB;
			case CLEAR: return Material.AIR;
			default: return null;
		}
	}
}
