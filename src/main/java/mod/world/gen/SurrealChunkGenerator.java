package mod.world.gen;

import mod.block.ModBlocks;
import mod.util.MiscUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;

public class SurrealChunkGenerator extends AbstractChunkGenerator {

	private final World template;

	private final PortalFieldGenerator portalGen = new PortalFieldGenerator();
	private final IBlockState blockState = ModBlocks.surrealBlock.getDefaultState();

	private Chunk templateChunk;

	public SurrealChunkGenerator(World world) {
		super(world);
		template = MiscUtils.worldServerForDimension(0);
	}

	@Override
	public Chunk provideChunk(int x, int z) {
		templateChunk = template.getChunkFromChunkCoords(x, z);
		ChunkPrimer primer = new ChunkPrimer();
		pregenChunk(primer);
		portalGen.generate(primer);
		Chunk chunk = new Chunk(world, primer, x, z);
		copyBiomes(templateChunk, chunk);
		chunk.generateSkylightMap();
		return chunk;
	}

	private void pregenChunk(ChunkPrimer primer) {
		for (int cx = 0; cx < 16; ++cx) {
			for (int cz = 0; cz < 16; ++cz) {
				for (int cy = 0; cy < 256; ++cy) {
					if (testBlock(cx, cy, cz)) {
						primer.setBlockState(cx, 255-cy, cz, blockState);
					}
				}
			}
		}
	}

	private boolean testBlock(int x, int y, int z) {
		return templateChunk.getBlockState(x, y, z).getRenderType() == EnumBlockRenderType.MODEL;
	}

	private void copyBiomes(Chunk src, Chunk dst) {
		dst.setBiomeArray(src.getBiomeArray());
	}
}
