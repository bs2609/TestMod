package mod.world.gen;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.List;

public class TestChunkGenerator extends AbstractChunkGenerator {

	private final List<ITerrainGenerator> generators;
	private final PortalFieldGenerator portalGen = new PortalFieldGenerator();

	public TestChunkGenerator(World worldIn, List<ITerrainGenerator> generatorList) {
		super(worldIn);
		generators = generatorList;
	}

	@Override
	public Chunk provideChunk(int x, int z) {
		ChunkPrimer primer = new ChunkPrimer();
		for (ITerrainGenerator generator : generators) {
			generator.generate(x, z, primer);
		}
		portalGen.generate(primer);
		Chunk chunk = new Chunk(world, primer, x, z);
		initBiomes(chunk, x, z);
		chunk.generateSkylightMap();
		return chunk;
	}

	private void initBiomes(Chunk chunk, int x, int z) {
		Biome[] biomes = world.getBiomeProvider().loadBlockGeneratorData(null, x << 4, z << 4, 16, 16);
		byte[] bytes = chunk.getBiomeArray();
		int n = Math.min(biomes.length, bytes.length);
		for (int i = 0; i < n; ++i) {
			bytes[i] = (byte) Biome.getIdForBiome(biomes[i]);
		}
	}
}
