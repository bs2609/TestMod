package mod.world;

import mod.block.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class PortalBlockTeleporter extends SimpleTeleporter {
	
	private final IBlockState state = ModBlocks.PORTAL_BLOCK.getDefaultState();
	
	public PortalBlockTeleporter(WorldServer world) {
		super(world);
	}
	
	@Override
	public BlockPos getDestination(Entity entity) {
		BlockPos entityPos = new BlockPos(entity);
		BlockPos surfacePos = world.getTopSolidOrLiquidBlock(entityPos);
		BlockPos destPos = surfacePos.getY() > entityPos.getY() ? surfacePos : entityPos;
		world.setBlockState(destPos.down(), state);
		return destPos;
	}
}
