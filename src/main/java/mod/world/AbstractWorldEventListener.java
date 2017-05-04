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
	
	public void notifyBlockUpdate(World world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {}
	
	public void notifyLightSet(BlockPos pos) {}
	
	public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {}
	
	public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent sound, SoundCategory category, double x, double y, double z, float volume, float pitch) {}
	
	public void playRecord(SoundEvent sound, BlockPos pos) {}
	
	public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {}
	
	public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {}
	
	public void onEntityAdded(Entity entity) {}
	
	public void onEntityRemoved(Entity entity) {}
	
	public void broadcastSound(int soundID, BlockPos pos, int data) {}
	
	public void playEvent(EntityPlayer player, int type, BlockPos pos, int data) {}
	
	public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {}
	
}
