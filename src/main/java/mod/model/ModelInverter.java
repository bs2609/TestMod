package mod.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.*;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class ModelInverter extends ModelTransformer {
	
	private final TRSRTransformation transform = new TRSRTransformation(new Vector3f(0.0f, 1.0f, 0.0f), null, new Vector3f(1.0f, -1.0f, 1.0f), null);
	private final Matrix4f matrix = transform.getMatrix();
	
	@Override
	protected BakedQuad transformQuad(BakedQuad quad) {
		
		final VertexFormat format = quad.getFormat();
		
		UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
		QuadRewindingTransformer rewinder = new QuadRewindingTransformer(builder);
		VertexTransformer transformer = new VertexTransformer(rewinder) {
			
			private final Vector4f vec = new Vector4f();
			
			@Override
			public void put(int element, float... data) {
				VertexFormatElement formatElement = format.getElement(element);
				switch (formatElement.getUsage()) {
					case POSITION:
						// set w manually due to bug
						vec.set(data[0], data[1], data[2], 1.0f);
						matrix.transform(vec);
						vec.get(data);
					default:
						parent.put(element, data);
				}
			}
			
			@Override
			public void setQuadOrientation(EnumFacing orientation) {
				parent.setQuadOrientation(getInverted(orientation));
			}
			
			private EnumFacing getInverted(EnumFacing side) {
				return (side.getAxis() == EnumFacing.Axis.Y) ? side.getOpposite() : side;
			}
		};
		
		quad.pipe(transformer);
		return builder.build();
	}
}
