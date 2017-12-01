package mod.item;

import mod.TestMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public abstract class BasicItem extends Item {
	
	protected BasicItem(String name) {
		setUnlocalizedName(TestMod.MOD_ID + "." + name);
	}
	
	protected BasicItem(String name, CreativeTabs tab) {
		this(name);
		setCreativeTab(tab);
	}
}
