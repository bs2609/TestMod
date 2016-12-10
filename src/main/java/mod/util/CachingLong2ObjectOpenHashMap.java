package mod.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class CachingLong2ObjectOpenHashMap<V> extends Long2ObjectOpenHashMap<V> {
	
	private long lastKey = 0L;
	private V lastValue = defRetValue;
	
	public CachingLong2ObjectOpenHashMap(int expected, float f) {
		super(expected, f);
	}
	
	public CachingLong2ObjectOpenHashMap(int expected) {
		super(expected);
	}
	
	public CachingLong2ObjectOpenHashMap() {}
	
	@Override
	public V get(long k) {
		if (k == lastKey && lastValue != defRetValue) return lastValue;
		lastKey = k;
		return lastValue = super.get(k);
	}
	
	@Override
	public V remove(long k) {
		if (k == lastKey) lastValue = defRetValue;
		return super.remove(k);
	}
	
	@Override
	public void clear() {
		lastValue = defRetValue;
		super.clear();
	}
}
