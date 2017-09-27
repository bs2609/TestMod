package mod.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderUtils {
	
	public static class EndSkyRenderer extends IRenderHandler {
		
		private static final ReflectionUtils.MethodAccessor renderMethod
				= new ReflectionUtils.MethodAccessor(RenderGlobal.class, "renderSkyEnd", "func_180448_r");
		
		@Override
		public void render(float partialTicks, WorldClient world, Minecraft mc) {
			renderMethod.invoke(mc.renderGlobal);
		}
	}
	
	public static class CloudRenderer extends IRenderHandler {
		
		private static final ReflectionUtils.MethodAccessor renderMethod
				= new ReflectionUtils.MethodAccessor(RenderGlobal.class, "renderCloudsFancy", "func_180445_c", float.class, int.class);
		
		@Override
		public void render(float partialTicks, WorldClient world, Minecraft mc) {
			renderMethod.invoke(mc.renderGlobal, partialTicks, 2);
		}
	}
}
