package mod.world.gen;

import java.util.Random;

public abstract class SeededTerrainGenerator implements ITerrainGenerator {

	protected final long seed;

	protected SeededTerrainGenerator(long seed) {
		this.seed = seed;
	}

	protected Random getRandom(int x, int z) {
		Random random = new Random(seed);
		long mx = random.nextLong(), mz = random.nextLong();
		random.setSeed(x*mx + z*mz ^ seed);
		return random;
	}
}
