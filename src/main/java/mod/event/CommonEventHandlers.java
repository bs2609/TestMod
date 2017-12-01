package mod.event;

import mod.TestMod;
import mod.portal.Portal;
import mod.portal.PortalUtils;
import mod.util.BlockArea;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = TestMod.MOD_ID)
public class CommonEventHandlers {
	
	@SubscribeEvent
	public static void onThrowableImpact(ProjectileImpactEvent.Throwable event) {
		EntityThrowable thrown = event.getThrowable();
		
		World world = thrown.world;
		if (world.isRemote) return;
		
		if (thrown instanceof EntityEnderPearl) {
			RayTraceResult trace = event.getRayTraceResult();
			if (trace.typeOfHit != RayTraceResult.Type.BLOCK) return;
			
			BlockPos pos = trace.getBlockPos();
			TestMod.getLogger().debug("Event target: " + pos);
			
			IBlockState interior = Portal.getInterior();
			if (world.getBlockState(pos) != interior) return;
			
			// check for portal at this location
			TestMod.getLogger().debug("Searching for portal...");
			BlockArea portal = PortalUtils.isInsidePortal(world, pos);
			if (portal == null) return;
			
			TestMod.getLogger().debug("Found portal: " + portal);
			IBlockState border = Portal.getBorder();
			if (PortalUtils.checkPortal(world, portal, border, interior)) {
				event.setCanceled(true);
				thrown.setDead();
			}
		}
	}
}
