package mod.util;

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
		
		private static final String[] methodNames = {"renderSkyEnd", "func_180448_r"};
		private static final Method renderMethod = ReflectionHelper.findMethod(RenderGlobal.class, null, methodNames);
		
		@Override
		public void render(float partialTicks, WorldClient world, Minecraft mc) {
			try {
				renderMethod.invoke(mc.renderGlobal);
			} catch (Exception e) {}
		}
	}
	
}
