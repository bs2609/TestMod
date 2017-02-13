package mod.model;

import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ModelTransformer {
	
	private final ModelCache<List<BakedQuad>> cacheL1 = new ModelCache<List<BakedQuad>>("L1") {
		
		@Override
		protected List<BakedQuad> create(List<BakedQuad> key) {
			return applyTransform(key);
		}
	};
	
	private final ModelCache<BakedQuad> cacheL2 = new ModelCache<BakedQuad>("L2") {
		
		@Override
		protected BakedQuad create(BakedQuad key) {
			return transformQuad(key);
		}
	};
	
	public List<BakedQuad> transformQuads(List<BakedQuad> in) {
		if (in.isEmpty()) return Collections.emptyList();
		return cacheL1.get(in);
	}
	
	private List<BakedQuad> applyTransform(List<BakedQuad> in) {
		List<BakedQuad> out = new ArrayList<BakedQuad>(in.size());
		for (BakedQuad quad : in) {
			out.add(cacheL2.get(quad));
		}
		return out;
	}
	
	protected abstract BakedQuad transformQuad(BakedQuad quad);
}
