package mod.model;

import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ModelTransformer {
	
	public List<BakedQuad> transformQuads(List<BakedQuad> in) {
		if (in.isEmpty()) return Collections.emptyList();
		List<BakedQuad> out = new ArrayList<BakedQuad>(in.size());
		for (BakedQuad quad : in) {
			out.add(transformQuad(quad));
		}
		return out;
	}
	
	protected abstract BakedQuad transformQuad(BakedQuad quad);
}
