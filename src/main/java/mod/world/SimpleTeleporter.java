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
	public void placeInPortal(Entity entityIn, float rotationYaw) {
		BlockPos pos = getDestinationForEntity(entityIn);
		entityIn.motionX = entityIn.motionY = entityIn.motionZ = 0.0;
		entityIn.fallDistance = 0.0f;
		entityIn.moveToBlockPosAndAngles(pos, entityIn.rotationYaw, 0.0f);
	}

	public abstract BlockPos getDestinationForEntity(Entity entity);

}
