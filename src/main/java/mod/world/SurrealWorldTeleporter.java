package mod.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class SurrealWorldTeleporter extends SimpleTeleporter {

	public SurrealWorldTeleporter(WorldServer world) {
		super(world);
	}

	@Override
	public BlockPos getDestination(Entity entity) {
		double x = entity.posX, y = 256.0, z = entity.posZ;
		return new BlockPos(x, y, z);
	}
}
