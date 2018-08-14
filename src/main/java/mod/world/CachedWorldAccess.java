package mod.world;

import mod.util.MiscUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
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
		if (chunk == null || chunk.x != x || chunk.z != z) {
			chunk = getWorld().getChunk(x, z);
		}
		return chunk;
	}
	
	private World getWorld() {
		if (world == null) {
			world = MiscUtils.getWorld(dim);
		}
		return world;
	}
	
	@Override
	public int getLightFor(EnumSkyBlock type, BlockPos pos) {
		return type == EnumSkyBlock.SKY && !getWorld().provider.hasSkyLight() ? 0 : super.getLightFor(type, pos);
	}
	
	@Override
	public WorldType getWorldType() {
		return getWorld().getWorldType();
	}
	
	@Override
	protected BiomeProvider getBiomeProvider() {
		return getWorld().getBiomeProvider();
	}
	
	private class EventHandler {
		
		@SubscribeEvent
		public void onChunkUnload(ChunkEvent.Unload event) {
			if (chunk != null && chunk.x == event.getChunk().x && chunk.z == event.getChunk().z) {
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
