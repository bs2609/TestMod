package mod.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

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
		return new Vec3d(a.xCoord*b.xCoord, a.yCoord*b.yCoord, a.zCoord*b.zCoord);
	}
}
