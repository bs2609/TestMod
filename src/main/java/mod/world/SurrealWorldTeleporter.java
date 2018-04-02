package mod.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SurrealWorldTeleporter extends SimpleTeleporter {

	@Override
	public BlockPos getDestination(World world, Entity entity) {
		double x = entity.posX, y = 256.0, z = entity.posZ;
		return new BlockPos(x, y, z);
	}
}
