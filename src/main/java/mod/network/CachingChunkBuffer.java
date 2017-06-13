package mod.network;

import net.minecraft.world.chunk.Chunk;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public abstract class CachingChunkBuffer extends ChunkBuffer {
	
	private final ThreadLocal<Reference<Chunk>> lastAccess = new ThreadLocal<Reference<Chunk>>();
	
	@Override
	public Chunk getChunk(int x, int z) {
		Reference<Chunk> ref = lastAccess.get();
		Chunk chunk = ref != null ? ref.get() : null;
		if (chunk == null || chunk.x != x || chunk.z != z) {
			chunk = super.getChunk(x, z);
			lastAccess.set(new WeakReference<Chunk>(chunk));
		}
		return chunk;
	}
}
