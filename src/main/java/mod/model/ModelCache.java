package mod.model;

import net.minecraft.client.renderer.block.model.BakedQuad;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ModelCache<K> {
	
	private final Map<K, Reference<List<BakedQuad>>> map = Collections.synchronizedMap(new IdentityHashMap<K, Reference<List<BakedQuad>>>(160));
	
	private final AtomicInteger accessCount = new AtomicInteger(), missCount = new AtomicInteger();
	
	public List<BakedQuad> getQuads(K key) {
		Reference<List<BakedQuad>> value = map.get(key);
		List<BakedQuad> quads = (value == null) ? null : value.get();
		if (quads == null) {
			missCount.getAndIncrement();
			quads = buildQuads(key);
			map.put(key, new WeakReference<List<BakedQuad>>(quads));
		}
		accessCount.getAndIncrement();
		return quads;
	}
	
	protected abstract List<BakedQuad> buildQuads(K key);
}
