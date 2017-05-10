package mod.network;

import mod.TestMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PartialChunk extends EmptyChunk {
	
	public PartialChunk(World world, int x, int z) {
		super(world, x, z);
	}
	
	@Override
	public IBlockState getBlockState(BlockPos pos) {
		return getBlockState(pos.getX(), pos.getY(), pos.getZ());
	}
	
	@Override
	public IBlockState getBlockState(int x, int y, int z) {
		int idx = y >> 4;
		ExtendedBlockStorage[] storageArray = getBlockStorageArray();
		if (idx >= 0 && idx < storageArray.length) {
			ExtendedBlockStorage storage = storageArray[idx];
			if (storage != NULL_BLOCK_STORAGE) {
				return storage.get(x & 15, y & 15, z & 15);
			}
		}
		return Blocks.AIR.getDefaultState();
	}
}
