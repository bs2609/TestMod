package mod.block;

import mod.portal.Portal;
import mod.portal.PortalType;
import mod.portal.PortalUtils;
import mod.util.BlockArea;
import mod.util.MiscUtils;
import mod.world.ModDimensions;
import mod.world.PortalTeleporter;
import mod.world.SurrealWorldTeleporter;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Random;

@SuppressWarnings("deprecation")
public class PortalInteriorBlock extends BasicBlock {

	public static final String NAME = "portalInteriorBlock";
	public static final IProperty<PortalType> TYPE = PortalType.PROPERTY;

	PortalInteriorBlock() {
		super(NAME, Material.PORTAL);
		setLightLevel(0.75f);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		IBlockState other = worldIn.getBlockState(pos.offset(side));
		return other != state && super.shouldSideBeRendered(state, worldIn, pos, side);
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}
	
	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return null;
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
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE);
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (worldIn.isRemote || !PortalUtils.checkEntity(entityIn)) return;
		if (entityIn instanceof EntityPlayerMP) {
			onPlayerCollision(worldIn, pos, state, (EntityPlayerMP) entityIn);
		}
		if (entityIn instanceof EntityFishHook) {
			onFishingExpedition(worldIn, pos, state, (EntityFishHook) entityIn);
		}
	}

	private void onPlayerCollision(World world, BlockPos pos, IBlockState state, EntityPlayerMP player) {
		PortalType typeIn = state.getValue(TYPE);
		BlockArea area = PortalUtils.isInsideActivePortal(world, pos);
		if (area == null) {
			area = PortalUtils.isInsidePortal(world, pos);
			IBlockState border = Portal.getBorder();
			if (area == null || !PortalUtils.checkPortal(world, area, border, state)) return;
		}
		int origin = world.provider.getDimension();
		int destination = PortalUtils.getDestinationDimension(typeIn, origin);
		WorldServer worldOut = MiscUtils.worldServerForDimension(destination);

		Teleporter teleporter;
		if (PortalUtils.checkDestination(player, world, worldOut)) {
			PortalType typeOut = PortalUtils.getTypeMapping(destination, origin);
			teleporter = new PortalTeleporter(worldOut, area.getSize(), typeOut);
		} else {
			destination = ModDimensions.DIM_SURREAL;
			worldOut = MiscUtils.worldServerForDimension(destination);
			teleporter = new SurrealWorldTeleporter(worldOut);
			player.addChatComponentMessage(new TextComponentString("Error/.Error"));
		}
		
		MiscUtils.setInvulnerableDimensionChange(player);

		world.getMinecraftServer().getPlayerList().transferPlayerToDimension(player, destination, teleporter);
	}

	private void onFishingExpedition(World world, BlockPos pos, IBlockState state, EntityFishHook fishHook) {
		PortalType typeIn = state.getValue(TYPE);
		BlockArea area = PortalUtils.isInsideActivePortal(world, pos);
		if (area == null) {
			area = PortalUtils.isInsidePortal(world, pos);
			IBlockState border = Portal.getBorder();
			if (area == null || !PortalUtils.checkPortal(world, area, border, state)) return;
		}
		int origin = world.provider.getDimension();
		int destination = PortalUtils.getDestinationDimension(typeIn, origin);
		WorldServer worldOut = MiscUtils.worldServerForDimension(destination);
		PortalType typeOut = PortalUtils.getTypeMapping(destination, origin);
		Teleporter teleporter = new PortalTeleporter(worldOut, area.getSize(), typeOut);
		WorldServer worldIn = (WorldServer) world;

		fishHook.angler.addChatComponentMessage(new TextComponentString("Probing (" + typeIn + ") portal at " + area + "..."));

		world.getMinecraftServer().getPlayerList().transferEntityToWorld(fishHook, origin, worldIn, worldOut, teleporter);

		fishHook.angler.addChatComponentMessage(new TextComponentString("Portal to dimension " + destination + " around " + new BlockPos(fishHook.posX, fishHook.posY, fishHook.posZ)));

		world.getMinecraftServer().getPlayerList().transferEntityToWorld(fishHook, destination, worldOut, worldIn, teleporter);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock) {
		if (worldIn.isRemote) return;
		BlockArea portal = PortalUtils.isInsideActivePortal(worldIn, pos);
		IBlockState border = Portal.getBorder();
		if (portal != null) {
			PortalUtils.checkPortal(worldIn, portal, border, state);
		}
	}
}
