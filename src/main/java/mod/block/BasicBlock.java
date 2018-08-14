package mod.block;

import mod.TestMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;

public abstract class BasicBlock extends Block {

	protected BasicBlock(String name, Material material) {
		super(material);
		setTranslationKey(TestMod.MOD_ID + "." + name);
	}
	
	protected BasicBlock(String name, Material material, CreativeTabs tab) {
		this(name, material);
		setCreativeTab(tab);
	}
	
	protected ItemBlock createItemBlock() {
		ItemBlock itemBlock = new ItemBlock(this);
		itemBlock.setRegistryName(this.getRegistryName());
		return itemBlock;
	}
}
