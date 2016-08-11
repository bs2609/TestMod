package mod.world.gen;

import net.minecraft.world.chunk.ChunkPrimer;

public interface ITerrainGenerator {

	void generate(int x, int z, ChunkPrimer primer);
}
