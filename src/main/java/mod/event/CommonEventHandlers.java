package mod.event;

import mod.TestMod;
import mod.portal.Portal;
import mod.portal.PortalUtils;
import mod.util.BlockArea;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonEventHandlers {
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new CommonEventHandlers());
	}

	// TODO: Use ThrowableImpactEvent for 1.10
	
	@SubscribeEvent
	public void onEnderTeleportEvent(EnderTeleportEvent event) {
		// if an ender pearl is used
		if (event.getEntityLiving() instanceof EntityPlayer) {
			World world = event.getEntity().worldObj;
			if (world.isRemote) return;

			TestMod.getLogger().debug("Event location: " + new Vec3d(event.getTargetX(), event.getTargetY(), event.getTargetZ()));
			// get the actual thrown entity so we can guess the target block
			EntityEnderPearl pearl = getThrownEnderPearl(world, event.getTargetX(), event.getTargetY(), event.getTargetZ());
			if (pearl == null) return;

			BlockPos pos = getTargetPos(pearl);
			if (pos == null) return;

			TestMod.getLogger().debug("Event target: " + pos);
			if (world.getBlockState(pos) != Portal.getInterior()) return;

			// check for portal at this location
			TestMod.getLogger().debug("Searching for portal...");
			BlockArea portal = PortalUtils.isInsidePortal(world, pos);
			if (portal == null) return;

			TestMod.getLogger().debug("Found portal: " + portal);
			IBlockState border = Portal.getBorder();
			IBlockState interior = Portal.getInterior();
			if (PortalUtils.checkPortal(world, portal, border, interior)) {
				event.setCanceled(true);
			}
		}
	}

	private EntityEnderPearl getThrownEnderPearl(World world, double x, double y, double z) {
		AxisAlignedBB aabb = new AxisAlignedBB(x-1.0, y-1.0, z-1.0, x+1.0, y+1.0, z+1.0);
		for (EntityEnderPearl pearl : world.getEntitiesWithinAABB(EntityEnderPearl.class, aabb)) {
			if (pearl.posX == x && pearl.posY == y && pearl.posZ == z) {
				return pearl;
			}
		}
		return null;
	}

	private BlockPos getTargetPos(EntityThrowable entity) {
		Vec3d from = new Vec3d(entity.posX, entity.posY, entity.posZ);
		Vec3d to = new Vec3d(entity.posX+entity.motionX, entity.posY+entity.motionY, entity.posZ+entity.motionZ);
		RayTraceResult result = entity.worldObj.rayTraceBlocks(from, to);
		if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
			return result.getBlockPos();
		}
		return null;
	}
}
