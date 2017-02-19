package mod.proxy;

import mod.block.ModBlocks;
import mod.event.ClientEventHandlers;
import mod.item.ModItems;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends DefaultProxy {
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		ClientEventHandlers.register();
		ModBlocks.initModels();
		ModItems.initModels();
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		ModBlocks.registerHandlers();
	}
}
