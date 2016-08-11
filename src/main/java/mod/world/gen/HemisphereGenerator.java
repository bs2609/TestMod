package mod.world.gen;

import mod.util.MiscUtils;
import net.minecraft.init.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HemisphereGenerator extends ShapeGenerator {

	private class Hemisphere extends Shape {

		final int cx, cy, cz;
		final int size;

		Hemisphere(int x, int z, Random random) {
			cx = (x << 4) + random.nextInt(16);
			cz = (z << 4) + random.nextInt(16);
			cy = random.nextInt(256);
			size = random.nextInt(16);
		}

		public boolean test(int x, int y, int z) {
			int dx = Math.abs(x-cx), dy = Math.abs(y-cy), dz = Math.abs(z-cz);
			return y <= cy && Math.round(Math.sqrt(dx*dx + dy*dy + dz*dz)) <= size;
		}
	}

	public HemisphereGenerator(long seed) {
		super(seed, Blocks.END_STONE.getDefaultState());
	}

	@Override
	protected Shape[] getShapes(int x, int z) {
		List<Hemisphere> hemispheres = new ArrayList<Hemisphere>();
		for (int dx = -2; dx <= 2; ++dx) {
			for (int dz = -2; dz <= 2; ++dz) {
				long xz = MiscUtils.getChunkSeed(x+dx, z+dz);
				Random chunkRandom = new Random(seed ^ xz);
				for (int i = 0; i < 16; ++i) {
					if (chunkRandom.nextInt(16) != 0) continue;
					hemispheres.add(new Hemisphere(dx, dz, chunkRandom));
				}
			}
		}
		return hemispheres.toArray(new Hemisphere[0]);
	}
}
