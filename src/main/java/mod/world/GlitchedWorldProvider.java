package mod.world;

import mod.world.gen.GlitchedChunkGenerator;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.gen.IChunkGenerator;

public class GlitchedWorldProvider extends WorldProvider {
	
	public static final int ID = 101;
	
	@Override
	public IChunkGenerator createChunkGenerator() {
		return new GlitchedChunkGenerator(world);
	}
	
	@Override
	public DimensionType getDimensionType() {
		return ModDimensions.glitched;
	}
	
	@Override
	public boolean isSurfaceWorld() {
		return false;
	}
	
	@Override
	public boolean canRespawnHere() {
		return false;
	}
}
