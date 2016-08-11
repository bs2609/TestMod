package mod.util;

import com.google.common.base.Objects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;

public class BlockOffset {

	public final BlockPos origin;
	public final EnumFacing direction;
	public final int distance;

	public BlockOffset(BlockPos orig, EnumFacing dir, int dist) {
		origin = orig;
		direction = dir;
		distance = dist;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BlockOffset that = (BlockOffset) o;
		return distance == that.distance &&
				Objects.equal(origin, that.origin) &&
				direction == that.direction;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(origin, direction, distance);
	}

	@Override
	public String toString() {
		return "BlockOffset: " + distance + " " + direction + " from " + origin;
	}

	public BlockPos resolve() {
		return origin.offset(direction, distance);
	}
}
