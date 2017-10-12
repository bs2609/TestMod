package mod.world.gen;

import mod.block.SurrealBlock;
import mod.util.MiscUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class SurrealChunkGenerator extends AbstractChunkGenerator {
	
	private final PortalFieldGenerator portalGen = new PortalFieldGenerator();

	public SurrealChunkGenerator(World world) {
		super(world);
	}

	@Override
	public Chunk generateChunk(int x, int z) {
		Chunk template = loadTemplate(getWorld(), x, z);
		ChunkPrimer primer = new ChunkPrimer();
		pregenChunk(primer, template);
		portalGen.generate(primer);
		Chunk chunk = new Chunk(world, x, z);
		fillChunk(chunk, primer);
		chunk.setBiomeArray(template.getBiomeArray());
		chunk.generateSkylightMap();
		return chunk;
	}
	
	private WorldServer getWorld() {
		return MiscUtils.getWorld(SurrealBlock.DIM_ID);
	}
	
	private Chunk loadTemplate(WorldServer world, int x, int z) {
		Chunk template = null;
		for (int dx = -1; dx <= 1; ++dx) {
			for (int dz = -1; dz <= 1; ++dz) {
				Chunk chunk = world.getChunkFromChunkCoords(x+dx, z+dz);
				if (dx == 0 && dz == 0) template = chunk;
				queueUnload(world, chunk);
			}
		}
		return template;
	}
	
	private void queueUnload(WorldServer world, Chunk chunk) {
		if (!world.getPlayerChunkMap().contains(chunk.x, chunk.z)) {
			world.getChunkProvider().queueUnload(chunk);
		}
	}
	
	private void pregenChunk(ChunkPrimer primer, Chunk template) {
		
		int top = template.getTopFilledSegment() + 16;
		
		for (int cx = 0; cx < 16; ++cx) {
			for (int cz = 0; cz < 16; ++cz) {
				for (int cy = 0; cy < top; ++cy) {
					IBlockState state = template.getBlockState(cx, cy, cz);
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
