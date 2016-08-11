package mod.world;

import mod.util.MiscUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkGenerator;

public class TestWorldProvider extends WorldProvider {

	public static final int ID = 88;

	@Override
	protected void createBiomeProvider() {
		biomeProvider = ModDimensions.getSpecifier(getDimension()).getBiomeProvider(worldObj);
	}

	@Override
	protected void generateLightBrightnessTable() {
		float[] table = MiscUtils.generateLightBrightnessTable(3.0f, 0.25f);
		System.arraycopy(table, 0, lightBrightnessTable, 0, 16);
	}

	@Override
	public IChunkGenerator createChunkGenerator() {
		return ModDimensions.getSpecifier(getDimension()).getChunkGenerator(worldObj);
	}

	@Override
	public DimensionType getDimensionType() {
		return ModDimensions.test;
	}
	
	@Override
	public boolean isSurfaceWorld() {
		return false;
	}
	
	@Override
	public boolean canRespawnHere() {
		return super.canRespawnHere();
	}

	@Override
	public boolean canCoordinateBeSpawn(int x, int z) {
		return worldObj.getGroundAboveSeaLevel(new BlockPos(x, 0, z)).getMaterial().blocksMovement();
	}

	@Override
	public double getMovementFactor() {
		return ModDimensions.getMovementFactor(getDimension());
	}
}
