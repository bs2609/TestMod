package mod.world.gen;

public abstract class SeededTerrainGenerator implements ITerrainGenerator {

	protected final long seed;

	protected SeededTerrainGenerator(long seed) {
		this.seed = seed;
	}
}
