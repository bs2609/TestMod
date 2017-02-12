package mod.world;

import mod.block.SurrealBlock;
import mod.util.MiscUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SurrealWorldUpdater extends AbstractWorldEventListener {
	
	private static final IBlockState air = Blocks.AIR.getDefaultState();
	
	@Override
	public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
		if (oldState == newState) return;
		WorldServer world = MiscUtils.worldServerForDimension(ModDimensions.DIM_SURREAL);
		Chunk chunk = world.getChunkProvider().loadChunk(pos.getX() >> 4, pos.getZ() >> 4);
		if (chunk != null) {
			BlockPos inverted = new BlockPos(pos.getX(), 255-pos.getY(), pos.getZ());
			chunk.setBlockState(inverted, SurrealBlock.canReplace(newState) ? SurrealBlock.getStateFor(newState) : air);
		}
	}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		World world = event.getWorld();
		if (world.isRemote) return;
		int dimension = world.provider.getDimension();
		if (dimension == SurrealBlock.DIM_ID) {
			world.addEventListener(this);
		}
	}
	
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		World world = event.getWorld();
		if (world.isRemote) return;
		int dimension = world.provider.getDimension();
		if (dimension == SurrealBlock.DIM_ID) {
			world.removeEventListener(this);
		}
	}
}
