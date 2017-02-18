package mod.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import mod.gui.IDebugInfo;
import mod.gui.ModDebugInfo;

public abstract class ModelCache<T> implements IDebugInfo {
	
	private class Loader extends CacheLoader<T, T> {
		
		@Override
		public T load(T key) {
			return create(key);
		}
	}
	
	private final LoadingCache<T, T> cache;
	
	{
		cache = CacheBuilder.newBuilder()
				.initialCapacity(160)
				.recordStats()
				.weakKeys()
				.weakValues()
				.build(new Loader());
	}
	
	ModelCache(String id) {
		ModDebugInfo.register(id, this);
	}
	
	@Override
	public String getInfoText() {
		CacheStats stats = cache.stats();
		return "access: " + stats.requestCount() + ", miss: " + stats.missCount() + ", size: " + cache.size();
	}
	
	public T get(T key) {
		return cache.getUnchecked(key);
	}
	
	protected abstract T create(T key);
}
