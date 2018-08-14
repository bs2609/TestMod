package mod.network;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

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
		
		static final Map<Integer, IMessageValidator<ChunkRequestPacket>> validators = new HashMap<Integer, IMessageValidator<ChunkRequestPacket>>();
		
		static void register(int id, IMessageValidator<ChunkRequestPacket> validator) {
			validators.put(id, validator);
		}
		
		@Override
		public IMessage onMessage(final ChunkRequestPacket msg, final MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					IMessageValidator<ChunkRequestPacket> validator = validators.get(msg.id);
					if (validator != null && validator.validate(msg, ctx)) {
						EntityPlayerMP player = ctx.getServerHandler().player;
						Chunk chunk = player.server.getWorld(msg.dim).getChunk(msg.x, msg.z);
						ModPacketHandler.INSTANCE.sendTo(new ChunkDataPacket(msg.id, chunk), player);
					}
				}
			});
			return null;
		}
	}
	
	public static class Validator implements IMessageValidator<ChunkRequestPacket> {
		
		private final int dim;
		
		private final Map<EntityPlayerMP, LongSet> accepted = new WeakHashMap<>();
		
		private static final Function<EntityPlayerMP, LongSet> newSet = p -> new LongOpenHashSet();
		
		public Validator(int dim) {
			this.dim = dim;
		}
		
		public void add(EntityPlayerMP player, int x, int z) {
			accepted.computeIfAbsent(player, newSet).add(ChunkPos.asLong(x, z));
		}
		
		public void remove(EntityPlayerMP player, int x, int z) {
			accepted.computeIfAbsent(player, newSet).remove(ChunkPos.asLong(x, z));
		}
		
		@Override
		public boolean validate(ChunkRequestPacket msg, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			return dim == msg.dim && accepted.computeIfAbsent(player, newSet).remove(ChunkPos.asLong(msg.x, msg.z));
		}
	}
}
