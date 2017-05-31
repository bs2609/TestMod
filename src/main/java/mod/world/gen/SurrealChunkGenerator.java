package mod.world.gen;

import mod.block.SurrealBlock;
import mod.util.MiscUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

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
		Chunk chunk = new Chunk(world, x, z);
		fillChunk(chunk, primer);
		chunk.setBiomeArray(templateChunk.getBiomeArray());
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
					if (SurrealBlock.StateMapper.isValid(state)) {
						primer.setBlockState(cx, 255-cy, cz, SurrealBlock.StateMapper.getStateFor(state));
					}
				}
			}
		}
	}
	
	private void fillChunk(Chunk chunk, ChunkPrimer primer) {
		
		ExtendedBlockStorage[] data = chunk.getBlockStorageArray();
		boolean skylight = world.provider.hasSkyLight();
		
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				for (int y = 0; y < 256; ++y) {
					IBlockState state = primer.getBlockState(x, y, z);
					if (state.getBlock() == Blocks.AIR) continue;
					int idx = y >> 4;
					if (data[idx] == Chunk.NULL_BLOCK_STORAGE) {
						data[idx] = new ExtendedBlockStorage(idx << 4, skylight);
					}
					data[idx].getData().set(x, y & 15, z, state);
				}
			}
		}
		
		for (ExtendedBlockStorage storage : data) {
			if (storage != Chunk.NULL_BLOCK_STORAGE) {
				storage.recalculateRefCounts();
			}
		}
	}
}
