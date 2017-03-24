package mod.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BlockStructurePlacementHelper {
	
	private final BitSet bitSet;
	private final BlockPos origin;
	private final Vec3i size;
	
	public BlockStructurePlacementHelper(World world, BlockArea area, BlockStateChecker checker) {
		origin = area.minPos;
		size = VectorUtils.add(area.getSize(), 1);
		bitSet = new BitSet(size.getX() * size.getY() * size.getZ());
		populateBits(world, area, checker);
	}
	
	private int getIndex(int x, int y, int z) {
		int idx = x;
		idx *= size.getY();
		idx += y;
		idx *= size.getZ();
		idx += z;
		return idx;
	}
	
	private int getIndex(BlockPos pos) {
		int x = pos.getX() - origin.getX();
		int y = pos.getY() - origin.getY();
		int z = pos.getZ() - origin.getZ();
		return getIndex(x, y, z);
	}
	
	private BlockPos getPos(int idx) {
		int z = idx % size.getZ();
		idx /= size.getZ();
		int y = idx % size.getY();
		idx /= size.getY();
		int x = idx;
		return new BlockPos(x, y, z);
	}
	
	private void populateBits(World world, BlockArea area, BlockStateChecker checker) {
		for (BlockPos pos : area) {
			boolean flag = checker.check(world, pos, world.getBlockState(pos));
			bitSet.set(getIndex(pos), flag);
		}
	}
	
	public Set<BlockArea> findPlacementsFor(Vec3i targetSize) {
		
		if (size.getX() < targetSize.getX() || size.getY() < targetSize.getY() || size.getZ() < targetSize.getZ()) {
			return Collections.emptySet();
		}
		
		BitSet working = (BitSet) bitSet.clone();
		
		int target = targetSize.getX();
		if (target > 0) {
			for (int y = 0; y < size.getY(); ++y) {
				for (int z = 0; z < size.getZ(); ++z) {
					for (int x = 0, count = 0; x < size.getX(); ++x) {
						count = check(working, getIndex(x, y, z), count, target);
					}
				}
			}
		}
		
		target = targetSize.getY();
		if (target > 0) {
			for (int z = 0; z < size.getZ(); ++z) {
				for (int x = 0; x < size.getX(); ++x) {
					for (int y = 0, count = 0; y < size.getY(); ++y) {
						count = check(working, getIndex(x, y, z), count, target);
					}
				}
			}
		}
		
		target = targetSize.getZ();
		if (target > 0) {
			for (int x = 0; x < size.getX(); ++x) {
				for (int y = 0; y < size.getY(); ++y) {
					for (int z = 0, count = 0; z < size.getZ(); ++z) {
						count = check(working, getIndex(x, y, z), count, target);
					}
				}
			}
		}
		
		Set<BlockArea> placements = new HashSet<BlockArea>();
		for (int i = working.nextSetBit(0); i >= 0; i = working.nextSetBit(i+1)) {
			BlockPos pos = getPos(i).add(origin);
			placements.add(new BlockArea(pos.subtract(targetSize), pos));
		}
		return placements;
	}
	
	private static int check(BitSet bits, int index, int count, int target) {
		if (bits.get(index)) {
			if (count < target) {
				bits.clear(index);
			}
			return count+1;
		}
		return 0;
	}
}
