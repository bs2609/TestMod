package mod.block;

import com.google.common.base.Function;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class VariantBlock<E extends Enum<E> & IStringSerializable> extends Block {

	protected VariantBlock(String name, Material material) {
		super(material);
		setUnlocalizedName(name);
		setRegistryName(name);
		Function<ItemStack, String> function = new Function<ItemStack, String>() {
			@Override
			public String apply(ItemStack input) {
				return getTypeForMeta(input.getMetadata()).getName();
			}
		};
		GameRegistry.register(this);
		GameRegistry.register(new ItemMultiTexture(this, this, function), getRegistryName());
	}

	public abstract int getMetaForType(E type);

	public abstract E getTypeForMeta(int meta);
}
