package mod.world;

import mod.block.SurrealBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SurrealWorldUpdater extends AbstractWorldEventListener {
	
	private static final IBlockState air = Blocks.AIR.getDefaultState();
	
	private final WorldUpdateAggregator updater = new WorldUpdateAggregator(ModDimensions.DIM_SURREAL);
	
	@Override
	public void notifyBlockUpdate(World world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
		if (oldState == newState) return;
		BlockPos inverted = new BlockPos(pos.getX(), 255-pos.getY(), pos.getZ());
		updater.queueUpdate(inverted, SurrealBlock.StateMapper.isValid(newState) ? SurrealBlock.StateMapper.getStateFor(newState) : air);
	}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		World world = event.getWorld();
		if (world.isRemote) return;
		int dimension = world.provider.getDimension();
		if (dimension == SurrealBlock.DIM_ID) {
			world.addEventListener(this);
		} else if (dimension == ModDimensions.DIM_SURREAL) {
			updater.applyUpdates();
		}
	}
	
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		World world = event.getWorld();
		if (world.isRemote) return;
		int dimension = world.provider.getDimension();
		if (dimension == SurrealBlock.DIM_ID) {
			world.removeEventListener(this);
			updater.applyUpdates();
		}
	}
}
