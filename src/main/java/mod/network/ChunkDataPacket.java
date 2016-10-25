package mod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;
import java.util.Map;

public class ChunkDataPacket implements IMessage {
	
	private int id;
	private Chunk chunk;
	
	public ChunkDataPacket() {}
	
	public ChunkDataPacket(int id, Chunk chunk) {
		this.id = id;
		this.chunk = chunk;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		id = buffer.readInt();
		chunk = new PartialChunk(null, buffer.readInt(), buffer.readInt());
		int mask = buffer.readInt();
		ExtendedBlockStorage[] array = chunk.getBlockStorageArray();
		for (int i = 0; i < array.length; ++i) {
			if ((mask & 1 << i) != 0) {
				array[i] = new ExtendedBlockStorage(i << 4, false);
				array[i].getData().read(buffer);
			}
		}
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeInt(id);
		buffer.writeInt(chunk.xPosition);
		buffer.writeInt(chunk.zPosition);
		int mask = 0;
		ExtendedBlockStorage[] array = chunk.getBlockStorageArray();
		for (int i = 0; i < array.length; ++i) {
			if (array[i] != Chunk.NULL_BLOCK_STORAGE) {
				mask |= 1 << i;
			}
		}
		buffer.writeInt(mask);
		for (int i = 0; i < array.length; ++i) {
			if (array[i] != Chunk.NULL_BLOCK_STORAGE) {
				array[i].getData().write(buffer);
			}
		}
	}
	
	public static class Handler implements IMessageHandler<ChunkDataPacket, IMessage> {
		
		private static final Map<Integer, ChunkBuffer> chunkHandlers = new HashMap<Integer, ChunkBuffer>();
		
		private static int id = 0;
		
		public static int register(ChunkBuffer destination) {
			chunkHandlers.put(id, destination);
			return id++;
		}
		
		@Override
		public IMessage onMessage(final ChunkDataPacket msg, final MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					chunkHandlers.get(msg.id).putChunk(msg.chunk.xPosition, msg.chunk.zPosition, msg.chunk);
				}
			});
			return null;
		}
	}
}
