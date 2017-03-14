package mod.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public abstract class SimpleTeleporter extends Teleporter {

	protected final WorldServer worldAccess;

	protected SimpleTeleporter(WorldServer world) {
		super(world);
		worldAccess = world;
	}

	@Override
	public void placeInPortal(Entity entity, float rotationYaw) {
		BlockPos pos = getDestinationForEntity(entity);
		entity.motionX = entity.motionY = entity.motionZ = 0.0;
		entity.fallDistance = 0.0f;
		entity.moveToBlockPosAndAngles(pos, entity.rotationYaw, 0.0f);
	}

	public abstract BlockPos getDestinationForEntity(Entity entity);

}
