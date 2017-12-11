package mod.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public abstract class SimpleTeleporter extends Teleporter {

	protected SimpleTeleporter(WorldServer world) {
		super(world);
	}

	@Override
	public void placeInPortal(Entity entity, float rotationYaw) {
		BlockPos pos = getDestination(entity);
		entity.motionX = entity.motionY = entity.motionZ = 0.0;
		entity.fallDistance = 0.0f;
		entity.moveToBlockPosAndAngles(pos, entity.rotationYaw, entity.rotationPitch);
	}

	public abstract BlockPos getDestination(Entity entity);

}
