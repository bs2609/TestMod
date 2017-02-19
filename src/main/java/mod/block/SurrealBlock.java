package mod.block;

import mod.model.SurrealBlockModel;
import mod.network.ChunkDataPacket;
import mod.network.ChunkRequestPacket;
import mod.network.ModPacketHandler;
import mod.network.ChunkBuffer;
import mod.util.MiscUtils;
import mod.world.ModDimensions;
import mod.world.SimpleBlockAccess;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@SuppressWarnings("deprecation")
public class SurrealBlock extends BasicBlock {

	public static final String NAME = "surreal_block";
	
	public static final IProperty<Boolean> FULL_CUBE = PropertyBool.create("full");
	public static final IProperty<Boolean> OPAQUE_CUBE = PropertyBool.create("opaque");
	public static final IProperty<MaterialType> MATERIAL_TYPE = MaterialType.PROPERTY;
	
	public static final IUnlistedProperty<IBlockState> APPEARANCE = new UnlistedPropertyBlockAppearance();
	
	public static final int DIM_ID = 0;
	
	private final ChunkBuffer buffer = new ChunkBuffer() {
		
		private final int id = ChunkDataPacket.Handler.register(this);
		
		@Override
		protected void onMissingChunk(int x, int z) {
			// send request packet
			ModPacketHandler.INSTANCE.sendToServer(new ChunkRequestPacket(id, DIM_ID, x, z));
		}
		
		@Override
		protected void onChunkLoad(Chunk chunk) {
			// mark chunk for render update
			int x = chunk.xPosition << 4, z = chunk.zPosition << 4;
			Minecraft.getMinecraft().world.markBlockRangeForRenderUpdate(x, 0, z, x | 15, 255, z | 15);
		}
	};
	
	private final IBlockAccess bufferAccess = new SimpleBlockAccess() {
		
		@Override
		protected Chunk getChunk(int x, int z) {
			return buffer.getChunk(x, z);
		}
		
		@Override
		protected World getWorld() {
			// init() returns World, not WorldClient
			return Minecraft.getMinecraft().world.init();
		}
	};
	
	@SideOnly(Side.CLIENT)
	private class ColourHandler implements IBlockColor {
		
		@Override
		public int colorMultiplier(IBlockState state, IBlockAccess access, BlockPos pos, int tintIndex) {
			if (access == null || pos == null) return -1;
			IBlockAccess source = getBlockAccess(MiscUtils.getSide(access));
			BlockPos inverted = getInverted(pos);
			IBlockState appearance = getBlockAppearance(source, inverted);
			return Minecraft.getMinecraft().getBlockColors().colorMultiplier(appearance, source, inverted, tintIndex);
		}
	}
	
	private class EventHandler {
		
		private boolean checkWorld(World world) {
			return world.isRemote && world.provider.getDimension() == ModDimensions.DIM_SURREAL;
		}
		
		@SubscribeEvent
		public void onChunkLoad(ChunkEvent.Load event) {
			if (!checkWorld(event.getWorld())) return;
			Chunk chunk = event.getChunk();
			buffer.getChunk(chunk.xPosition, chunk.zPosition);
		}
		
		@SubscribeEvent
		public void onChunkUnload(ChunkEvent.Unload event) {
			if (!checkWorld(event.getWorld())) return;
			Chunk chunk = event.getChunk();
			buffer.removeChunk(chunk.xPosition, chunk.zPosition);
		}
	}

	SurrealBlock() {
		super(NAME, Material.ROCK);
		setDefaultState(makeDefaultState());
		MinecraftForge.EVENT_BUS.register(new EventHandler());
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
		IProperty[] listedProperties = new IProperty[] {FULL_CUBE, OPAQUE_CUBE, MATERIAL_TYPE};
		IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[] {APPEARANCE};
		return new ExtendedBlockState(this, listedProperties, unlistedProperties);
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
		IBlockState blockAppearance = getBlockAppearance(access, pos, true);
		return extendedState.withProperty(APPEARANCE, blockAppearance);
	}
	
	private IBlockState getBlockAppearance(IBlockAccess access, BlockPos pos, boolean remap) {
		if (!remap) return getBlockAppearance(access, pos);
		IBlockAccess source = getBlockAccess(MiscUtils.getSide(access));
		BlockPos inverted = getInverted(pos);
		return getBlockAppearance(source, inverted);
	}
	
	private IBlockState getBlockAppearance(IBlockAccess access, BlockPos pos) {
		return access.getBlockState(pos).getActualState(access, pos);
	}
	
	private IBlockAccess getBlockAccess(Side side) {
		switch (side) {
			case CLIENT:
				return bufferAccess;
			case SERVER:
				return MiscUtils.worldServerForDimension(DIM_ID);
			default:
				throw new IllegalArgumentException("Invalid side: " + side);
		}
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
		IBlockAccess source = getBlockAccess(MiscUtils.getSide(access));
		BlockPos inverted = getInverted(pos);
		IBlockState appearance = getBlockAppearance(source, inverted);
		EnumFacing opposite = MiscUtils.getReflected(side, EnumFacing.Axis.Y);
		return appearance.shouldSideBeRendered(source, inverted, opposite);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
		IBlockAccess source = getBlockAccess(MiscUtils.getSide(access));
		BlockPos inverted = getInverted(pos);
		IBlockState appearance = getBlockAppearance(source, inverted);
		return invert(appearance.getBoundingBox(source, inverted));
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
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
		IBlockState appearance = getBlockAppearance(world, pos, true);
		return invert(appearance.getCollisionBoundingBox(world, pos));
	}
	
	private static AxisAlignedBB invert(AxisAlignedBB aabb) {
		return (aabb == null || aabb.minY + aabb.maxY == 1.0) ? aabb : new AxisAlignedBB(aabb.minX, 1.0-aabb.maxY, aabb.minZ, aabb.maxX, 1.0-aabb.minY, aabb.maxZ);
	}
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess access, BlockPos pos) {
		IBlockAccess source = getBlockAccess(MiscUtils.getSide(access));
		BlockPos inverted = getInverted(pos);
		IBlockState appearance = getBlockAppearance(source, inverted);
		return appearance.getLightValue(source, inverted);
	}
	
	@Override
	public int getLightOpacity(IBlockState state, IBlockAccess access, BlockPos pos) {
		IBlockAccess source = getBlockAccess(MiscUtils.getSide(access));
		BlockPos inverted = getInverted(pos);
		IBlockState appearance = getBlockAppearance(source, inverted);
		return appearance.getLightOpacity(source, inverted);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getPackedLightmapCoords(IBlockState state, IBlockAccess access, BlockPos pos) {
		IBlockState appearance = getBlockAppearance(access, pos, true);
		return appearance.getPackedLightmapCoords(access, pos) ^ 0xf0;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public float getAmbientOcclusionLightValue(IBlockState state) {
		return state.isBlockNormalCube() ? 0.8f : 1.0f;
	}
	
	@Override
	public boolean causesSuffocation() {
		return false;
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (heldItem != null && heldItem.getItem() == Items.STICK) {
			IBlockState appearance = getBlockAppearance(world, pos, true);
			player.sendMessage(new TextComponentString(MiscUtils.toString(appearance)));
			return true;
		}
		return false;
	}
	
	public static boolean canReplace(IBlockState state) {
		return state.getRenderType() == EnumBlockRenderType.MODEL;
	}
	
	public static IBlockState getStateFor(IBlockState state) {
		return ModBlocks.surrealBlock.getDefaultState()
				.withProperty(FULL_CUBE, state.isFullCube())
				.withProperty(OPAQUE_CUBE, state.isOpaqueCube())
				.withProperty(MATERIAL_TYPE, MaterialType.getType(state.getMaterial()));
	}
}
