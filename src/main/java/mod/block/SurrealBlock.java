package mod.block;

import mod.network.ChunkDataPacket;
import mod.network.ChunkRequestPacket;
import mod.network.ModPacketHandler;
import mod.network.ChunkBuffer;
import mod.util.MiscUtils;
import mod.world.ModDimensions;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
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
			Minecraft.getMinecraft().theWorld.markBlockRangeForRenderUpdate(x, 0, z, x + 15, 255, z + 15);
		}
	};
	
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
		MinecraftForge.EVENT_BUS.register(new EventHandler());
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
		ModelLoader.setCustomModelResourceLocation(item, 0, itemMRL);
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
		Chunk chunk = getChunk(pos, MiscUtils.getSide(world));
		return (chunk != null) ? chunk.getBlockState(pos.getX(), 255-pos.getY(), pos.getZ()) : null;
	}
	
	private IBlockState getBlockAppearance(IBlockState state) {
		if (state instanceof IExtendedBlockState) {
			IExtendedBlockState extendedState = (IExtendedBlockState) state;
			return extendedState.getValue(APPEARANCE);
		}
		return null;
	}
	
	private Chunk getChunk(BlockPos pos, Side side) {
		switch (side) {
			case CLIENT:
				return buffer.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
			case SERVER:
				return MiscUtils.worldServerForDimension(DIM_ID).getChunkFromBlockCoords(pos);
			default:
				return null;
		}
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side) {
		IBlockState appearance = getBlockAppearance(state);
		return (appearance != null) ? appearance.shouldSideBeRendered(access, pos, side) : super.shouldSideBeRendered(state, access, pos, side);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		IBlockState appearance = getBlockAppearance(state);
		return (appearance != null) ? invert(appearance.getBoundingBox(source, pos)) : super.getBoundingBox(state, source, pos);
	}
	
	private AxisAlignedBB invert(AxisAlignedBB aabb) {
		return (aabb.minY + aabb.maxY == 1.0) ? aabb : new AxisAlignedBB(aabb.minX, 1.0-aabb.maxY, aabb.minZ, aabb.maxX, 1.0-aabb.minY, aabb.maxZ);
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 0;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (heldItem != null && heldItem.getItem() == Items.STICK) {
			IBlockState appearance = getBlockAppearance(world, pos);
			if (appearance != null) {
				player.addChatMessage(new TextComponentString(appearance.getBlock().getRegistryName().toString()));
			}
			return true;
		}
		return false;
	}
}
