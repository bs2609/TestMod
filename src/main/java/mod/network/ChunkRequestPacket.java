package mod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ChunkRequestPacket implements IMessage {
	
	private int id, dim, x, z;
	
	public ChunkRequestPacket() {}
	
	public ChunkRequestPacket(int id, int dim, int x, int z) {
		this.id = id;
		this.dim = dim;
		this.x = x;
		this.z = z;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		id = buf.readInt();
		dim = buf.readInt();
		x = buf.readInt();
		z = buf.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
		buf.writeInt(dim);
		buf.writeInt(x);
		buf.writeInt(z);
	}
	
	public static class Handler implements IMessageHandler<ChunkRequestPacket, IMessage> {
		@Override
		public IMessage onMessage(final ChunkRequestPacket msg, final MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					EntityPlayerMP player = ctx.getServerHandler().player;
					Chunk chunk = player.mcServer.getWorld(msg.dim).getChunkFromChunkCoords(msg.x, msg.z);
					ModPacketHandler.INSTANCE.sendTo(new ChunkDataPacket(msg.id, chunk), player);
				}
			});
			return null;
		}
	}
}
