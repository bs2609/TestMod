package mod.util;

import mod.network.CachingChunkBuffer;
import mod.network.ChunkBuffer;
import mod.network.ChunkRequestPacket;
import mod.network.ModPacketHandler;
import mod.world.CachedWorldAccess;
import mod.world.ModDimensions;
import mod.world.SimpleBlockAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

public class WorldViewer {
	
	public static final int DIM_ID = 0;
	
	public static final WorldViewer instance = new WorldViewer();
	
	private final ChunkBuffer buffer = new CachingChunkBuffer() {
		
		private final int id = ModPacketHandler.registerWithHandlers(this, new ChunkRequestPacket.Validator(DIM_ID));
		
		@Override
		protected void onMissingChunk(int x, int z) {
			// send request packet
			ModPacketHandler.INSTANCE.sendToServer(new ChunkRequestPacket(id, DIM_ID, x, z));
		}
		
		@Override
		protected void onChunkLoad(Chunk chunk) {
			int x = chunk.x << 4, z = chunk.z << 4;
			// mark chunk for render update
			Minecraft.getMinecraft().world.markBlockRangeForRenderUpdate(x, 0, z, x | 15, 255, z | 15);
		}
	};
	
	private final IBlockAccess bufferAccess = new SimpleBlockAccess() {
		
		@Override
		protected Chunk getChunk(int x, int z) {
			return buffer.getChunk(x, z);
		}
	};
	
	private final IBlockAccess worldAccess = new CachedWorldAccess(DIM_ID);
	
	private final class EventHandler {
		
		private boolean checkWorld(World world) {
			return world.isRemote && world.provider.getDimension() == ModDimensions.DIM_SURREAL;
		}
		
		@SubscribeEvent
		public void onChunkLoad(ChunkEvent.Load event) {
			if (!checkWorld(event.getWorld())) return;
			Chunk chunk = event.getChunk();
			buffer.getChunk(chunk.x, chunk.z);
		}
		
		@SubscribeEvent
		public void onChunkUnload(ChunkEvent.Unload event) {
			if (!checkWorld(event.getWorld())) return;
			Chunk chunk = event.getChunk();
			buffer.removeChunk(chunk.x, chunk.z);
		}
	}
	
	private WorldViewer() {
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}
	
	public IBlockAccess getBlockAccess(IBlockAccess access) {
		return getBlockAccess(MiscUtils.getSide(access));
	}
	
	public IBlockAccess getBlockAccess(Side side) {
		switch (side) {
			case CLIENT:
				return bufferAccess;
			case SERVER:
				return worldAccess;
			default:
				throw new IllegalArgumentException("Invalid side: " + side);
		}
	}
}
