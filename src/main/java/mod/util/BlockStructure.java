package mod.util;

import net.minecraft.block.state.IBlockState;

import java.util.Arrays;

public class BlockStructure {
	
	private final IBlockState[] states;
	
	public BlockStructure(IBlockState... states) {
		this.states = Arrays.copyOf(states, 4);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BlockStructure that = (BlockStructure) o;
		return Arrays.equals(states, that.states);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(states);
	}
	
	public IBlockState getStateFor(BlockArea area) {
		return states[area.getDims()];
	}
}
