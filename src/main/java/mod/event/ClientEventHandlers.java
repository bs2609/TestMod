package mod.event;

import mod.gui.ModDebugInfo;
import mod.model.SurrealBlockModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventHandlers {
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new SurrealBlockModel.EventHandler());
		MinecraftForge.EVENT_BUS.register(new ModDebugInfo.EventHandler());
	}
}
