package mod.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

public enum MaterialType implements IStringSerializable {
	
	SOLID("solid"),
	TRANSLUCENT("translucent"),
	PASSABLE("passable"),
	CLEAR("clear");
	
	public static final Set<MaterialType> VALUES = Collections.unmodifiableSet(EnumSet.allOf(MaterialType.class));
	public static final IProperty<MaterialType> PROPERTY = PropertyEnum.create("material", MaterialType.class, VALUES);
	private static final EnumMap<MaterialType, Material> MATERIALS = new EnumMap<MaterialType, Material>(MaterialType.class);
	
	static {
		for (MaterialType type : VALUES) {
			MATERIALS.put(type, new MaterialProxy(type));
		}
	}
	
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
		return MATERIALS.get(this);
	}
	
	private static class MaterialProxy extends Material {
		
		private final MaterialType type;
		
		private MaterialProxy(MaterialType type) {
			super(MapColor.AIR);
			this.type = type;
		}
		
		@Override
		public boolean isSolid() {
			return type != CLEAR;
		}
		
		@Override
		public boolean blocksLight() {
			return type != CLEAR;
		}
		
		@Override
		public boolean blocksMovement() {
			return type == SOLID || type == TRANSLUCENT;
		}
		
		@Override
		public boolean isOpaque() {
			return type == SOLID;
		}
	}
}
