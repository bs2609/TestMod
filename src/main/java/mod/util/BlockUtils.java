package mod.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Cartesian;
import net.minecraft.world.World;

import java.util.*;

public class BlockUtils {

	public static final Comparator<BlockPos> comparePositions = new Comparator<BlockPos>() {
		@Override
		public int compare(BlockPos o1, BlockPos o2) {

			int x1 = o1.getX(), x2 = o2.getX();
			int y1 = o1.getY(), y2 = o2.getY();
			int z1 = o1.getZ(), z2 = o2.getZ();

			if (x1 > x2) return 1;
			if (x1 < x2) return -1;
			if (y1 > y2) return 1;
			if (y1 < y2) return -1;
			if (z1 > z2) return 1;
			if (z1 < z2) return -1;

			return 0;
		}
	};

	public static final Comparator<BlockPos> compareStrict = new Comparator<BlockPos>() {
		@Override
		public int compare(BlockPos o1, BlockPos o2) {

			int x1 = o1.getX(), x2 = o2.getX();
			int y1 = o1.getY(), y2 = o2.getY();
			int z1 = o1.getZ(), z2 = o2.getZ();

			if (x1 == x2 && y1 == y2 && z1 == z2) return 0;
			if (x1 >= x2 && y1 >= y2 && z1 >= z2) return 1;
			if (x1 <= x2 && y1 <= y2 && z1 <= z2) return -1;

			return 0;
		}
	};
	
	private static final Comparator<BlockArea> compareDimensions = new Comparator<BlockArea>() {
		@Override
		public int compare(BlockArea o1, BlockArea o2) {
			int d1 = o1.getDims(), d2 = o2.getDims();
			return d2 - d1;
		}
	};

	public static Set<BlockOffset> countBlocksInRange(World world, BlockPos from, IBlockState matching, EnumSet<EnumFacing> directions, int range) {
		Set<BlockOffset> blocks = new HashSet<BlockOffset>();
		for (EnumFacing direction : directions) {
			for (int i = 1; i <= range; i++) {
				BlockPos pos = from.offset(direction, i);
				if (world.getBlockState(pos) == matching) {
					blocks.add(new BlockOffset(from, direction, i));
				}
			}
		}
		return blocks;
	}

	public static boolean containsOnly(World world, BlockArea area, IBlockState state) {
		return containsOnly(world, area, state, false);
	}

	public static boolean containsOnly(World world, BlockArea area, IBlockState state, boolean matchState) {
		for (BlockPos pos : area) {
			if (matchState && world.getBlockState(pos) != state
					|| world.getBlockState(pos).getBlock() != state.getBlock()) {
				return false;
			}
		}
		return true;
	}
	
	public static void placeStructure(World world, BlockArea area, BlockStructure structure) {
		placeStructure(world, area, structure, 3);
	}
	
	public static void placeStructure(World world, BlockArea area, BlockStructure structure, int flags) {
		List<BlockArea> components = getStructureComponents(area, true);
		Collections.sort(components, compareDimensions);
		for (BlockArea component: components) {
			IBlockState state = structure.getStateFor(component);
			if (state != null) {
				fillArea(world, component, state, flags);
			}
		}
	}

	public static void fillArea(World world, BlockArea area, IBlockState state) {
		fillArea(world, area, state, 3);
	}

	public static void fillArea(World world, BlockArea area, IBlockState state, int flags) {
		for (BlockPos pos : area) {
			world.setBlockState(pos, state, flags);
		}
	}

	public static List<BlockPos> getVertices(BlockArea area) {

		List<BlockPos> vertices = new ArrayList<BlockPos>();

		int minX = area.minPos.getX(), maxX = area.maxPos.getX();
		int minY = area.minPos.getY(), maxY = area.maxPos.getY();
		int minZ = area.minPos.getZ(), maxZ = area.maxPos.getZ();

		Set<Integer> X = new HashSet<Integer>(2), Y = new HashSet<Integer>(2), Z = new HashSet<Integer>(2);

		X.add(minX); X.add(maxX);
		Y.add(minY); Y.add(maxY);
		Z.add(minZ); Z.add(maxZ);

		Iterable<Integer[]> product = Cartesian.cartesianProduct(Integer.class, Arrays.asList(X, Y, Z));

		for (Integer[] vertex : product) {
			vertices.add(new BlockPos(vertex[0], vertex[1], vertex[2]));
		}

		return vertices;
	}

	public static List<BlockArea> getStructureComponents(BlockArea area, boolean all) {

		List<BlockArea> components = new ArrayList<BlockArea>();
		List<BlockPos> vertices = getVertices(area);
		Collections.sort(vertices, comparePositions);

		for (int i = 0; i < vertices.size(); i++) {
			BlockPos a = vertices.get(i);
			if (all) {
				components.add(new BlockArea(a, a));
			}
			for (int j = i+1; j < vertices.size(); j++) {
				BlockPos b = vertices.get(j);
				if (compareStrict.compare(a, b) < 0) {
					components.add(new BlockArea(a, b));
				}
			}
		}
		return components;
	}

	public static List<BlockArea> getStructureComponents(BlockArea area, int dims) {
		List<BlockArea> components = new ArrayList<BlockArea>();
		for (BlockArea component : getStructureComponents(area, dims == 0)) {
			if (component.getDims() == dims) {
				components.add(component);
			}
		}
		return components;
	}
}
