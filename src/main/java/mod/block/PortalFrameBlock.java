package mod.block;

import mod.portal.PortalType;
import mod.portal.PortalUtils;
import mod.util.BlockArea;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation")
public class PortalFrameBlock extends VariantBlock<PortalType> {

	public static final String NAME = "portal_frame";
	public static final IProperty<PortalType> TYPE = PortalType.PROPERTY;
	public static final int RANGE = 16;

	PortalFrameBlock() {
		super(NAME, Material.ROCK, CreativeTabs.BUILDING_BLOCKS);
		setHardness(1.5f);
		setResistance(5.0f);
	}

	@SideOnly(Side.CLIENT)
	void initModel() {
		Item item = Item.getItemFromBlock(this);
		for (PortalType type : PortalType.VALUES) {
			ModelResourceLocation location = new ModelResourceLocation(getRegistryName(), TYPE.getName() + "=" + type.getName());
			ModelLoader.setCustomModelResourceLocation(item, getMetaForType(type), location);
		}
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (PortalType type : PortalType.VALUES) {
			list.add(new ItemStack(this, 1, getMetaForType(type)));
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (!world.isRemote) {
			PortalUtils.framePlaced(world, pos, state, RANGE);
		}
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (!world.isRemote) {
			PortalUtils.frameRemoved(world, pos);
		}
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player.getHeldItem(hand).getItem() == Items.STICK) {
			if (!world.isRemote) {
				BlockArea area = PortalUtils.isPortalFrame(world, pos);
				player.sendMessage(new TextComponentString("Portal area " + (area != null ? area : "not found")));
			}
			return true;
		}
		return false;
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TYPE, PortalType.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE).ordinal();
	}

	@Override
	public int getMetaForType(PortalType type) {
		return getMetaFromState(getDefaultState().withProperty(TYPE, type));
	}

	@Override
	public PortalType getTypeForMeta(int meta) {
		return getStateFromMeta(meta).getValue(TYPE);
	}
}
