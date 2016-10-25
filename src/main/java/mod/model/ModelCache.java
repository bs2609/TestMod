package mod.model;

import com.google.common.base.Objects;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ModelCache {
	
	private static class ModelKey {
		
		private final IBlockState state;
		private final EnumFacing side;
		private final long rand;
		
		private ModelKey(IBlockState state, EnumFacing side, long rand) {
			this.state = state;
			this.side = side;
			this.rand = rand;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ModelKey modelKey = (ModelKey) o;
			return state == modelKey.state && side == modelKey.side && rand == modelKey.rand;
		}
		
		@Override
		public int hashCode() {
			return Objects.hashCode(state, side, rand);
		}
	}
	
	private final int MAX_ENTRIES;
	
	private final Map<ModelKey, List<BakedQuad>> map = Collections.synchronizedMap(new LinkedHashMap<ModelKey, List<BakedQuad>>(128, 0.75f, true) {
		
		@Override
		protected boolean removeEldestEntry(Map.Entry<ModelKey, List<BakedQuad>> eldest) {
			return size() > MAX_ENTRIES;
		}
	});
	
	public ModelCache(int maxEntries) {
		MAX_ENTRIES = maxEntries;
	}
	
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		ModelKey key = new ModelKey(state, side, rand);
		List<BakedQuad> quads = map.get(key);
		if (quads == null) {
			quads = buildQuads(state, side, rand);
			map.put(key, quads);
		}
		return quads;
	}
	
	protected abstract List<BakedQuad> buildQuads(IBlockState state, EnumFacing side, long rand);
}
