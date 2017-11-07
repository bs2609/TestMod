package mod.block;

import mod.model.SurrealBlockModel;
import mod.util.BlockAccessRemapper;
import mod.util.MiscUtils;
import mod.util.WorldViewer;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@SuppressWarnings("deprecation")
public class SurrealBlock extends BasicBlock {

	public static final String NAME = "surreal_block";
	
	public static final IProperty<Boolean> FULL_CUBE = PropertyBool.create("full");
	public static final IProperty<Boolean> OPAQUE_CUBE = PropertyBool.create("opaque");
	public static final IProperty<MaterialType> MATERIAL_TYPE = MaterialType.PROPERTY;
	
	public static final IUnlistedProperty<IBlockState> APPEARANCE = UnlistedProperties.BlockState.create("appearance");
	
	private static final IBlockState fallback = new Block(MaterialType.SOLID.getExample()).getDefaultState();
	private static final IBlockState air = Blocks.AIR.getDefaultState();
	
	@SideOnly(Side.CLIENT)
	private final class ColourHandler implements IBlockColor {
		
		@Override
		public int colorMultiplier(IBlockState state, IBlockAccess access, BlockPos pos, int tintIndex) {
			if (access == null || pos == null) return -1;
			IBlockAccess remapper = new Remapper(access);
			BlockPos inverted = getInverted(pos);
			IBlockState appearance = getBlockAppearance(state, remapper, inverted);
			return Minecraft.getMinecraft().getBlockColors().colorMultiplier(appearance, remapper, inverted, tintIndex);
		}
	}
	
	private final class Remapper extends BlockAccessRemapper {
		
		Remapper(IBlockAccess source) {
			super(WorldViewer.instance.getBlockAccess(source), source);
		}
		
		@Override
		protected boolean shouldRemap(BlockPos pos) {
			return other.getBlockState(remap(pos)).getBlock() != SurrealBlock.this;
		}
		
		@Override
		protected BlockPos remap(BlockPos pos) {
			return getInverted(pos);
		}
		
		@Override
		protected EnumFacing remap(EnumFacing facing) {
			return MiscUtils.getReflected(facing, EnumFacing.Axis.Y);
		}
	}

	SurrealBlock() {
		super(NAME, Material.ROCK);
		setDefaultState(makeDefaultState());
	}

	@SideOnly(Side.CLIENT)
	void initModel() {
		// register state mapper
		StateMapperBase ignoreState = new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return SurrealBlockModel.modelResourceLocation;
			}
		};
		ModelLoader.setCustomStateMapper(this, ignoreState);
		// register item model
		Item item = Item.getItemFromBlock(this);
		ModelResourceLocation itemMRL = new ModelResourceLocation(getRegistryName(), "inventory");
		ModelLoader.setCustomModelResourceLocation(item, 0, itemMRL);
	}
	
	@SideOnly(Side.CLIENT)
	void registerColourHandler() {
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new ColourHandler(), this);
	}
	
	private IBlockState makeDefaultState() {
		return blockState.getBaseState()
				.withProperty(FULL_CUBE, true)
				.withProperty(OPAQUE_CUBE, true)
				.withProperty(MATERIAL_TYPE, MaterialType.SOLID);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer.Builder(this)
				.add(FULL_CUBE, OPAQUE_CUBE, MATERIAL_TYPE)
				.add(APPEARANCE)
				.build();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(FULL_CUBE, (meta & 8) != 0)
				.withProperty(OPAQUE_CUBE, (meta & 4) != 0)
				.withProperty(MATERIAL_TYPE, MaterialType.values()[meta & 3]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(MATERIAL_TYPE).ordinal()
				| (state.getValue(OPAQUE_CUBE) ? 4 : 0)
				| (state.getValue(FULL_CUBE) ? 8 : 0);
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess access, BlockPos pos) {
		IExtendedBlockState extendedState = (IExtendedBlockState) state;
		if (extendedState.getValue(APPEARANCE) != null) return extendedState;
		IBlockAccess remapper = new Remapper(access);
		BlockPos inverted = getInverted(pos);
		IBlockState appearance = getBlockAppearance(remapper, inverted);
		return extendedState.withProperty(APPEARANCE, appearance.getBlock().getExtendedState(appearance.getActualState(remapper, inverted), remapper, inverted));
	}
	
	private IBlockState getBlockAppearance(IBlockState state, IBlockAccess access, BlockPos pos) {
		IExtendedBlockState extendedState = (IExtendedBlockState) state;
		IBlockState appearance = extendedState.getValue(APPEARANCE);
		return appearance != null ? appearance : getBlockAppearance(access, pos);
	}
	
	private IBlockState getBlockAppearance(IBlockAccess access, BlockPos pos, boolean remap) {
		if (!remap) return getBlockAppearance(access, pos);
		return getBlockAppearance(new Remapper(access), getInverted(pos));
	}
	
	private IBlockState getBlockAppearance(IBlockAccess access, BlockPos pos) {
		IBlockState state = access.getBlockState(pos);
		return state.getBlock() == this ? fallback : state;
	}
	
	private static BlockPos getInverted(BlockPos pos) {
		return new BlockPos(pos.getX(), 255-pos.getY(), pos.getZ());
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side) {
		IBlockAccess remapper = new Remapper(access);
		BlockPos inverted = getInverted(pos);
		IBlockState appearance = getBlockAppearance(remapper, inverted);
		EnumFacing opposite = MiscUtils.getReflected(side, EnumFacing.Axis.Y);
		return appearance.shouldSideBeRendered(remapper, inverted, opposite);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
		IBlockAccess remapper = new Remapper(access);
		BlockPos inverted = getInverted(pos);
		IBlockState appearance = getBlockAppearance(remapper, inverted);
		return invert(appearance.getBoundingBox(remapper, inverted));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		IBlockState appearance = getBlockAppearance(world, pos, true);
		AxisAlignedBB aabb = appearance.getSelectedBoundingBox(world, pos);
		double x = pos.getX(), y = pos.getY(), z = pos.getZ();
		return invert(aabb.offset(-x, -y, -z)).offset(x, y, z);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
		IBlockAccess remapper = new Remapper(access);
		BlockPos inverted = getInverted(pos);
		IBlockState appearance = getBlockAppearance(remapper, inverted);
		return invert(appearance.getCollisionBoundingBox(remapper, inverted));
	}
	
	private static AxisAlignedBB invert(AxisAlignedBB aabb) {
		return (aabb == null || aabb.minY + aabb.maxY == 1.0) ? aabb : new AxisAlignedBB(aabb.minX, 1.0-aabb.maxY, aabb.minZ, aabb.maxX, 1.0-aabb.minY, aabb.maxZ);
	}
	
	@Override
	public Vec3d getOffset(IBlockState state, IBlockAccess access, BlockPos pos) {
		IBlockAccess remapper = new Remapper(access);
		BlockPos inverted = getInverted(pos);
		IBlockState appearance = getBlockAppearance(remapper, inverted);
		return invert(appearance.getOffset(remapper, inverted));
	}
	
	private static Vec3d invert(Vec3d vec) {
		return (vec.y == 0.0) ? vec : new Vec3d(vec.x, -vec.y, vec.z);
	}
	
	@Override
	public boolean isPassable(IBlockAccess access, BlockPos pos) {
		IBlockAccess remapper = new Remapper(access);
		BlockPos inverted = getInverted(pos);
		IBlockState appearance = getBlockAppearance(remapper, inverted);
		return appearance.getBlock().isPassable(remapper, inverted);
	}
	
	@Override
	public boolean isLadder(IBlockState state, IBlockAccess access, BlockPos pos, EntityLivingBase entity) {
		IBlockAccess remapper = new Remapper(access);
		BlockPos inverted = getInverted(pos);
		IBlockState appearance = getBlockAppearance(remapper, inverted);
		return appearance.getBlock().isLadder(appearance, remapper, inverted, entity);
	}
	
	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity) {
		IBlockState appearance = getBlockAppearance(world, pos, true);
		return appearance.getBlock().getSoundType();
	}
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess access, BlockPos pos) {
		IBlockAccess remapper = new Remapper(access);
		BlockPos inverted = getInverted(pos);
		IBlockState appearance = getBlockAppearance(remapper, inverted);
		return appearance.getLightValue(remapper, inverted);
	}
	
	@Override
	public int getLightOpacity(IBlockState state, IBlockAccess access, BlockPos pos) {
		IBlockState appearance = getBlockAppearance(access, pos, true);
		return (appearance == air ? state : appearance).getLightOpacity();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getPackedLightmapCoords(IBlockState state, IBlockAccess access, BlockPos pos) {
		IBlockAccess remapper = new Remapper(access);
		BlockPos inverted = getInverted(pos);
		IBlockState appearance = getBlockAppearance(remapper, inverted);
		return appearance.getPackedLightmapCoords(remapper, inverted);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public float getAmbientOcclusionLightValue(IBlockState state) {
		return state.isBlockNormalCube() ? 0.2f : 1.0f;
	}
	
	@Override
	public boolean causesSuffocation(IBlockState state) {
		return state.getMaterial().blocksMovement() && state.isFullCube();
	}
	
	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess access, BlockPos pos) {
		IBlockState appearance = getBlockAppearance(access, pos, true);
		return appearance.isNormalCube();
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return state.getValue(FULL_CUBE);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return state.getValue(OPAQUE_CUBE);
	}
	
	@Override
	public Material getMaterial(IBlockState state) {
		return state.getValue(MATERIAL_TYPE).getExample();
	}
	
	@Override
	public boolean isFullBlock(IBlockState state) {
		return state.isOpaqueCube();
	}
	
	@Override
	public int getLightOpacity(IBlockState state) {
		return state.isFullBlock() ? 255 : 0;
	}
	
	@Override
	public boolean isTranslucent(IBlockState state) {
		return !state.getMaterial().blocksLight();
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 0;
	}
	
	@Override
	public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		Item item = player.getHeldItem(hand).getItem();
		
		if (item == Items.STICK) {
			IBlockAccess remapper = new Remapper(world);
			BlockPos inverted = getInverted(pos);
			IBlockState appearance = getBlockAppearance(remapper, inverted).getActualState(remapper, inverted);
			player.sendMessage(new TextComponentString(MiscUtils.toString(appearance)));
			return true;
		}
		
		return false;
	}
	
	public static final class StateMapper {
		
		private static final IBlockState[] states = new IBlockState[16];
		private static final IBlockState unmapped = ModBlocks.surrealVoidBlock.getDefaultState();
		
		static {
			for (int i = 0; i < 16; ++i) {
				states[i] = ModBlocks.surrealBlock.getStateFromMeta(i);
			}
		}
		
		public static IBlockState getStateFor(IBlockState state) {
			return isValid(state)
					? states[getStateID(state.isFullCube(), state.isOpaqueCube(), state.getMaterial())]
					: unmapped;
		}
		
		private static boolean isValid(IBlockState state) {
			return state.getRenderType() == EnumBlockRenderType.MODEL;
		}
		
		private static int getStateID(boolean full, boolean opaque, Material material) {
			return (full ? 8 : 0) | (opaque ? 4 : 0) | MaterialType.getType(material).ordinal();
		}
	}
}
