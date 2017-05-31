package mod.world;

import mod.util.MiscUtils;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CachedWorldAccess extends SimpleBlockAccess {
	
	private final int dim;
	
	private World world;
	private Chunk chunk;
	
	public CachedWorldAccess(int dim) {
		this.dim = dim;
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}
	
	@Override
	protected Chunk getChunk(int x, int z) {
		if (chunk == null || chunk.xPosition != x || chunk.zPosition != z) {
			chunk = getWorld().getChunkFromChunkCoords(x, z);
		}
		return chunk;
	}
	
	@Override
	protected World getWorld() {
		if (world == null) {
			world = MiscUtils.worldServerForDimension(dim);
		}
		return world;
	}
	
	private class EventHandler {
		
		@SubscribeEvent
		public void onChunkUnload(ChunkEvent.Unload event) {
			if (chunk != null && chunk.xPosition == event.getChunk().xPosition && chunk.zPosition == event.getChunk().zPosition) {
				chunk = null;
			}
		}
		
		@SubscribeEvent
		public void onWorldUnload(WorldEvent.Unload event) {
			if (dim == event.getWorld().provider.getDimension()) {
				world = null;
			}
		}
	}
}
