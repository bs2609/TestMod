package mod.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class VectorUtils {

	public static EnumFacing getPrimaryDirection(Vec3d vector) {

		double max = 0.0;
		EnumFacing result = null;

		for (EnumFacing direction : EnumFacing.values()) {
			double product = vector.dotProduct(new Vec3d(direction.getDirectionVec()));
			if (product > max) {
				max = product;
				result = direction;
			}
		}

		return result;
	}

	public static Vec3d multiply(Vec3d a, Vec3d b) {
		return new Vec3d(a.x * b.x, a.y * b.y, a.z * b.z);
	}

	public static Vec3i add(Vec3i vec, int n) {
		return (n == 0) ? vec : new Vec3i(vec.getX() + n, vec.getY() + n, vec.getZ() + n);
	}

	public static Vec3i scale(Vec3i vec, int n) {
		return (n == 1) ? vec : new Vec3i(vec.getX() * n, vec.getY() * n, vec.getZ() * n);
	}
}
