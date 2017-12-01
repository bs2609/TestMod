package mod.proxy;

import mod.block.ModBlocks;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends DefaultProxy {
	
	@Override
	public void init(FMLInitializationEvent event) {
		ModBlocks.registerHandlers();
	}
}
