package mod.network;

import mod.TestMod;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ModPacketHandler {
	
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(TestMod.MOD_ID);
	
	private static int packet = 0, handler = 0;
	
	public static void init() {
		registerPackets();
	}
	
	private static void registerPackets() {
		INSTANCE.registerMessage(ChunkRequestPacket.Handler.class, ChunkRequestPacket.class, packet++, Side.SERVER);
		INSTANCE.registerMessage(ChunkDataPacket.Handler.class, ChunkDataPacket.class, packet++, Side.CLIENT);
	}
	
	public static int registerWithHandlers(IDataReceiver<Chunk> receiver, IMessageValidator<ChunkRequestPacket> validator) {
		ChunkDataPacket.Handler.register(handler, receiver);
		ChunkRequestPacket.Handler.register(handler, validator);
		return handler++;
	}
}
