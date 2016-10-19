package mod.block;

import mod.portal.PortalType;
import mod.portal.PortalUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SuppressWarnings("deprecation")
public class PortalFrameBlock extends VariantBlock<PortalType> {

	public static final String NAME = "portal_frame";
	public static final IProperty<PortalType> TYPE = PortalType.PROPERTY;
	public static final int RANGE = 16;

	PortalFrameBlock() {
		super(NAME, Material.ROCK, CreativeTabs.BUILDING_BLOCKS);
	}

	@SideOnly(Side.CLIENT)
	void initModel() {
		Item item = Item.getItemFromBlock(this);
		for (PortalType type : PortalType.values()) {
			ModelResourceLocation location = new ModelResourceLocation(getRegistryName(), "type="+type.getName());
			ModelLoader.setCustomModelResourceLocation(item, getMetaForType(type), location);
		}
	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		for (PortalType type : PortalType.values()) {
			list.add(new ItemStack(itemIn, 1, getMetaForType(type)));
		}
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (!worldIn.isRemote) {
			PortalUtils.framePlaced(worldIn, pos, state, RANGE);
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
