package mod.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class BlockPosTeleporter extends SimpleTeleporter {
	
	private final BlockPos destination;
	
	public BlockPosTeleporter(WorldServer world, BlockPos pos) {
		super(world);
		destination = pos.toImmutable();
	}
	
	@Override
	public BlockPos getDestinationForEntity(Entity entity) {
		return destination;
	}
}
