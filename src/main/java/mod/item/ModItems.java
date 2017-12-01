package mod.item;

import mod.TestMod;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = TestMod.MOD_ID)
@GameRegistry.ObjectHolder(TestMod.MOD_ID)
public class ModItems {
	
	public static final PortalBuilder PORTAL_BUILDER = null;
	public static final PortalCompass PORTAL_COMPASS = null;
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
				new PortalBuilder().setRegistryName(TestMod.MOD_ID, PortalBuilder.NAME),
				new PortalCompass().setRegistryName(TestMod.MOD_ID, PortalCompass.NAME)
		);
	}
	
	@Mod.EventBusSubscriber(value = Side.CLIENT, modid = TestMod.MOD_ID)
	public static class ClientHandler {
		
		@SubscribeEvent
		public static void registerModels(ModelRegistryEvent event) {
			PORTAL_BUILDER.initModel();
			PORTAL_COMPASS.initModel();
		}
	}
}
