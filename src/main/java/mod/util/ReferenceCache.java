package mod.util;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ReferenceCache<K, V> {
	
	protected final Map<K, Reference<V>> map = Collections.synchronizedMap(new IdentityHashMap<K, Reference<V>>(160));
	
	protected final AtomicInteger accessCount = new AtomicInteger(), missCount = new AtomicInteger();
	
	public V get(K key) {
		Reference<V> reference = map.get(key);
		V value = (reference == null) ? null : reference.get();
		if (value == null) {
			missCount.incrementAndGet();
			value = create(key);
			map.put(key, new WeakReference<V>(value));
		}
		accessCount.incrementAndGet();
		return value;
	}
	
	protected abstract V create(K key);
}
