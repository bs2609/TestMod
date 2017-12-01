package mod.gui;

import mod.TestMod;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModDebugInfo {
	
	private static final Map<String, IDebugInfo> providers = new LinkedHashMap<String, IDebugInfo>();
	
	public static void register(String id, IDebugInfo provider) {
		providers.put(id, provider);
	}
	
	@Mod.EventBusSubscriber(value = Side.CLIENT, modid = TestMod.MOD_ID)
	public static class EventHandler {
		
		@SubscribeEvent
		public static void onRenderDebugInfo(RenderGameOverlayEvent.Text event) {
			if (!Minecraft.getMinecraft().gameSettings.showDebugInfo) return;
			List<String> left = event.getLeft();
			left.add("");
			left.add("[" + TestMod.MOD_ID + "]");
			for (Map.Entry<String, IDebugInfo> entry : providers.entrySet()) {
				left.add(entry.getKey() + ": " + entry.getValue().getInfoText());
			}
		}
	}
}
