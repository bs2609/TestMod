package mod.block;

import com.google.common.base.Function;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public abstract class VariantBlock<E extends Enum<E> & IStringSerializable> extends BasicBlock {
	
	protected VariantBlock(String name, Material material) {
		super(name, material);
	}
	
	@Override
	protected ItemBlock createItemBlock() {
		Function<ItemStack, String> function = new Function<ItemStack, String>() {
			@Override
			public String apply(ItemStack input) {
				return getTypeForMeta(input.getMetadata()).getName();
			}
		};
		return new ItemMultiTexture(this, this, function);
	}
	
	public abstract int getMetaForType(E type);

	public abstract E getTypeForMeta(int meta);
}
