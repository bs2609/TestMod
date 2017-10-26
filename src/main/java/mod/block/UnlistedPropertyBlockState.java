package mod.block;

import mod.util.MiscUtils;
import net.minecraft.block.state.IBlockState;

public class UnlistedPropertyBlockState extends AbstractUnlistedProperty<IBlockState> {

	public UnlistedPropertyBlockState(String name) {
		super(name);
	}

	@Override
	public Class<IBlockState> getType() {
		return IBlockState.class;
	}

	@Override
	public String valueToString(IBlockState value) {
		return MiscUtils.toString(value);
	}
}
