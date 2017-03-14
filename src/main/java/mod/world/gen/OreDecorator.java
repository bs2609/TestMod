package mod.world.gen;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;

import java.util.Random;

public class OreDecorator extends BiomeDecorator {
	
	@Override
	protected void genDecorations(Biome biome, World world, Random random) {
		generateOres(world, random);
	}
}
