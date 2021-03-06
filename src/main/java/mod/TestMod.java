package mod;

import mod.command.ModCommands;
import mod.network.ModPacketHandler;
import mod.proxy.LoadingProxy;
import mod.util.WorldViewer;
import mod.world.ModDimensions;
import mod.world.gen.ModWorldGenerators;
import net.minecraft.command.ICommand;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(modid = TestMod.MOD_ID, name = TestMod.NAME, version = TestMod.VERSION,
		dependencies = "required-after:forge@[14.23.3.2693,)", acceptedMinecraftVersions = "[1.12.2,1.13)")
public class TestMod {

	public static final String MOD_ID = "testing";
	public static final String NAME = "a test mod";
	public static final String VERSION = "0.4.4";

	@Mod.Instance(MOD_ID)
	public static TestMod instance;

	@SidedProxy(clientSide = "mod.proxy.LoadingProxy$Client", serverSide = "mod.proxy.LoadingProxy$Server")
	public static LoadingProxy proxy;

	private static Logger logger;
	private static Configuration config;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		logger = event.getModLog();
		config = new Configuration(event.getSuggestedConfigurationFile());
		
		ModDimensions.init();
		ModWorldGenerators.register();
		ModPacketHandler.init();
		WorldViewer.init();
		
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
	
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		for (ICommand command : ModCommands.getCommands()) {
			event.registerServerCommand(command);
		}
	}

	public static Logger getLogger() {
		return logger;
	}

	public static Configuration getConfig() {
		return config;
	}
}
