package mod.world.gen;

import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class TowerGenerator extends SeededTerrainGenerator {
	
	private final IBlockState air = Blocks.AIR.getDefaultState();
	private final IBlockState brick = Blocks.NETHER_BRICK.getDefaultState();
	private final IBlockState glass = Blocks.STAINED_GLASS.getDefaultState()
			.withProperty(BlockStainedGlass.COLOR, EnumDyeColor.BLACK);
	
	private final int[] north = {0, 1}, south = {2, 3}, west = {0, 2}, east = {1, 3};
	
	private final int[] floorX = {0, 1, 2, 3, 3, 3, 3, 2, 1, 0, 0, 0};
	private final int[] floorZ = {0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 2, 1};
	
	private final int[] windowX = {1, 2, 4, 4, 2, 1, -1, -1};
	private final int[] windowZ = {-1, -1, 1, 2, 4, 4, 2, 1};
	
	private final IBlockState[] connection = {brick, air, air, air};
	private final IBlockState[] window = {brick, glass, glass, glass};
	
	public TowerGenerator(long seed) {
		super(seed);
	}
	
	private int[] getHeights(int x, int z) {
		Random random = getRandom(x, z);
		int[] heights = new int[4];
		for (int i = 0; i < 4; ++i) {
			heights[i] = 64 + random.nextInt(128);
		}
		return heights;
	}
	
	private int[] getConnections(int x, int z) {
		Random random = getRandom(x, z);
		int[] connections = new int[4];
		for (int i = 0; i < 4; ++i) {
			connections[i] = random.nextInt();
		}
		return connections;
	}
	
	@Override
	public void generate(int x, int z, ChunkPrimer primer) {
		int[] heights = getHeights(x, z);
		int[] connections = getConnections(x, z);
		generateTowers(heights, connections, primer);
		generateInternalConnections(heights, connections, primer);
		generateExternalConnections(x, z, heights, connections, primer);
	}
	
	private void generateTowers(int[] heights, int[] connections, ChunkPrimer primer) {
		generateTower(0, 0, heights[0], connections[0], primer);
		generateTower(8, 0, heights[1], connections[1], primer);
		generateTower(0, 8, heights[2], connections[2], primer);
		generateTower(8, 8, heights[3], connections[3], primer);
	}
	
	private void generateTower(int dx, int dz, int height, int connections, ChunkPrimer primer) {
		for (int x = 1; x < 7; ++x) {
			for (int z = 1; z < 7; ++z) {
				IBlockState state = (x == 1 || x == 6 || z == 1 || z == 6) ? brick : air;
				for (int y = 0; y < height; ++y) {
					primer.setBlockState(x+dx, y, z+dz, state);
				}
			}
		}
		generateLandings(dx+2, dz+2, height, connections, primer);
		fill(dx+2, dx+6, dz+2, dz+6, height-1, primer, glass);
	}
	
	private void generateLandings(int x, int z, int height, int connections, ChunkPrimer primer) {
		for (int i = 1; i < height >> 3; ++i) {
			if ((connections & 1 << i) != 0) {
				for (int j = 0; j < 12; ++j) {
					primer.setBlockState(x + floorX[j], i << 3, z + floorZ[j], brick);
				}
				for (int j = 0; j < 8; ++j) {
					placeColumn(x + windowX[j], i << 3, z + windowZ[j], primer, window);
				}
			}
		}
	}
	
	private void generateInternalConnections(int[] heights, int[] connections, ChunkPrimer primer) {
		int hn = Math.min(heights[north[0]], heights[north[1]]);
		int cn = connections[north[0]] & connections[north[1]];
		for (int i = 1; i < hn >> 3; ++i) {
			if ((cn & 1 << i) != 0) {
				buildConnection(6, 10, 3, 5, i << 3, primer);
			}
		}
		int hs = Math.min(heights[south[0]], heights[south[1]]);
		int cs = connections[south[0]] & connections[south[1]];
		for (int i = 1; i < hs >> 3; ++i) {
			if ((cs & 1 << i) != 0) {
				buildConnection(6, 10, 11, 13, i << 3, primer);
			}
		}
		int hw = Math.min(heights[west[0]], heights[west[1]]);
		int cw = connections[west[0]] & connections[west[1]];
		for (int i = 1; i < hw >> 3; ++i) {
			if ((cw & 1 << i) != 0) {
				buildConnection(3, 5, 6, 10, i << 3, primer);
			}
		}
		int he = Math.min(heights[east[0]], heights[east[1]]);
		int ce = connections[east[0]] & connections[east[1]];
		for (int i = 1; i < he >> 3; ++i) {
			if ((ce & 1 << i) != 0) {
				buildConnection(11, 13, 6, 10, i << 3, primer);
			}
		}
	}
	
	private void generateExternalConnections(int x, int z, int[] heights, int[] connections, ChunkPrimer primer) {
		
		int[] adjHeights, adjConnections;
		
		adjHeights = getHeights(x, z-1);
		adjConnections = getConnections(x, z-1);
		for (int i = 0; i < 2; ++i) {
			int hn = Math.min(heights[north[i]], adjHeights[south[i]]);
			int cn = connections[north[i]] & adjConnections[south[i]];
			for (int j = 1; j < hn >> 3; ++j) {
				if ((cn & 1 << j) != 0) {
					buildConnection(3 + (i << 3), 5 + (i << 3), 0, 2, j << 3, primer);
				}
			}
		}
		
		adjHeights = getHeights(x, z+1);
		adjConnections = getConnections(x, z+1);
		for (int i = 0; i < 2; ++i) {
			int hn = Math.min(heights[south[i]], adjHeights[north[i]]);
			int cn = connections[south[i]] & adjConnections[north[i]];
			for (int j = 1; j < hn >> 3; ++j) {
				if ((cn & 1 << j) != 0) {
					buildConnection(3 + (i << 3), 5 + (i << 3), 14, 16, j << 3, primer);
				}
			}
		}
		
		adjHeights = getHeights(x-1, z);
		adjConnections = getConnections(x-1, z);
		for (int i = 0; i < 2; ++i) {
			int hn = Math.min(heights[west[i]], adjHeights[east[i]]);
			int cn = connections[west[i]] & adjConnections[east[i]];
			for (int j = 1; j < hn >> 3; ++j) {
				if ((cn & 1 << j) != 0) {
					buildConnection(0, 2, 3 + (i << 3), 5 + (i << 3), j << 3, primer);
				}
			}
		}
		
		adjHeights = getHeights(x+1, z);
		adjConnections = getConnections(x+1, z);
		for (int i = 0; i < 2; ++i) {
			int hn = Math.min(heights[east[i]], adjHeights[west[i]]);
			int cn = connections[east[i]] & adjConnections[west[i]];
			for (int j = 1; j < hn >> 3; ++j) {
				if ((cn & 1 << j) != 0) {
					buildConnection(14, 16, 3 + (i << 3), 5 + (i << 3), j << 3, primer);
				}
			}
		}
	}
	
	private void fill(int x1, int x2, int z1, int z2, int y, ChunkPrimer primer, IBlockState state) {
		for (int x = x1; x < x2; ++x) {
			for (int z = z1; z < z2; ++z) {
				primer.setBlockState(x, y, z, state);
			}
		}
	}
	
	private void buildConnection(int x1, int x2, int z1, int z2, int y, ChunkPrimer primer) {
		for (int x = x1; x < x2; ++x) {
			for (int z = z1; z < z2; ++z) {
				placeColumn(x, y, z, primer, connection);
			}
		}
	}
	
	private void placeColumn(int x, int y, int z, ChunkPrimer primer, IBlockState... column) {
		for (int i = 0; i < column.length; ++i) {
			primer.setBlockState(x, y+i, z, column[i]);
		}
	}
}
