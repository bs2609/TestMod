package mod.portal;

import mod.block.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class Portal {

	public static IBlockState getFrame(PortalType type) {
		return ModBlocks.portalFrameBlock.getDefaultState().withProperty(PortalType.PROPERTY, type);
	}

	public static IBlockState getBorder() {
		return Blocks.QUARTZ_BLOCK.getDefaultState();
	}

	public static IBlockState getInterior() {
		return Blocks.GLASS_PANE.getDefaultState();
	}

	public static IBlockState getInterior(PortalType type) {
		return ModBlocks.portalInteriorBlock.getDefaultState().withProperty(PortalType.PROPERTY, type);
	}
}
