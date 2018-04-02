package mod.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;

public abstract class SimpleTeleporter implements ITeleporter {

	@Override
	public void placeEntity(World world, Entity entity, float yaw) {
		BlockPos pos = getDestination(world, entity);
		entity.motionX = entity.motionY = entity.motionZ = 0.0;
		entity.fallDistance = 0.0f;
		entity.moveToBlockPosAndAngles(pos, entity.rotationYaw, entity.rotationPitch);
	}

	protected abstract BlockPos getDestination(World world, Entity entity);

}
