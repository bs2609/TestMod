package mod.world;

import mod.portal.Portal;
import mod.portal.PortalType;
import mod.portal.PortalUtils;
import mod.util.BlockArea;
import mod.util.BlockStructurePlacementHelper;
import mod.util.BlockUtils;
import mod.util.VectorUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

import java.util.EnumSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class PortalTeleporter extends Teleporter {
	
	private final Vec3i portalSize;
	private final PortalType portalType;
	
	public PortalTeleporter(WorldServer world, Vec3i size, PortalType type) {
		super(world);
		portalSize = size;
		portalType = type;
	}

	@Override
	public void placeInPortal(Entity entity, float rotationYaw) {
		if (!placeInExistingPortal(entity, rotationYaw)) {
			makePortal(entity);
			placeInExistingPortal(entity, rotationYaw);
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, float rotationYaw) {
		BlockArea existing = hasExistingPortal(entity);
		if (existing == null) return false;
		BlockArea cleared = clearArea(existing);
		Vec3d v1 = getBottomCentre(cleared);
		placeEntity(entity, v1);
		Vec3d v2 = getEntityOffset(entity, existing);
		placeEntity(entity, v1.add(v2));
		return true;
	}

	@Override
	public boolean makePortal(Entity entity) {
		BlockArea area = getPortalArea(entity);
		IBlockState frame = Portal.getFrame(portalType);
		IBlockState border = Portal.getBorder();
		IBlockState interior = Portal.getInterior(portalType);
		return PortalUtils.constructPortal(world, area, frame, border, interior);
	}

	private BlockArea hasExistingPortal(Entity entity) {
		BlockPos entityPos = new BlockPos(entity);
		Vec3i range = new Vec3i(16, 16, 16);
		BlockArea searchArea = new BlockArea(entityPos.subtract(range), entityPos.add(range));
		for (BlockArea area : PortalUtils.getPortalsWithin(world, searchArea)) {
			if (PortalUtils.getPortalType(world, area) == portalType) return area;
		}
		return null;
	}

	private BlockArea getPortalArea(Entity entity) {
		
		Vec3i size = VectorUtils.add(VectorUtils.scale(portalSize, 3), 2);
		BlockArea searchArea = getArea(entity, size);
		
		BlockStructurePlacementHelper helper = new BlockStructurePlacementHelper(world, searchArea, BlockUtils.airBlocks);
		Set<BlockArea> valid = helper.findPlacementsFor(portalSize);
		
		if (!valid.isEmpty()) {
			SortedSet<BlockArea> sorted = new TreeSet<BlockArea>(BlockArea.compareDistancesTo(entity.getPositionVector()));
			sorted.addAll(valid);
			return sorted.first();
		}
		
		return getArea(entity, portalSize);
	}
	
	private BlockArea getArea(Entity entity, Vec3i size) {
		BlockPos entityPos = new BlockPos(entity);
		BlockArea init = new BlockArea(entityPos, entityPos.add(size));
		Vec3d v = entity.getPositionVector().subtract(getBottomCentre(init));
		return init.translate(new Vec3i(v.x, v.y, v.z));
	}

	private BlockArea clearArea(BlockArea area) {
		if (!area.hasInternalArea()) return null;

		BlockArea internal = area.getInternalArea();
		BlockPos min = internal.minPos, max = internal.maxPos;

		for (EnumFacing.Axis axis : EnumSet.complementOf(area.getAxes())) {
			int dist = (axis == EnumFacing.Axis.Y) ? 2 : 1;
			min = min.offset(EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.NEGATIVE, axis), dist);
			max = max.offset(EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, axis), dist);
		}

		BlockArea clearArea = new BlockArea(min, max);
		IBlockState clearBlock = Blocks.AIR.getDefaultState();

		for (BlockPos pos : clearArea) {
			if (!internal.contains(pos) && !world.isAirBlock(pos)) {
				world.setBlockState(pos, clearBlock);
			}
		}

		BlockArea floorArea = clearArea.flatten(EnumFacing.DOWN).translate(EnumFacing.DOWN.getDirectionVec());
		IBlockState floorBlock = Blocks.COBBLESTONE.getDefaultState();

		for (BlockPos pos : floorArea) {
			IBlockState state = world.getBlockState(pos);
			if (!state.isSideSolid(world, pos, EnumFacing.UP)) {
				world.setBlockState(pos, floorBlock);
			}
		}

		return clearArea;
	}

	private void placeEntity(Entity entity, Vec3d location) {

		double x = location.x, y = location.y, z = location.z;
		float yaw = entity.rotationYaw, pitch = entity.rotationPitch;

		entity.motionX = entity.motionY = entity.motionZ = 0.0;
		entity.fallDistance = 0.0f;

		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			player.connection.setPlayerLocation(x, y, z, yaw, pitch);
		} else  {
			entity.setLocationAndAngles(x, y, z, yaw, pitch);
		}
	}

	private Vec3d getBottomCentre(BlockArea area) {
		return area.getRelativePosition(new Vec3d(0.5, 0.0, 0.5));
	}

	private Vec3d getEntityOffset(Entity entity, BlockArea area) {
		if (entity.getEntityBoundingBox().intersects(area.toAABB())) {
			EnumFacing facing = entity.getHorizontalFacing();
			EnumSet<EnumFacing.Axis> axes = EnumSet.complementOf(area.getAxes());
			if (axes.contains(facing.getAxis())) {
				return new Vec3d(facing.getDirectionVec());

			} else {
				for (EnumFacing.Axis axis : axes) {
					EnumFacing dir = EnumFacing.getFacingFromAxis(facing.getAxisDirection(), axis);
					return new Vec3d(dir.getDirectionVec());
				}
			}
		}
		return Vec3d.ZERO;
	}
}
