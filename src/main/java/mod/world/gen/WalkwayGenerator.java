package mod.world.gen;

import mod.util.MiscUtils;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class WalkwayGenerator extends SeededTerrainGenerator {

	private final int baseLevel = 64;

	private final IBlockState base = Blocks.STONEBRICK.getDefaultState();
	private final IBlockState join = Blocks.STONE_BRICK_STAIRS.getDefaultState();
	private final IBlockState clear = Blocks.AIR.getDefaultState();

	private final int[] north = {0, 1, 2}, south = {6, 7, 8}, west = {0, 3, 6}, east = {2, 5, 8};

	public WalkwayGenerator(long seed) {
		super(seed);
	}
	
	public WalkwayGenerator(long seed, int layer) {
		super(deriveSeed(seed, layer));
	}
	
	private static long deriveSeed(long seed, int n) {
		Random random = new Random(~seed);
		long offset = n * random.nextLong();
		return seed ^ offset;
	}
	
	@Override
	public void generate(int x, int z, ChunkPrimer primer) {
		int level = getLevel(x, z);
		if (!checkLevel(level)) return;
		boolean[] grid = getGrid(x, z);
		fillChunk(level, grid, primer);
		joinEdges(x, z, level, grid, primer);
	}
	
	private int getLevel(int x, int z) {
		Random random = new Random(seed);
		int total = Math.abs(x) + Math.abs(z);
		int acc = 0, level = 0;
		while (acc < total) {
			acc += random.nextInt(16) + 1;
			level += (random.nextInt(30) >= level + 15) ? 1 : -1;
		}
		return level;
	}
	
	private boolean checkLevel(int level) {
		int y = baseLevel + (level << 2);
		return y > 0 && y < 256;
	}
	
	private boolean[] getGrid(int x, int z) {
		final byte[] weights = {0, 32, 0, 32, 64, 32, 0, 32, 0};
		boolean[] grid = new boolean[9];
		long xz = MiscUtils.getChunkSeed(x, z);
		Random random = new Random(seed ^ xz);
		byte[] bytes = new byte[9];
		random.nextBytes(bytes);
		for (int i = 0; i < 9; ++i) {
			grid[i] = bytes[i] < weights[i];
		}
		return grid;
	}

	private void fillChunk(int level, boolean[] grid, ChunkPrimer primer) {
		int y = baseLevel + (level << 2);
		for (int x = 2; x < 14; ++x) {
			for (int z = 2; z < 14; ++z) {
				int i = (x-2) >> 2, j = (z-2) >> 2;
				if (grid[3*j+i]) {
					placeBlockColumn(x, y, z, primer, base);
				}
			}
		}
	}

	private void joinEdges(int x, int z, int level, boolean[] grid, ChunkPrimer primer) {
		joinEdgeN(x, z, level, grid, primer);
		joinEdgeS(x, z, level, grid, primer);
		joinEdgeW(x, z, level, grid, primer);
		joinEdgeE(x, z, level, grid, primer);
	}

	private void joinEdgeN(int x, int z, int level, boolean[] grid, ChunkPrimer primer) {

		IBlockState up = join.withProperty(BlockStairs.FACING, EnumFacing.NORTH);
		IBlockState down = join.withProperty(BlockStairs.FACING, EnumFacing.SOUTH);

		IBlockState upAlt = down.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
		IBlockState downAlt = up.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);

		int adjLevel = getLevel(x, z-1);
		if (!checkLevel(adjLevel)) return;

		boolean[] adjGrid = getGrid(x, z-1);

		int cy = baseLevel + (level << 2);
		for (int cx = 2; cx < 14; ++cx) {
			int i = (cx-2) >> 2;
			if (grid[north[i]] && adjGrid[south[i]]) {
				if (level < adjLevel) {
					placeBlockColumn(cx, cy+2, 0, primer, up, upAlt);
					placeBlockColumn(cx, cy+1, 1, primer, up, upAlt);
				} else if (level > adjLevel) {
					placeBlockColumn(cx, cy-1, 0, primer, down, downAlt);
					placeBlockColumn(cx, cy  , 1, primer, down, downAlt);
				} else {
					placeBlockColumn(cx, cy, 0, primer, base);
					placeBlockColumn(cx, cy, 1, primer, base);
				}
			}
		}
	}

	private void joinEdgeS(int x, int z, int level, boolean[] grid, ChunkPrimer primer) {

		IBlockState up = join.withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
		IBlockState down = join.withProperty(BlockStairs.FACING, EnumFacing.NORTH);

		IBlockState upAlt = down.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
		IBlockState downAlt = up.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);

		int adjLevel = getLevel(x, z+1);
		if (!checkLevel(adjLevel)) return;

		boolean[] adjGrid = getGrid(x, z+1);

		int cy = baseLevel + (level << 2);
		for (int cx = 2; cx < 14; ++cx) {
			int i = (cx-2) >> 2;
			if (grid[south[i]] && adjGrid[north[i]]) {
				if (level < adjLevel) {
					placeBlockColumn(cx, cy+1, 14, primer, up, upAlt);
					placeBlockColumn(cx, cy+2, 15, primer, up, upAlt);
				} else if (level > adjLevel) {
					placeBlockColumn(cx, cy  , 14, primer, down, downAlt);
					placeBlockColumn(cx, cy-1, 15, primer, down, downAlt);
				} else {
					placeBlockColumn(cx, cy, 14, primer, base);
					placeBlockColumn(cx, cy, 15, primer, base);
				}
			}
		}
	}

	private void joinEdgeW(int x, int z, int level, boolean[] grid, ChunkPrimer primer) {

		IBlockState up = join.withProperty(BlockStairs.FACING, EnumFacing.WEST);
		IBlockState down = join.withProperty(BlockStairs.FACING, EnumFacing.EAST);

		IBlockState upAlt = down.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
		IBlockState downAlt = up.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);

		int adjLevel = getLevel(x-1, z);
		if (!checkLevel(adjLevel)) return;

		boolean[] adjGrid = getGrid(x-1, z);

		int cy = baseLevel + (level << 2);
		for (int cz = 2; cz < 14; ++cz) {
			int i = (cz-2) >> 2;
			if (grid[west[i]] && adjGrid[east[i]]) {
				if (level < adjLevel) {
					placeBlockColumn(0, cy+2, cz, primer, up, upAlt);
					placeBlockColumn(1, cy+1, cz, primer, up, upAlt);
				} else if (level > adjLevel) {
					placeBlockColumn(0, cy-1, cz, primer, down, downAlt);
					placeBlockColumn(1, cy  , cz, primer, down, downAlt);
				} else {
					placeBlockColumn(0, cy, cz, primer, base);
					placeBlockColumn(1, cy, cz, primer, base);
				}
			}
		}
	}

	private void joinEdgeE(int x, int z, int level, boolean[] grid, ChunkPrimer primer) {

		IBlockState up = join.withProperty(BlockStairs.FACING, EnumFacing.EAST);
		IBlockState down = join.withProperty(BlockStairs.FACING, EnumFacing.WEST);

		IBlockState upAlt = down.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
		IBlockState downAlt = up.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);

		int adjLevel = getLevel(x+1, z);
		if (!checkLevel(adjLevel)) return;

		boolean[] adjGrid = getGrid(x+1, z);

		int cy = baseLevel + (level << 2);
		for (int cz = 2; cz < 14; ++cz) {
			int i = (cz-2) >> 2;
			if (grid[east[i]] && adjGrid[west[i]]) {
				if (level < adjLevel) {
					placeBlockColumn(14, cy+1, cz, primer, up, upAlt);
					placeBlockColumn(15, cy+2, cz, primer, up, upAlt);
				} else if (level > adjLevel) {
					placeBlockColumn(14, cy  , cz, primer, down, downAlt);
					placeBlockColumn(15, cy-1, cz, primer, down, downAlt);
				} else {
					placeBlockColumn(14, cy, cz, primer, base);
					placeBlockColumn(15, cy, cz, primer, base);
				}
			}
		}
	}

	private void placeBlockColumn(int x, int y, int z, ChunkPrimer primer, IBlockState state) {
		primer.setBlockState(x, y, z, state);
		for (int dy = 1; dy < 4; ++dy) {
			primer.setBlockState(x, y+dy, z, clear);
		}
	}

	private void placeBlockColumn(int x, int y, int z, ChunkPrimer primer, IBlockState state, IBlockState under) {
		placeBlockColumn(x, y, z, primer, state);
		primer.setBlockState(x, y-1, z, under);
	}
}
