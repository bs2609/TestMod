package mod.world;

import mod.world.gen.SurrealChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkGenerator;

public class SurrealWorldProvider extends WorldProvider {

	public static final int ID = 39;

	@Override
	protected void init() {
		super.init();
		hasSkyLight = false;
	}

	@Override
	protected void generateLightBrightnessTable() {
		super.generateLightBrightnessTable();
	}

	@Override
	public IChunkGenerator createChunkGenerator() {
		return new SurrealChunkGenerator(world);
	}

	@Override
	public DimensionType getDimensionType() {
		return ModDimensions.surreal;
	}

	@Override
	public boolean isSurfaceWorld() {
		return false;
	}

	@Override
	public boolean canRespawnHere() {
		return false;
	}

	@Override
	public boolean canCoordinateBeSpawn(int x, int z) {
		return world.getGroundAboveSeaLevel(new BlockPos(x, 0, z)).getMaterial().blocksMovement();
	}
}
