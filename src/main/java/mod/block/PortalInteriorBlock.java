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
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation")
public class PortalInteriorBlock extends UnobtainableBlock {

	public static final String NAME = "portal_interior";
	public static final IProperty<PortalType> TYPE = PortalType.PROPERTY;

	PortalInteriorBlock() {
		super(NAME, Material.PORTAL);
		setLightLevel(0.75f);
	}

	@Override
	@SideOnly(Side.CLIENT)
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
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		IBlockState other = worldIn.getBlockState(pos.offset(side));
		return other != state && super.shouldSideBeRendered(state, worldIn, pos, side);
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
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		
		if (world.isRemote || !PortalUtils.checkEntity(entity)) return;
		
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
		
		if (PortalUtils.checkDestination(entity, world, worldOut)) {
			PortalType typeOut = PortalUtils.getTypeMapping(destination, origin);
			teleporter = new PortalTeleporter(worldOut, area.getSize(), typeOut);
		} else {
			destination = ModDimensions.DIM_SURREAL;
			worldOut = MiscUtils.worldServerForDimension(destination);
			teleporter = new SurrealWorldTeleporter(worldOut);
			entity.sendMessage(new TextComponentString("Error/.Error"));
		}
		
		PlayerList playerList = world.getMinecraftServer().getPlayerList();
		
		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			MiscUtils.setInvulnerableDimensionChange(player);
			playerList.transferPlayerToDimension(player, destination, teleporter);
		} else {
			WorldServer worldIn = (WorldServer) world;
			playerList.transferEntityToWorld(entity, origin, worldIn, worldOut, teleporter);
		}
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
