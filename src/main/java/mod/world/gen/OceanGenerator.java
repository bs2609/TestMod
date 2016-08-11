package mod.world.gen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

public class OceanGenerator extends BasicTerrainGenerator {

	private final int height = 128;

	private final IBlockState state = Blocks.WATER.getDefaultState();

	@Override
	public void generate(ChunkPrimer primer) {
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				for (int y = 0; y < height; ++y) {
					primer.setBlockState(x, y, z, state);
				}
			}
		}
	}
}
