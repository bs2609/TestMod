package mod.world.gen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class PillarGenerator extends SeededTerrainGenerator {
	
	private final IBlockState column = Blocks.WATER.getDefaultState();
	private final IBlockState platform = Blocks.STONEBRICK.getDefaultState();
	
	private final int minY = 32, maxY = 96, maxDist = 8;
	
	public PillarGenerator(long seed) {
		super(seed);
	}
	
	@Override
	public void generate(int x, int z, ChunkPrimer primer) {
		int levels = getPillarLevels(x, z);
		if (levels != 0) {
			generatePillar(x, z, levels, primer);
		} else {
			generateBridges(x, z, primer);
		}
	}
	
	private int getPillarLevels(int x, int z) {
		Random random = new Random(seed);
		long mx = random.nextLong(), mz = random.nextLong();
		random.setSeed(x*mx + z*mz ^ seed);
		return random.nextInt(5) == 0 ? random.nextInt() : 0;
	}
	
	private void generatePillar(int x, int z, int levels, ChunkPrimer primer) {
		for (int i = minY >> 3; i <= maxY >> 3; ++i) {
			if ((levels & 1 << i) != 0) {
				generatePlatform(i, primer);
				generateEdges(x, z, i, primer);
			}
		}
		generateColumn(primer);
	}
	
	private void generatePlatform(int level, ChunkPrimer primer) {
		fill(4, 12, 4, 12, level << 3, primer, platform);
	}
	
	private void generateColumn(ChunkPrimer primer) {
		for (int x = 6; x < 10; ++x) {
			for (int z = 6; z < 10; ++z) {
				for (int y = 0; y <= 128; ++y) {
					primer.setBlockState(x, y, z, column);
				}
			}
		}
	}
	
	private void generateBridges(int x, int z, ChunkPrimer primer) {
		for (int i = minY >> 3; i <= maxY >> 3; ++i) {
			generateConnections(x, z, i, primer);
		}
	}
	
	private void generateConnections(int x, int z, int level, ChunkPrimer primer) {
		for (int i = 1; i < maxDist; ++i) {
			int n = getPillarLevels(x, z-i);
			if (n == 0) continue;
			if ((n & 1 << level) == 0) break;
			for (int j = 1; j < maxDist-i; ++j) {
				int s = getPillarLevels(x, z+j);
				if (s == 0) continue;
				if ((s & 1 << level) == 0) break;
				fill(6, 10, 0, 16, level << 3, primer, platform);
				break;
			}
			break;
		}
		for (int i = 1; i < maxDist; ++i) {
			int w = getPillarLevels(x-i, z);
			if (w == 0) continue;
			if ((w & 1 << level) == 0) break;
			for (int j = 1; j < maxDist-i; ++j) {
				int e = getPillarLevels(x+j, z);
				if (e == 0) continue;
				if ((e & 1 << level) == 0) break;
				fill(0, 16, 6, 10, level << 3, primer, platform);
				break;
			}
			break;
		}
	}
	
	private void generateEdges(int x, int z, int level, ChunkPrimer primer) {
		for (int i = 1; i < maxDist; ++i) {
			int n = getPillarLevels(x, z-i);
			if (n == 0) continue;
			if ((n & 1 << level) != 0) {
				fill(6, 10, 0, 4, level << 3, primer, platform);
			}
			break;
		}
		for (int i = 1; i < maxDist; ++i) {
			int s = getPillarLevels(x, z+i);
			if (s == 0) continue;
			if ((s & 1 << level) != 0) {
				fill(6, 10, 12, 16, level << 3, primer, platform);
			}
			break;
		}
		for (int i = 1; i < maxDist; ++i) {
			int w = getPillarLevels(x-i, z);
			if (w == 0) continue;
			if ((w & 1 << level) != 0) {
				fill(0, 4, 6, 10, level << 3, primer, platform);
			}
			break;
		}
		for (int i = 1; i < maxDist; ++i) {
			int e = getPillarLevels(x+i, z);
			if (e == 0) continue;
			if ((e & 1 << level) != 0) {
				fill(12, 16, 6, 10, level << 3, primer, platform);
			}
			break;
		}
	}
	
	private void fill(int minX, int maxX, int minZ, int maxZ, int y, ChunkPrimer primer, IBlockState state) {
		for (int x = minX; x < maxX; ++x) {
			for (int z = minZ; z < maxZ; ++z) {
				primer.setBlockState(x, y, z, state);
			}
		}
	}
}
