package mod.item;

import mod.TestMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class BasicItem extends Item {
	
	protected BasicItem(String name) {
		setUnlocalizedName(TestMod.MOD_ID + "." + name);
		setRegistryName(name);
		GameRegistry.register(this);
	}
	
	protected BasicItem(String name, CreativeTabs tab) {
		this(name);
		setCreativeTab(tab);
	}
}
