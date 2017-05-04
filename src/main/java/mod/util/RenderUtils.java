package mod.util;

import mod.TestMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Method;

@SideOnly(Side.CLIENT)
public class RenderUtils {
	
	public static class EndSkyRenderer extends IRenderHandler {
		
		private static final Method renderMethod = ReflectionHelper.findMethod(RenderGlobal.class, "renderSkyEnd", "func_180448_r");
		
		@Override
		public void render(float partialTicks, WorldClient world, Minecraft mc) {
			try {
				renderMethod.invoke(mc.renderGlobal);
			} catch (Exception e) {
				TestMod.getLogger().warn("Exception invoking " + renderMethod, e);
			}
		}
	}
	
	public static class CloudRenderer extends IRenderHandler {
		
		private static final Method renderMethod = ReflectionHelper.findMethod(RenderGlobal.class, "renderCloudsFancy", "func_180445_c", float.class, int.class);
		
		@Override
		public void render(float partialTicks, WorldClient world, Minecraft mc) {
			try {
				renderMethod.invoke(mc.renderGlobal, partialTicks, 2);
			} catch (Exception e) {
				TestMod.getLogger().warn("Exception invoking " + renderMethod, e);
			}
		}
	}
}
