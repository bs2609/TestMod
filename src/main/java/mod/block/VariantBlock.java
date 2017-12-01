package mod.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public abstract class VariantBlock<E extends Enum<E> & IStringSerializable> extends BasicBlock {
	
	protected VariantBlock(String name, Material material) {
		super(name, material);
	}
	
	protected VariantBlock(String name, Material material, CreativeTabs tab) {
		super(name, material, tab);
	}
	
	@Override
	protected ItemBlock createItemBlock() {
		ItemMultiTexture.Mapper mapper = new ItemMultiTexture.Mapper() {
			@Override
			public String apply(ItemStack input) {
				return getTypeForMeta(input.getMetadata()).getName();
			}
		};
		ItemMultiTexture itemMultiTexture = new ItemMultiTexture(this, this, mapper);
		itemMultiTexture.setRegistryName(this.getRegistryName());
		return itemMultiTexture;
	}
	
	public abstract int getMetaForType(E type);

	public abstract E getTypeForMeta(int meta);
}
