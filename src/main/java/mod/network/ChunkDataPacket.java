package mod.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
	private ByteBuf data;
	
	public ChunkDataPacket() {}
	
	public ChunkDataPacket(int id, Chunk chunk) {
		this.id = id;
		this.data = encode(chunk);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		id = buf.readInt();
		data = buf.retain();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
		buf.writeBytes(data);
	}
	
	private static Chunk decode(ByteBuf buf) {
		
		int x = buf.readInt(), z = buf.readInt();
		int mask = buf.readInt();
		boolean flag = buf.readBoolean();
		
		Chunk chunk = new PartialChunk(null, x, z, flag);
		
		ExtendedBlockStorage[] array = chunk.getBlockStorageArray();
		for (int i = 0; i < array.length; ++i) {
			if ((mask & 1 << i) != 0) {
				array[i] = new ExtendedBlockStorage(i << 4, flag);
				array[i].getData().read(new PacketBuffer(buf));
				buf.readBytes(array[i].getBlockLight().getData());
				if (flag) buf.readBytes(array[i].getSkyLight().getData());
			}
		}
		
		buf.readBytes(chunk.getBiomeArray());
		
		return chunk;
	}
	
	private static ByteBuf encode(Chunk chunk) {
		
		ByteBuf buf = Unpooled.buffer();
		
		buf.writeInt(chunk.x);
		buf.writeInt(chunk.z);
		
		int mask = 0;
		ExtendedBlockStorage[] array = chunk.getBlockStorageArray();
		for (int i = 0; i < array.length; ++i) {
			if (array[i] != Chunk.NULL_BLOCK_STORAGE) {
				mask |= 1 << i;
			}
		}
		buf.writeInt(mask);
		
		boolean flag = chunk.getWorld().provider.hasSkyLight();
		buf.writeBoolean(flag);
		
		for (ExtendedBlockStorage storage : array) {
			if (storage != Chunk.NULL_BLOCK_STORAGE) {
				storage.getData().write(new PacketBuffer(buf));
				buf.writeBytes(storage.getBlockLight().getData());
				if (flag) buf.writeBytes(storage.getSkyLight().getData());
			}
		}
		
		buf.writeBytes(chunk.getBiomeArray());
		
		return buf;
	}
	
	public static class Handler implements IMessageHandler<ChunkDataPacket, IMessage> {
		
		static final Map<Integer, IDataReceiver<Chunk>> chunkHandlers = new HashMap<Integer, IDataReceiver<Chunk>>();
		
		static void register(int id, IDataReceiver<Chunk> destination) {
			chunkHandlers.put(id, destination);
		}
		
		@Override
		public IMessage onMessage(final ChunkDataPacket msg, final MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					IDataReceiver<Chunk> receiver = chunkHandlers.get(msg.id);
					if (receiver != null) {
						receiver.accept(decode(msg.data));
					}
					msg.data.release();
				}
			});
			return null;
		}
	}
}
