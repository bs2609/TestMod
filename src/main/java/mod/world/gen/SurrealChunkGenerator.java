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
		int top = templateChunk.getTopFilledSegment() + 16;
		for (int cx = 0; cx < 16; ++cx) {
			for (int cz = 0; cz < 16; ++cz) {
				for (int cy = 0; cy < top; ++cy) {
					IBlockState state = templateChunk.getBlockState(cx, cy, cz);
					if (SurrealBlock.canReplace(state)) {
						primer.setBlockState(cx, 255-cy, cz, SurrealBlock.getStateFor(state));
					}
				}
			}
		}
	}

	private void copyBiomes(Chunk src, Chunk dst) {
		dst.setBiomeArray(src.getBiomeArray());
	}
}
