package mod.world.gen;

import mod.block.SurrealBlock;
import mod.util.MiscUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;

public class SurrealChunkGenerator extends AbstractChunkGenerator {
	
	private final PortalFieldGenerator portalGen = new PortalFieldGenerator();
	
	private Chunk templateChunk;

	public SurrealChunkGenerator(World world) {
		super(world);
	}

	@Override
	public Chunk provideChunk(int x, int z) {
		templateChunk = getTemplate().getChunkFromChunkCoords(x, z);
		ChunkPrimer primer = new ChunkPrimer();
		pregenChunk(primer);
		portalGen.generate(primer);
		Chunk chunk = new Chunk(world, primer, x, z);
		copyBiomes(templateChunk, chunk);
		templateChunk = null;
		chunk.generateSkylightMap();
		return chunk;
	}
	
	private World getTemplate() {
		return MiscUtils.worldServerForDimension(SurrealBlock.DIM_ID);
	}

	private void pregenChunk(ChunkPrimer primer) {
		for (int cx = 0; cx < 16; ++cx) {
			for (int cz = 0; cz < 16; ++cz) {
				for (int cy = 0; cy < 256; ++cy) {
					if (testBlock(cx, cy, cz)) {
						primer.setBlockState(cx, 255-cy, cz, getBlockState(cx, cy, cz));
					}
				}
			}
		}
	}

	private boolean testBlock(int x, int y, int z) {
		return SurrealBlock.canReplace(templateChunk.getBlockState(x, y, z));
	}
	
	private IBlockState getBlockState(int x, int y, int z) {
		return SurrealBlock.getStateFor(templateChunk.getBlockState(x, y, z));
	}

	private void copyBiomes(Chunk src, Chunk dst) {
		dst.setBiomeArray(src.getBiomeArray());
	}
}
