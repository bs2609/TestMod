package mod.world.gen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.ChunkPrimer;

public abstract class ShapeGenerator extends SeededTerrainGenerator {

	private final IBlockState state;

	protected ShapeGenerator(long seed, IBlockState state) {
		super(seed);
		this.state = state;
	}

	protected abstract class Shape {
		public abstract boolean test(int x, int y, int z);
	}

	public void generate(int x, int z, ChunkPrimer primer) {
		Shape[] shapes = getShapes(x, z);
		buildShapes(shapes, primer);
	}

	protected abstract Shape[] getShapes(int x, int z);

	private void buildShapes(Shape[] shapes, ChunkPrimer primer) {
		if (shapes.length == 0) return;
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				for (int y = 0; y < 256; ++y) {
					for (Shape shape : shapes) {
						if (shape.test(x, y, z)) {
							primer.setBlockState(x, y, z, state);
							break;
						}
					}
				}
			}
		}
	}
}
