package mod.network;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

public abstract class ChunkBuffer implements IDataReceiver<Chunk> {
	
	private final Long2ObjectMap<Chunk> chunks = new Long2ObjectOpenHashMap<Chunk>(1024);
	
	private final TLongSet pending = new TLongHashSet();
	
	public Chunk getChunk(int x, int z) {
		long key = ChunkPos.asLong(x, z);
		synchronized (chunks) {
			Chunk chunk = chunks.get(key);
			if (chunk != null) return chunk;
		}
		synchronized (pending) {
			if (!pending.contains(key)) {
				pending.add(key);
				onMissingChunk(x, z);
			}
		}
		return null;
	}
	
	public void putChunk(Chunk chunk) {
		long key = ChunkPos.asLong(chunk.x, chunk.z);
		synchronized (chunks) {
			chunks.put(key, chunk);
		}
		synchronized (pending) {
			if (pending.contains(key)) {
				pending.remove(key);
				onChunkLoad(chunk);
			}
		}
	}
	
	public void removeChunk(int x, int z) {
		long key = ChunkPos.asLong(x, z);
		synchronized (chunks) {
			chunks.remove(key);
		}
	}
	
	public void clearChunks() {
		synchronized (chunks) {
			chunks.clear();
		}
	}
	
	@Override
	public void accept(Chunk chunk) {
		putChunk(chunk);
	}
	
	protected abstract void onMissingChunk(int x, int z);
	
	protected abstract void onChunkLoad(Chunk chunk);
}
