package mod.model;

import mod.gui.IDebugInfo;
import mod.gui.ModDebugInfo;
import mod.util.ReferenceCache;

public abstract class ModelCache<T> extends ReferenceCache<T, T> implements IDebugInfo {
	
	ModelCache(String id) {
		ModDebugInfo.register(id, this);
	}
	
	@Override
	public String getInfoText() {
		return "access: " + accessCount.get() + ", miss: " + missCount.get() + ", size: " + map.size();
	}
}
