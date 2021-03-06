package mod.util;

import com.google.common.base.Objects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;

public class BlockArea implements Iterable<BlockPos>, INBTSerializable<NBTTagCompound> {

	public final BlockPos minPos;
	public final BlockPos maxPos;

	private final Vec3i in;

	public BlockArea(BlockPos min, BlockPos max) {
		minPos = min.toImmutable();
		maxPos = max.toImmutable();
		in = createInternalVector();
	}

	private Vec3i createInternalVector() {
		int i = minPos.getX() < maxPos.getX() ? 1 : 0;
		int j = minPos.getY() < maxPos.getY() ? 1 : 0;
		int k = minPos.getZ() < maxPos.getZ() ? 1 : 0;
		return new Vec3i(i, j, k);
	}

	@Override
	public String toString() {
		return "[" + minPos + ", " + maxPos + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BlockArea blockArea = (BlockArea) o;
		return Objects.equal(minPos, blockArea.minPos) && Objects.equal(maxPos, blockArea.maxPos);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(minPos, maxPos);
	}

	public boolean hasInternalArea() {
		int dims = getDims();
		return dims > 0 && dims == new BlockArea(minPos.add(in), maxPos).getDims();
	}

	public BlockArea getInternalArea() {
		return new BlockArea(minPos.add(in), maxPos.subtract(in));
	}

	public int getDims() {
		return in.getX() + in.getY() + in.getZ();
	}

	public EnumSet<EnumFacing.Axis> getAxes() {
		EnumSet<EnumFacing.Axis> axes = EnumSet.noneOf(EnumFacing.Axis.class);
		if (in.getX() > 0) axes.add(EnumFacing.Axis.X);
		if (in.getY() > 0) axes.add(EnumFacing.Axis.Y);
		if (in.getZ() > 0) axes.add(EnumFacing.Axis.Z);
		return axes;
	}

	public Vec3i getSize() {
		int x = maxPos.getX() - minPos.getX();
		int y = maxPos.getY() - minPos.getY();
		int z = maxPos.getZ() - minPos.getZ();
		return new Vec3i(x, y, z);
	}

	public int getLongestSide() {
		int x = maxPos.getX() - minPos.getX();
		int y = maxPos.getY() - minPos.getY();
		int z = maxPos.getZ() - minPos.getZ();
		return Math.max(Math.max(x, y), z);
	}

	public Vec3d getRelativePosition(Vec3d vec) {
		double x = (1.0-vec.x) * minPos.getX() + vec.x * (maxPos.getX()+1.0);
		double y = (1.0-vec.y) * minPos.getY() + vec.y * (maxPos.getY()+1.0);
		double z = (1.0-vec.z) * minPos.getZ() + vec.z * (maxPos.getZ()+1.0);
		return new Vec3d(x, y, z);
	}
	
	private static final Vec3d CENTRE = new Vec3d(0.5, 0.5, 0.5);
	
	public Vec3d getCentre() {
		return getRelativePosition(CENTRE);
	}

	public Comparator<BlockArea> compareDistances() {
		return new Comparator<BlockArea>() {
			@Override
			public int compare(BlockArea o1, BlockArea o2) {
				double d1 = getDist(o1, BlockArea.this), d2 = getDist(o2, BlockArea.this);
				return Double.compare(d1, d2);
			}

			private double getDist(BlockArea a1, BlockArea a2) {
				Vec3d v1 = a1.getCentre(), v2 = a2.getCentre();
				return v1.squareDistanceTo(v2);
			}
		};
	}
	
	public static Comparator<BlockArea> compareDistancesTo(final Vec3d vec) {
		return new Comparator<BlockArea>() {
			@Override
			public int compare(BlockArea o1, BlockArea o2) {
				double d1 = o1.getCentre().squareDistanceTo(vec), d2 = o2.getCentre().squareDistanceTo(vec);
				return Double.compare(d1, d2);
			}
		};
	}

	public boolean intersects(BlockArea other) {
		return gte(this.maxPos, other.minPos) && lte(this.minPos, other.maxPos);
	}

	public boolean contains(BlockPos pos) {
		return gte(maxPos, pos) && lte(minPos, pos);
	}

	public boolean contains(BlockArea other) {
		return gte(this.maxPos, other.maxPos) && lte(this.minPos, other.minPos);
	}

	private static boolean gte(BlockPos a, BlockPos b) {
		return a.equals(b) || BlockUtils.compareStrict.compare(a, b) > 0;
	}

	private static boolean lte(BlockPos a, BlockPos b) {
		return a.equals(b) || BlockUtils.compareStrict.compare(a, b) < 0;
	}

	public BlockArea translate(Vec3i vector) {
		return new BlockArea(minPos.add(vector), maxPos.add(vector));
	}

	public BlockArea flatten(EnumFacing side) {

		int minX = minPos.getX(), minY = minPos.getY(), minZ = minPos.getZ();
		int maxX = maxPos.getX(), maxY = maxPos.getY(), maxZ = maxPos.getZ();

		switch (side.getAxisDirection()) {
			case POSITIVE:
				switch (side.getAxis()) {
					case X: minX = maxX; break;
					case Y: minY = maxY; break;
					case Z: minZ = maxZ; break;
				} break;
			
			case NEGATIVE:
				switch (side.getAxis()) {
					case X: maxX = minX; break;
					case Y: maxY = minY; break;
					case Z: maxZ = minZ; break;
				} break;
		}

		return new BlockArea(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
	}

	public AxisAlignedBB toAABB() {
		return new AxisAlignedBB(minPos, maxPos.add(1, 1, 1));
	}

	@Override
	public Iterator<BlockPos> iterator() {
		return BlockPos.getAllInBox(minPos, maxPos).iterator();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setLong("minPos", minPos.toLong());
		nbt.setLong("maxPos", maxPos.toLong());
		return nbt;
	}

	public static BlockArea fromNBT(NBTTagCompound nbt) {
		BlockPos min = BlockPos.fromLong(nbt.getLong("minPos"));
		BlockPos max = BlockPos.fromLong(nbt.getLong("maxPos"));
		return new BlockArea(min, max);
	}
}
