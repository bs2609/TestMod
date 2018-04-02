package mod.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPosTeleporter extends SimpleTeleporter {
	
	private final BlockPos destination;
	
	public BlockPosTeleporter(BlockPos pos) {
		destination = pos.toImmutable();
	}
	
	@Override
	public BlockPos getDestination(World world, Entity entity) {
		return destination;
	}
}
