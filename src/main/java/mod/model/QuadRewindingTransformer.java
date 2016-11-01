package mod.model;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;

public class QuadRewindingTransformer implements IVertexConsumer {
	
	private final IVertexConsumer parent;
	private final int elements;
	private final float[][][] buffer;
	
	private int vertex = 0;
	
	public QuadRewindingTransformer(IVertexConsumer parent) {
		this.parent = parent;
		this.elements = parent.getVertexFormat().getElementCount();
		this.buffer = new float[4][elements][];
	}
	
	@Override
	public VertexFormat getVertexFormat() {
		return parent.getVertexFormat();
	}
	
	@Override
	public void setQuadTint(int tint) {
		parent.setQuadTint(tint);
	}
	
	@Override
	public void setQuadOrientation(EnumFacing orientation) {
		parent.setQuadOrientation(orientation);
	}
	
	@Override
	public void setApplyDiffuseLighting(boolean diffuse) {
		parent.setApplyDiffuseLighting(diffuse);
	}
	
	@Override
	public void put(int element, float... data) {
		buffer[vertex][element] = data;
		if (element == elements - 1) {
			vertex++;
		}
		if (vertex == 4) {
			output();
		}
	}
	
	private void output() {
		while (vertex --> 0) {
			for (int element = 0; element < elements; ++element) {
				parent.put(element, buffer[vertex][element]);
			}
		}
	}
}
