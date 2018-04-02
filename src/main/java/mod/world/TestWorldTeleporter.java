package mod.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TestWorldTeleporter extends SimpleTeleporter {

	@Override
	public BlockPos getDestination(World world, Entity entity) {
		return new BlockPos(0, 64, 0);
	}
}
