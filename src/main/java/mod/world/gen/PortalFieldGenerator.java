package mod.world.gen;

import mod.block.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.ChunkPrimer;

public class PortalFieldGenerator extends BasicTerrainGenerator {

	private final IBlockState state = ModBlocks.portalField.getDefaultState();

	@Override
	public void generate(ChunkPrimer genTarget) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				genTarget.setBlockState(x, 0, z, state);
			}
		}
	}
}
