package mod.block;

import mod.TestMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class BasicBlock extends Block {

	protected BasicBlock(String name, Material material) {
		super(material);
		setUnlocalizedName(TestMod.MOD_ID + "." + name);
		setRegistryName(name);
		GameRegistry.register(this);
		GameRegistry.register(createItemBlock(), getRegistryName());
	}
	
	protected BasicBlock(String name, Material material, CreativeTabs tab) {
		this(name, material);
		setCreativeTab(tab);
	}
	
	protected ItemBlock createItemBlock() {
		return new ItemBlock(this);
	}
}
