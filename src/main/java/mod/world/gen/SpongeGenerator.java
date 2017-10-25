package mod.world.gen;

import net.minecraft.init.Blocks;

public class SpongeGenerator extends ShapeGenerator {

	class MengerSponge extends Shape {

		private final int iter = 5;
		private final int size = (int) Math.pow(3, iter);
		private final int x0, y0, z0;

		MengerSponge(int x, int z) {
			int n = size / 2;
			x0 = -n - (x << 4);
			y0 = 127 - n;
			z0 = -n - (z << 4);
		}

		@Override
		public boolean test(int x, int y, int z) {
			
			int sx = x-x0, sy = y-y0, sz = z-z0;
			
			if (sx < 0 || sx >= size || sy < 0 || sy >= size || sz < 0 || sz >= size) return false;
			
			for (int i = iter; i --> 0;) {
				int n = (int) Math.pow(3, i);
				boolean bx = sx / n % 3 == 1;
				boolean by = sy / n % 3 == 1;
				boolean bz = sz / n % 3 == 1;
				if (bx && by || bx && bz || by && bz) return false;
			}
			return true;
		}
	}

	public SpongeGenerator(long seed) {
		super(seed, Blocks.PURPUR_BLOCK.getDefaultState());
	}

	@Override
	protected Shape[] getShapes(int x, int z) {
		if (-8 <= x && x < 8 && -8 <= z && z < 8) {
			return new Shape[] {new MengerSponge(x, z)};
		}
		return new Shape[0];
	}
}
