package mod.world.gen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Random;

public class ModWorldGenerators implements IWorldGenerator {
	
	private final WorldGenerator portalRuinsGenerator = new PortalRuinsWorldGenerator();
	
	public static void register() {
		GameRegistry.registerWorldGenerator(new ModWorldGenerators(), 5);
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		BlockPos basePos = new BlockPos(chunkX << 4, 0, chunkZ << 4);
		if (world.provider.getDimensionType() == DimensionType.OVERWORLD) {
			generatePortalRuins(world, random, basePos);
		}
	}
	
	private void generatePortalRuins(World world, Random random, BlockPos pos) {
		if (random.nextInt(1024) == 0) {
			portalRuinsGenerator.generate(world, random, world.getTopSolidOrLiquidBlock(getOffsetPos(random, pos)));
		}
	}
	
	private static BlockPos getOffsetPos(Random random, BlockPos pos) {
		int dx = random.nextInt(16) + 8;
		int dz = random.nextInt(16) + 8;
		return pos.add(dx, 0, dz);
	}
}
