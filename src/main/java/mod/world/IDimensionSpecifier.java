package mod.world;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.IChunkGenerator;

public interface IDimensionSpecifier {

	BiomeProvider getBiomeProvider(World world);

	IChunkGenerator getChunkGenerator(World world);
}
