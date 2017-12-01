package mod.portal;

import mod.TestMod;
import mod.block.ModBlocks;
import mod.block.PortalFrameBlock;
import mod.util.BlockArea;
import mod.util.BlockOffset;
import mod.util.BlockUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.*;

public class PortalUtils {

	private static final Map<World, PortalLocationData> portalLocations = new WeakHashMap<World, PortalLocationData>();
	private static final Map<PortalType, Map<Integer, Integer>> dimensionMappings = new EnumMap<PortalType, Map<Integer, Integer>>(PortalType.class);
	private static final Map<Entity, Long> entityPortalTimes = new WeakHashMap<Entity, Long>();

	static {
		for (PortalType type : PortalType.VALUES) {
			dimensionMappings.put(type, new HashMap<Integer, Integer>());
		}
	}

	public static void addDimensionMapping(PortalType type, int from, int to) {
		dimensionMappings.get(type).put(from, to);
	}

	public static int getDestinationDimension(PortalType type, int origin) {
		return dimensionMappings.get(type).get(origin);
	}

	public static PortalType getTypeMapping(int from, int to) {
		for (PortalType type : PortalType.VALUES) {
			if (Integer.valueOf(to).equals(dimensionMappings.get(type).get(from))) return type;
		}
		return null;
	}

	public static boolean checkEntity(Entity entity) {
		long currentTime = System.currentTimeMillis();
		if (entityPortalTimes.containsKey(entity) && entityPortalTimes.get(entity) > currentTime - 2000L) {
			return false;
		}
		TestMod.getLogger().debug(entity.getName() + "@" + currentTime);
		entityPortalTimes.put(entity, currentTime);
		return true;
	}

	public static boolean checkDestination(Entity entity, World src, World dst) {
		double scaleFactor = src.provider.getMovementFactor() / dst.provider.getMovementFactor();
		double x = entity.posX * scaleFactor, z = entity.posZ * scaleFactor, size = 3e7;
		return -size < x && x < size && -size < z && z < size;
	}

	public static BlockArea framePlaced(World world, BlockPos pos, IBlockState state, int maxDist) {
		List<BlockPos> frameBlocks = new ArrayList<BlockPos>();
		frameBlocks.add(pos);

		EnumSet<EnumFacing> allDirections = EnumSet.allOf(EnumFacing.class);

		Set<BlockOffset> nextFrames = BlockUtils.countBlocksInRange(world, pos, state, allDirections, maxDist);
		if (nextFrames.size() != 2) return null;

		BlockPos finalPos = null;

		for (BlockOffset offset : nextFrames) {
			EnumSet<EnumFacing> newDirections = EnumSet.copyOf(allDirections);
			newDirections.remove(offset.direction.getOpposite());
			BlockPos newPos = offset.resolve();
			frameBlocks.add(newPos);

			Set<BlockOffset> newFrames = BlockUtils.countBlocksInRange(world, newPos, state, newDirections, maxDist);
			if (newFrames.size() != 1) return null;

			BlockOffset checkOffset = newFrames.iterator().next();
			if (finalPos == null) {
				finalPos = checkOffset.resolve();
			} else {
				if (!checkOffset.resolve().equals(finalPos)) return null;
				frameBlocks.add(finalPos);
			}
		}

		BlockPos min = Collections.min(frameBlocks, BlockUtils.comparePositions);
		BlockPos max = Collections.max(frameBlocks, BlockUtils.comparePositions);

		PortalType type = state.getValue(PortalFrameBlock.TYPE);
		BlockArea frame = new BlockArea(min, max);

		TestMod.getLogger().debug("Found portal frame: " + frame);
		addCandidatePortal(world, frame);
		return frame;
	}

	public static void frameRemoved(World world, BlockPos pos) {
		TestMod.getLogger().debug("Frame block removed: " + pos);
		
		BlockArea active = isActivePortalFrame(world, pos);
		if (active != null) {
			deactivatePortal(world, active);
		}
		
		BlockArea candidate = isPortalFrame(world, pos);
		if (candidate != null) {
			removeCandidatePortal(world, candidate);
		}
	}

	private static boolean addCandidatePortal(World world, BlockArea portal) {
		PortalLocationData data = getLocationData(world);
		return data.candidatePortals.add(portal) && dataChanged(data);
	}

	private static boolean removeCandidatePortal(World world, BlockArea portal) {
		PortalLocationData data = getLocationData(world);
		return data.candidatePortals.remove(portal) && dataChanged(data);
	}

	public static BlockArea isInsidePortal(World world, BlockPos pos) {
		Set<BlockArea> portals = getLocationData(world).candidatePortals;
		return getAreaContaining(pos, portals);
	}

	public static BlockArea isInsideActivePortal(World world, BlockPos pos) {
		Set<BlockArea> portals = getLocationData(world).activePortals;
		return getAreaContaining(pos, portals);
	}
	
	private static BlockArea getAreaContaining(BlockPos pos, Set<BlockArea> areas) {
		for (BlockArea area : areas) {
			if (!area.hasInternalArea()) continue;
			if (area.getInternalArea().contains(pos)) return area;
		}
		return null;
	}
	
	public static BlockArea isPortalFrame(World world, BlockPos pos) {
		Set<BlockArea> portals = getLocationData(world).candidatePortals;
		return isFrameBlock(pos, portals);
	}
	
	public static BlockArea isActivePortalFrame(World world, BlockPos pos) {
		Set<BlockArea> portals = getLocationData(world).activePortals;
		return isFrameBlock(pos, portals);
	}
	
	private static BlockArea isFrameBlock(BlockPos pos, Set<BlockArea> areas) {
		for (BlockArea area : areas) {
			for (BlockPos framePos : BlockUtils.getVertices(area)) {
				if (pos.equals(framePos)) return area;
			}
		}
		return null;
	}

	public static List<BlockArea> getPortalsWithin(World world, BlockArea area) {
		List<BlockArea> portals = new ArrayList<BlockArea>();
		Set<BlockArea> allPortals = getLocationData(world).activePortals;
		for (BlockArea portal : allPortals) {
			if (portal.intersects(area)) portals.add(portal);
		}
		Collections.sort(portals, area.compareDistances());
		return portals;
	}
	
	public static BlockArea getClosestPortal(World world, Entity entity) {
		Set<BlockArea> portals = getLocationData(world).activePortals;
		Comparator<BlockArea> distanceToEntity = BlockArea.compareDistancesTo(entity.getPositionVector());
		return portals.isEmpty() ? null : Collections.min(portals, distanceToEntity);
	}
	
	public static boolean constructPortal(World world, BlockArea area, IBlockState frame, IBlockState border, IBlockState interior) {
		TestMod.getLogger().debug("Constructing portal: " + area);
		constructFrame(world, area, frame);
		constructBorder(world, area, border);
		constructInterior(world, area, interior);
		return checkPortal(world, area, border, interior);
	}

	private static void constructFrame(World world, BlockArea area, IBlockState frame) {
		int dist = area.getLongestSide();
		for (BlockPos pos : BlockUtils.getVertices(area)) {
			world.setBlockState(pos, frame);
			framePlaced(world, pos, frame, dist);
		}
	}

	private static void constructBorder(World world, BlockArea area, IBlockState border) {
		List<BlockArea> borderSections = BlockUtils.getStructureComponents(area, 1);
		for (BlockArea section : borderSections) {
			if (!section.hasInternalArea()) continue;
			BlockUtils.fillArea(world, section.getInternalArea(), border);
		}
	}

	private static void constructInterior(World world, BlockArea area, IBlockState interior) {
		if (!area.hasInternalArea()) return;
		BlockUtils.fillArea(world, area.getInternalArea(), interior);
	}

	public static PortalType getPortalType(World world, BlockArea area) {
		EnumSet<PortalType> types = EnumSet.noneOf(PortalType.class);
		for (BlockPos pos : BlockUtils.getVertices(area)) {
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() == ModBlocks.PORTAL_FRAME) {
				types.add(state.getValue(PortalFrameBlock.TYPE));
			}
		}
		if (types.size() != 1) return null;
		return types.iterator().next();
	}

	public static boolean checkPortal(World world, BlockArea area, IBlockState border, IBlockState interior) {
		TestMod.getLogger().debug("Checking portal type...");
		PortalType type = getPortalType(world, area);
		return (type != null) && validateWorld(world, type) && validatePortal(world, area, border, interior) && activatePortal(world, area, type) || deactivatePortal(world, area);
	}

	private static boolean validateWorld(World world, PortalType type) {
		int dim = world.provider.getDimension();
		return dimensionMappings.get(type).containsKey(dim);
	}

	private static boolean validatePortal(World world, BlockArea area, IBlockState border, IBlockState interior) {
		TestMod.getLogger().debug("Checking portal structure...");
		return validateInterior(world, area, interior) && validateBorder(world, area, border);
	}

	private static boolean validateInterior(World world, BlockArea portal, IBlockState interior) {
		return portal.hasInternalArea() && BlockUtils.containsOnly(world, portal.getInternalArea(), interior);
	}

	private static boolean validateBorder(World world, BlockArea portal, IBlockState border) {
		List<BlockArea> borderSections = BlockUtils.getStructureComponents(portal, 1);
		for (BlockArea section : borderSections) {
			if (!section.hasInternalArea()) continue;
			if (!BlockUtils.containsOnly(world, section.getInternalArea(), border)) return false;
		}
		return true;
	}

	private static boolean activatePortal(World world, BlockArea portal, PortalType type) {
		if (getLocationData(world).activePortals.contains(portal)) return true;
		TestMod.getLogger().debug("Activating portal...");
		IBlockState active = Portal.getInterior(type);
		BlockUtils.fillArea(world, portal.getInternalArea(), active);
		addActivePortal(world, portal);
		return true;
	}

	private static boolean deactivatePortal(World world, BlockArea portal) {
		if (removeActivePortal(world, portal)) {
			TestMod.getLogger().debug("Deactivating portal...");
			IBlockState clear = Blocks.AIR.getDefaultState();
			BlockUtils.fillArea(world, portal.getInternalArea(), clear);
		}
		return false;
	}

	private static boolean addActivePortal(World world, BlockArea portal) {
		PortalLocationData data = getLocationData(world);
		return data.activePortals.add(portal) && dataChanged(data);
	}

	private static boolean removeActivePortal(World world, BlockArea portal) {
		PortalLocationData data = getLocationData(world);
		return data.activePortals.remove(portal) && dataChanged(data);
	}

	private static PortalLocationData getLocationData(World world) {
		PortalLocationData data = portalLocations.get(world);
		if (data == null) {
			data = PortalLocationData.get(world);
			portalLocations.put(world, data);
		}
		return data;
	}

	private static boolean dataChanged(PortalLocationData data) {
		data.markDirty();
		return true;
	}
}
