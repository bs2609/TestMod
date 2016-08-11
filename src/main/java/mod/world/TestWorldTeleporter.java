package mod.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class TestWorldTeleporter extends SimpleTeleporter {

	public TestWorldTeleporter(WorldServer world) {
		super(world);
	}

	@Override
	public BlockPos getDestinationForEntity(Entity entity) {
		return new BlockPos(0, 64, 0);
	}
}
