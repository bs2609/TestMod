package mod.world;

import mod.block.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class PortalBlockTeleporter extends SimpleTeleporter {
	
	private final IBlockState state = ModBlocks.portalBlock.getDefaultState();
	
	public PortalBlockTeleporter(WorldServer world) {
		super(world);
	}
	
	@Override
	public BlockPos getDestinationForEntity(Entity entity) {
		BlockPos pos = worldAccess.getTopSolidOrLiquidBlock(new BlockPos(entity));
		worldAccess.setBlockState(pos, state);
		return pos;
	}
}
