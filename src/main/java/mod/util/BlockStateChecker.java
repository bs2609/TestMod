package mod.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockStateChecker {
	
	public abstract boolean check(World world, BlockPos pos, IBlockState state);
	
	public static BlockStateChecker excludingArea(final BlockArea area) {
		return new BlockStateChecker() {
			@Override
			public boolean check(World world, BlockPos pos, IBlockState state) {
				return !area.contains(pos);
			}
		};
	}
	
	public BlockStateChecker and(final BlockStateChecker other) {
		return new BlockStateChecker() {
			@Override
			public boolean check(World world, BlockPos pos, IBlockState state) {
				return BlockStateChecker.this.check(world, pos, state) && other.check(world, pos, state);
			}
		};
	}
	
	public BlockStateChecker or(final BlockStateChecker other) {
		return new BlockStateChecker() {
			@Override
			public boolean check(World world, BlockPos pos, IBlockState state) {
				return BlockStateChecker.this.check(world, pos, state) || other.check(world, pos, state);
			}
		};
	}
}
