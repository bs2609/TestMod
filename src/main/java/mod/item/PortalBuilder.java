package mod.item;

import mod.block.ModBlocks;
import mod.block.PortalFrameBlock;
import mod.portal.Portal;
import mod.portal.PortalType;
import mod.portal.PortalUtils;
import mod.util.BlockArea;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PortalBuilder extends BasicItem {
	
	public static final String NAME = "portal_builder";
	
	PortalBuilder() {
		super(NAME, CreativeTabs.TOOLS);
	}
	
	@SideOnly(Side.CLIENT)
	void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() != ModBlocks.portalFrame) return EnumActionResult.PASS;
		if (world.isRemote) return EnumActionResult.SUCCESS;
		
		BlockArea area = PortalUtils.isPortalFrame(world, pos);
		if (area == null) {
			player.sendMessage(new TextComponentString("No portal frame found"));
			return EnumActionResult.FAIL;
		}
		
		PortalType type = state.getValue(PortalFrameBlock.TYPE);
		IBlockState frame = Portal.getFrame(type);
		IBlockState border = Portal.getBorder();
		IBlockState interior = Portal.getInterior(type);
		
		if (!PortalUtils.constructPortal(world, area, frame, border, interior)) {
			player.sendMessage(new TextComponentString("Couldn't construct portal"));
			return EnumActionResult.FAIL;
		}
		return EnumActionResult.SUCCESS;
	}
}
