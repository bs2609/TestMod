package mod.world;

import mod.world.gen.*;
import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.IChunkGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DimensionInstances {

	private static final Map<String, IDimensionSpecifier> instanceMap = new HashMap<String, IDimensionSpecifier>();

	static {
		instanceMap.put("cube", new VoidCube());
		instanceMap.put("islands", new FloatingIslands());
		instanceMap.put("ocean", new OceanChambers());
		instanceMap.put("ice", new IceSieve());
		instanceMap.put("pillars", new BridgedPillars());
		instanceMap.put("pyramids", new PyramidLand());
		instanceMap.put("walkways", new EndlessWalkways());
	}

	public static IDimensionSpecifier getInstance(String name) {
		return instanceMap.get(name);
	}

	public static class VoidCube implements IDimensionSpecifier {

		private VoidCube() {}

		@Override
		public BiomeProvider getBiomeProvider(World world) {
			return new BiomeProviderSingle(Biomes.SKY);
		}

		@Override
		public IChunkGenerator getChunkGenerator(World world) {
			List<ITerrainGenerator> generators = new ArrayList<ITerrainGenerator>();
			generators.add(new SpongeGenerator(world.getSeed()));
			return new TestChunkGenerator(world, generators);
		}
	}

	public static class FloatingIslands implements IDimensionSpecifier {

		private FloatingIslands() {}

		@Override
		public BiomeProvider getBiomeProvider(World world) {
			return new BiomeProviderSingle(Biomes.SKY);
		}

		@Override
		public IChunkGenerator getChunkGenerator(World world) {
			List<ITerrainGenerator> generators = new ArrayList<ITerrainGenerator>();
			generators.add(new HemisphereGenerator(world.getSeed()));
			return new TestChunkGenerator(world, generators);
		}
	}

	public static class OceanChambers implements IDimensionSpecifier {

		private OceanChambers() {}

		@Override
		public BiomeProvider getBiomeProvider(World world) {
			return new BiomeProviderSingle(Biomes.DEEP_OCEAN);
		}

		@Override
		public IChunkGenerator getChunkGenerator(World world) {
			List<ITerrainGenerator> generators = new ArrayList<ITerrainGenerator>();
			generators.add(new OceanGenerator());
			generators.add(new ChamberGenerator(world.getSeed()));
			return new TestChunkGenerator(world, generators);
		}
	}

	public static class IceSieve implements IDimensionSpecifier {

		private IceSieve() {}

		@Override
		public BiomeProvider getBiomeProvider(World world) {
			return new BiomeProviderSingle(Biomes.FROZEN_OCEAN);
		}

		@Override
		public IChunkGenerator getChunkGenerator(World world) {
			List<ITerrainGenerator> generators = new ArrayList<ITerrainGenerator>();
			generators.add(new CrossingGenerator(world.getSeed()));
			return new TestChunkGenerator(world, generators);
		}
	}

	public static class BridgedPillars implements IDimensionSpecifier {

		private BridgedPillars() {}

		@Override
		public BiomeProvider getBiomeProvider(World world) {
			return new BiomeProviderSingle(Biomes.OCEAN);
		}

		@Override
		public IChunkGenerator getChunkGenerator(World world) {
			List<ITerrainGenerator> generators = new ArrayList<ITerrainGenerator>();
			generators.add(new PillarGenerator(world.getSeed()));
			return new TestChunkGenerator(world, generators);
		}
	}

	public static class PyramidLand implements IDimensionSpecifier {

		private PyramidLand() {}

		@Override
		public BiomeProvider getBiomeProvider(World world) {
			return new BiomeProviderSingle(Biomes.DESERT);
		}

		@Override
		public IChunkGenerator getChunkGenerator(World world) {
			List<ITerrainGenerator> generators = new ArrayList<ITerrainGenerator>();
			generators.add(new PyramidGenerator(world.getSeed()));
			return new TestChunkGenerator(world, generators);
		}
	}

	public static class EndlessWalkways implements IDimensionSpecifier {

		private EndlessWalkways() {}

		@Override
		public BiomeProvider getBiomeProvider(World world) {
			return new BiomeProviderSingle(Biomes.PLAINS);
		}

		@Override
		public IChunkGenerator getChunkGenerator(World world) {
			List<ITerrainGenerator> generators = new ArrayList<ITerrainGenerator>();
			generators.add(new WalkwayGenerator(world.getSeed()));
			return new TestChunkGenerator(world, generators);
		}
	}
}
