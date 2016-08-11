package mod.world.gen;

import mod.util.MiscUtils;
import net.minecraft.init.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PyramidGenerator extends ShapeGenerator {

	private class Pyramid extends Shape {

		final int cx, cy, cz;
		final int size;

		Pyramid(int x, int z, Random random) {
			cx = (x << 4) + random.nextInt(16);
			cz = (z << 4) + random.nextInt(16);
			cy = random.nextInt(128);
			size = random.nextInt(128);
		}

		public boolean test(int x, int y, int z) {
			return Math.abs(x-cx) + Math.abs(z-cz) + (y-cy) == size;
		}
	}

	public PyramidGenerator(long seed) {
		super(seed, Blocks.STONEBRICK.getDefaultState());
	}

	@Override
	protected Pyramid[] getShapes(int x, int z) {
		List<Pyramid> pyramids = new ArrayList<Pyramid>();
		for (int dx = -15; dx <= 15; ++dx) {
			for (int dz = -15; dz <= 15; ++dz) {
				long xz = MiscUtils.getChunkSeed(x+dx, z+dz);
				Random chunkRandom = new Random(seed ^ xz);
				if (chunkRandom.nextInt(16) != 0) continue;
				pyramids.add(new Pyramid(dx, dz, chunkRandom));
			}
		}
		return pyramids.toArray(new Pyramid[0]);
	}
}
