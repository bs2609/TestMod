package mod.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.IChunkGenerator;

public class TestWorldType extends WorldType {

	public static final String NAME = "mod_custom";

	public TestWorldType() {
		super(NAME);
	}

	@Override
	public BiomeProvider getBiomeProvider(World world) {
		return super.getBiomeProvider(world);
	}

	@Override
	public IChunkGenerator getChunkGenerator(World world, String generatorOptions) {
		return super.getChunkGenerator(world, generatorOptions);
	}
}
