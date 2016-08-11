package mod.world.gen;

import net.minecraft.world.chunk.ChunkPrimer;

public abstract class BasicTerrainGenerator implements ITerrainGenerator {

	@Override
	public void generate(int x, int z, ChunkPrimer primer) {
		generate(primer);
	}

	public abstract void generate(ChunkPrimer primer);
}
