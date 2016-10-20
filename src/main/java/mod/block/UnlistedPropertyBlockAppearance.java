package mod.block;

import mod.util.MiscUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyBlockAppearance implements IUnlistedProperty<IBlockState> {

	@Override
	public String getName() {
		return "appearance";
	}

	@Override
	public boolean isValid(IBlockState value) {
		return value != null;
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
