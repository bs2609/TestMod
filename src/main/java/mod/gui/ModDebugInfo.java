package mod.gui;

import mod.TestMod;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ModDebugInfo {
	
	private static final SortedMap<String, IDebugInfo> providers = new TreeMap<String, IDebugInfo>();
	
	public static void register(String id, IDebugInfo provider) {
		providers.put(id, provider);
	}
	
	public static class EventHandler {
		
		@SubscribeEvent
		public void onRenderDebugInfo(RenderGameOverlayEvent.Text event) {
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
