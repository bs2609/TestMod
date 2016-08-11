package mod.block;

import mod.util.MiscUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class SurrealBlock extends BasicBlock {

	public static final String NAME = "surrealBlock";
	public static final UnlistedPropertyBlockAppearance APPEARANCE = new UnlistedPropertyBlockAppearance();

	SurrealBlock() {
		super(NAME, Material.ROCK);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		StateMapperBase ignoreState = new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return SurrealBlockModel.modelResourceLocation;
			}
		};
		ModelLoader.setCustomStateMapper(this, ignoreState);
	}

	@SideOnly(Side.CLIENT)
	public void initItemModel() {
		Item item = Item.REGISTRY.getObject(getRegistryName());
		ModelResourceLocation itemMRL = new ModelResourceLocation(getRegistryName(), "inventory");
		final int DEFAULT_ITEM_SUBTYPE = 0;
		ModelLoader.setCustomModelResourceLocation(item, DEFAULT_ITEM_SUBTYPE, itemMRL);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		IProperty[] listedProperties = new IProperty[0];
		IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[] {APPEARANCE};
		return new ExtendedBlockState(this, listedProperties, unlistedProperties);
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		IExtendedBlockState extendedState = (IExtendedBlockState) state;
		IBlockState blockAppearance = getBlockAppearance(world, pos);
		return extendedState.withProperty(APPEARANCE, blockAppearance);
	}

	private IBlockState getBlockAppearance(IBlockAccess world, BlockPos pos) {
		return MiscUtils.worldServerForDimension(0).getBlockState(invertPos(pos));
	}

	private BlockPos invertPos(BlockPos orig) {
		return new BlockPos(orig.getX(), 255-orig.getY(), orig.getZ());
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return true;
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}
}