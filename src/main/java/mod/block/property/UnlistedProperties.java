package mod.block.property;

import net.minecraft.block.state.IBlockState;

public class UnlistedProperties {
	
	public static class BlockState extends SimpleUnlistedProperty<IBlockState> {
		
		private BlockState(String name) {
			super(name, IBlockState.class);
		}
		
		public static BlockState create(String name) {
			return new BlockState(name);
		}
	}
}
