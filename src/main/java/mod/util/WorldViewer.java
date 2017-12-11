package mod.util;

import mod.network.*;
import mod.world.CachedWorldAccess;
import mod.world.ModDimensions;
import mod.world.SimpleBlockAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

public class WorldViewer {
	
	public static final int DIM_ID = 0;
	
	private static WorldViewer instance;
	
	private final ChunkRequestPacket.Validator validator = new ChunkRequestPacket.Validator(DIM_ID);
	
	private final ChunkBuffer buffer = new CachingChunkBuffer() {
		
		private final int id = ModPacketHandler.registerWithHandlers(this, validator);
		
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
	
	@SuppressWarnings("unused")
	private final class EventHandler {
		
		private boolean checkWorld(World world) {
			return world.provider.getDimension() == ModDimensions.DIM_SURREAL;
		}
		
		@SubscribeEvent
		public void onChunkUnload(ChunkEvent.Unload event) {
			World world = event.getWorld();
			if (world.isRemote && checkWorld(world)) {
				Chunk chunk = event.getChunk();
				buffer.removeChunk(chunk.x, chunk.z);
			}
		}
		
		@SubscribeEvent
		public void onChunkWatch(ChunkWatchEvent.Watch event) {
			EntityPlayerMP player = event.getPlayer();
			if (checkWorld(player.world)) {
				ChunkPos pos = event.getChunk();
				validator.add(player, pos.x, pos.z);
			}
		}
		
		@SubscribeEvent
		public void onChunkUnwatch(ChunkWatchEvent.UnWatch event) {
			EntityPlayerMP player = event.getPlayer();
			if (checkWorld(player.world)) {
				ChunkPos pos = event.getChunk();
				validator.remove(player, pos.x, pos.z);
			}
		}
	}
	
	private WorldViewer() {
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}
	
	public static void init() {
		instance = new WorldViewer();
	}
	
	public static WorldViewer get() {
		return instance;
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
