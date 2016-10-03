package mod.network;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

public abstract class ChunkBuffer {
	
	private final Long2ObjectMap<Chunk> chunks = new Long2ObjectOpenHashMap<Chunk>(1024);
	
	private final TLongSet pending = new TLongHashSet();
	
	public synchronized Chunk getChunk(int x, int z) {
		long key = ChunkPos.chunkXZ2Int(x, z);
		Chunk chunk = chunks.get(key);
		if (chunk == null && !pending.contains(key)) {
			pending.add(key);
			onMissingChunk(x, z);
		}
		return chunk;
	}
	
	public synchronized void putChunk(int x, int z, Chunk chunk) {
		long key = ChunkPos.chunkXZ2Int(x, z);
		chunks.put(key, chunk);
		if (pending.contains(key)) {
			pending.remove(key);
			onChunkLoad(chunk);
		}
	}
	
	public synchronized void removeChunk(int x, int z) {
		long key = ChunkPos.chunkXZ2Int(x, z);
		chunks.remove(key);
	}
	
	public synchronized void clearChunks() {
		chunks.clear();
	}
	
	protected abstract void onMissingChunk(int x, int z);
	
	protected abstract void onChunkLoad(Chunk chunk);
}
