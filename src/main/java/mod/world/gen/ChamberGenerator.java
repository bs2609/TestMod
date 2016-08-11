package mod.world.gen;

import mod.util.MiscUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class ChamberGenerator extends SeededTerrainGenerator {

	private final IBlockState platform = Blocks.PRISMARINE.getDefaultState();
	private final IBlockState air = Blocks.AIR.getDefaultState();

	public ChamberGenerator(long seed) {
		super(seed);
	}

	@Override
	public void generate(int x, int z, ChunkPrimer primer) {

		long xz = MiscUtils.getChunkSeed(x, z);
		Random random = new Random(seed ^ xz);

		int min = random.nextInt(96) + 16;
		int max = min + random.nextInt(12) + 4;

		generateChamber(min, max, primer);
	}

	private void generateChamber(int min, int max, ChunkPrimer primer) {
		for (int x = 1; x < 15; ++x) {
			for (int z = 1; z < 15; ++z) {
				primer.setBlockState(x, min, z, platform);
				for (int y = min+1; y < max; ++y) {
					primer.setBlockState(x, y, z, air);
				}
				primer.setBlockState(x, max, z ,platform);
			}
		}
	}
}
