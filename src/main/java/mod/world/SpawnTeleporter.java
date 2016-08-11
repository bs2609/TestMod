package mod.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class SpawnTeleporter extends SimpleTeleporter {

	public SpawnTeleporter(WorldServer world) {
		super(world);
	}

	@Override
	public BlockPos getDestinationForEntity(Entity entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			BlockPos bed = player.getBedLocation(0);
			if (bed != null) return bed;
		}
		return worldAccess.getTopSolidOrLiquidBlock(worldAccess.getSpawnPoint());
	}

}
