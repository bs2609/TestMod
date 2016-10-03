package mod.proxy;

import mod.block.ModBlocks;
import mod.crafting.ModCrafting;
import mod.event.CommonEventHandlers;
import mod.item.ModItems;
import mod.network.ModPacketHandler;
import mod.world.ModDimensions;
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
		ModPacketHandler.init();
		CommonEventHandlers.register();
	}

	@Override
	public void init(FMLInitializationEvent event) {

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {

	}
}
