package mod.event;

import mod.block.SurrealBlockModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventHandlers {
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new SurrealBlockModel.EventHandler());
	}
}
