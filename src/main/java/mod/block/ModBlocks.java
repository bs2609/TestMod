package mod.block;

import mod.TestMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = TestMod.MOD_ID)
@GameRegistry.ObjectHolder(TestMod.MOD_ID)
public class ModBlocks {
	
	public static final SurrealBlock SURREAL_BLOCK = null;
	public static final SurrealVoidBlock SURREAL_VOID = null;
	public static final PortalBlock PORTAL_BLOCK = null;
	public static final PortalFieldBlock PORTAL_FIELD = null;
	public static final PortalFrameBlock PORTAL_FRAME = null;
	public static final PortalInteriorBlock PORTAL_INTERIOR = null;
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(
				new SurrealBlock().setRegistryName(TestMod.MOD_ID, SurrealBlock.NAME),
				new SurrealVoidBlock().setRegistryName(TestMod.MOD_ID, SurrealVoidBlock.NAME),
				new PortalBlock().setRegistryName(TestMod.MOD_ID, PortalBlock.NAME),
				new PortalFieldBlock().setRegistryName(TestMod.MOD_ID, PortalFieldBlock.NAME),
				new PortalFrameBlock().setRegistryName(TestMod.MOD_ID, PortalFrameBlock.NAME),
				new PortalInteriorBlock().setRegistryName(TestMod.MOD_ID, PortalInteriorBlock.NAME)
		);
	}
	
	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
				PORTAL_BLOCK.createItemBlock(),
				PORTAL_FRAME.createItemBlock()
		);
	}
	
	@Mod.EventBusSubscriber(value = Side.CLIENT, modid = TestMod.MOD_ID)
	public static class ClientHandler {
		
		@SubscribeEvent
		public static void registerModels(ModelRegistryEvent event) {
			SURREAL_BLOCK.initModel();
			SURREAL_VOID.initModel();
			PORTAL_BLOCK.initModel();
			PORTAL_FIELD.initModel();
			PORTAL_FRAME.initModel();
		}
		
		@SubscribeEvent
		public static void registerColourHandlers(ColorHandlerEvent.Block event) {
			SURREAL_BLOCK.registerColourHandler(event.getBlockColors());
		}
	}
}
