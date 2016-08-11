package mod.world.gen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class CrossingGenerator extends SeededTerrainGenerator {

	private final int layers = 16;

	private final IBlockState state = Blocks.ICE.getDefaultState();

	public CrossingGenerator(long seed) {
		super(seed);
	}

	@Override
	public void generate(int x, int z, ChunkPrimer primer) {
		Random r = new Random(seed);
		long rx = r.nextLong(), rz = r.nextLong();

		Random xRandom = new Random(seed ^ x*rx);
		Random zRandom = new Random(seed ^ z*rz);

		byte[][] xy = new byte[16][layers];
		byte[][] zy = new byte[16][layers];

		for (int i = 0; i < 16; ++i) {
			xRandom.nextBytes(xy[i]);
			zRandom.nextBytes(zy[i]);
		}

		fill(xy, zy, primer);
	}

	private void fill(byte[][] xy, byte[][] zy, ChunkPrimer primer) {
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				for (int i = 0; i < layers; ++i) {
					primer.setBlockState(x, xy[x][i] & 0xff, z, state);
					primer.setBlockState(x, zy[z][i] & 0xff, z, state);
				}
			}
		}
	}
}
