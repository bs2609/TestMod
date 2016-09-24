package mod.proxy;

import mod.block.ModBlocks;
import mod.crafting.ModCrafting;
import mod.event.CommonEventHandlers;
import mod.item.ModItems;
import mod.world.ModDimensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy implements IProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		ModBlocks.init();
		ModItems.init();
		ModCrafting.init();
		ModDimensions.init();

		MinecraftForge.EVENT_BUS.register(new CommonEventHandlers());
	}

	@Override
	public void init(FMLInitializationEvent event) {

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {

	}
}
