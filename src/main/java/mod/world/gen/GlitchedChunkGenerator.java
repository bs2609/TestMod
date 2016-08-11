package mod.world.gen;

import mod.TestMod;
import mod.util.MiscUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkProviderOverworld;
import net.minecraft.world.storage.WorldInfo;

import java.util.Random;

public class GlitchedChunkGenerator extends AbstractChunkGenerator {

	private final IChunkGenerator source;
	
	private final OreDecorator decorator = new OreDecorator();

	public GlitchedChunkGenerator(World worldIn) {
		super(worldIn);
		WorldInfo info = world.getWorldInfo();
		source = new ChunkProviderOverworld(world, world.getSeed(), info.isMapFeaturesEnabled(), info.getGeneratorOptions());
	}

	@Override
	public Chunk provideChunk(int x, int z) {
		long seed = world.getSeed();
		long xz = MiscUtils.getChunkSeed(x, z);
		Random random = new Random(seed ^ xz);

		if (random.nextInt(48) == 0) {
			TestMod.getLogger().info("changing chunk (" + x + ", " + z + ")");
			int off = random.nextBoolean() ? random.nextInt(32) : 1;
			Chunk in = random.nextBoolean() ? source.provideChunk(x^off, z) : source.provideChunk(x, z^off);
			Chunk out = new Chunk(world, x, z);
			cloneChunk(in, out);
			return out;
		}

		return source.provideChunk(x, z);
	}
	
	private void cloneChunk(Chunk in, Chunk out) {
		ExtendedBlockStorage[] dataFrom = in.getBlockStorageArray(), dataTo = out.getBlockStorageArray();
		System.arraycopy(dataFrom, 0, dataTo, 0, 16);
		out.setBiomeArray(in.getBiomeArray());
		out.generateSkylightMap();
	}
	
	@Override
	public void populate(int x, int z) {
		BlockPos pos = new BlockPos(x << 4, 0, z << 4);
		decorator.decorate(world, getChunkRandom(x, z), null, pos);
	}
	
	private Random getChunkRandom(int x, int z) {
		long seed = world.getSeed();
		Random src = new Random(seed);
		long rx = src.nextLong() | 1L, rz = src.nextLong() | 1L;
		seed ^= x*rx + z*rz;
		return new Random(seed);
	}
}
