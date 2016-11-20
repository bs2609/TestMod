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
import net.minecraft.item.ItemStack;
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
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState state = worldIn.getBlockState(pos);
		if (state.getBlock() != ModBlocks.portalFrameBlock) return EnumActionResult.PASS;
		if (worldIn.isRemote) return EnumActionResult.SUCCESS;
		
		BlockArea area = PortalUtils.isPortalFrame(worldIn, pos);
		if (area == null) {
			playerIn.addChatMessage(new TextComponentString("No portal frame found"));
			return EnumActionResult.FAIL;
		}
		
		PortalType type = state.getValue(PortalFrameBlock.TYPE);
		IBlockState frame = Portal.getFrame(type);
		IBlockState border = Portal.getBorder();
		IBlockState interior = Portal.getInterior(type);
		
		if (!PortalUtils.constructPortal(worldIn, area, frame, border, interior)) {
			playerIn.addChatMessage(new TextComponentString("Couldn't construct portal"));
			return EnumActionResult.FAIL;
		}
		return EnumActionResult.SUCCESS;
	}
}
