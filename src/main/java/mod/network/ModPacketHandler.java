package mod.network;

import mod.TestMod;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ModPacketHandler {
	
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(TestMod.MOD_ID);
	
	private static int id = 0;
	
	public static void init() {
		registerPackets();
	}
	
	private static void registerPackets() {
		INSTANCE.registerMessage(ChunkRequestPacket.Handler.class, ChunkRequestPacket.class, id++, Side.SERVER);
		INSTANCE.registerMessage(ChunkDataPacket.Handler.class, ChunkDataPacket.class, id++, Side.CLIENT);
	}
}
