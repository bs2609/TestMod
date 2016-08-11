package mod.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class MiscUtils {

	public static long getChunkSeed(int x, int z) {
		int bits = 24;
		long mask = (1L << bits) - 1L;
		return (long) x & mask | ((long) z & mask) << bits;
	}

	public static float[] generateLightBrightnessTable(float a, float b) {
		float[] table = new float[16];
		for (int i = 0; i < 16; ++i) {
			float f = i / 15.0f;
			table[i] = f / (1.0f + a - f * a) * (1.0f - b) + b;
		}
		return table;
	}

	public static WorldServer worldServerForDimension(int dimension) {
		WorldServer world = DimensionManager.getWorld(dimension);
		if (world == null) {
			DimensionManager.initDimension(dimension);
			world = DimensionManager.getWorld(dimension);
		}
		return world;
	}
	
	public static void setInvulnerableDimensionChange(EntityPlayerMP player) {
		String[] fieldNames = {"invulnerableDimensionChange", "field_184851_cj"};
		ReflectionHelper.setPrivateValue(EntityPlayerMP.class, player, true, fieldNames);
	}
}
