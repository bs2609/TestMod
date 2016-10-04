package mod.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

public abstract class AbstractWorldEventListener implements IWorldEventListener {
	
	public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {}
	
	public void notifyLightSet(BlockPos pos) {}
	
	public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {}
	
	public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {}
	
	public void playRecord(SoundEvent soundIn, BlockPos pos) {}
	
	public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {}
	
	public void onEntityAdded(Entity entityIn) {}
	
	public void onEntityRemoved(Entity entityIn) {}
	
	public void broadcastSound(int soundID, BlockPos pos, int data) {}
	
	public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {}
	
	public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {}
	
}
