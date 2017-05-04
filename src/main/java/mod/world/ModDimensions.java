package mod.world;

import mod.TestMod;
import mod.portal.PortalType;
import mod.portal.PortalUtils;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import java.util.HashMap;
import java.util.Map;

public class ModDimensions {

	private static final String CONFIG_CATEGORY = "dimensions";

	private static final int[] DEFAULT_DIMS = {83, 84, 85, 86, 87, 88};
	private static final String[] DEFAULT_NAMES = {"walkways", "pyramids", "ocean", "ice", "islands", "cube"};

	private static int[] dims;
	private static String[] names;

	public static int DIM_SURREAL = 39;
	public static int DIM_GLITCHED = 101;

	public static DimensionType surreal, glitched, test;

	private static final Map<Integer, IDimensionSpecifier> dimTypes = new HashMap<Integer, IDimensionSpecifier>();

	public static void init() {
		loadFromConfig();
		registerDimensionTypes();
		registerDimensions();
		setupDimensions();
		linkDimensions();
		registerForEvents();
	}

	private static void loadFromConfig() {
		Configuration config = TestMod.getConfig();
		DIM_SURREAL = config.get(CONFIG_CATEGORY, "surreal", DIM_SURREAL).getInt();
		DIM_GLITCHED = config.get(CONFIG_CATEGORY, "glitched", DIM_GLITCHED).getInt();
		dims = config.get(CONFIG_CATEGORY, "ids", DEFAULT_DIMS).getIntList();
		names = config.get(CONFIG_CATEGORY, "names", DEFAULT_NAMES).getStringList();
		if (config.hasChanged()) config.save();
	}

	private static void registerDimensionTypes() {
		surreal = DimensionType.register("???", "_hmm", SurrealWorldProvider.ID, SurrealWorldProvider.class, false);
		glitched = DimensionType.register("101", "_101", GlitchedWorldProvider.ID, GlitchedWorldProvider.class, false);
		test = DimensionType.register("test", "_test", TestWorldProvider.ID, TestWorldProvider.class, false);
	}

	private static void registerDimensions() {
		DimensionManager.registerDimension(DIM_SURREAL, surreal);
		DimensionManager.registerDimension(DIM_GLITCHED, glitched);
		for (int dim : dims) {
			DimensionManager.registerDimension(dim, test);
		}
	}

	private static void setupDimensions() {
		int n = Math.min(dims.length, names.length);
		for (int i = 0; i < n; ++i) {
			dimTypes.put(dims[i], DimensionInstances.getInstance(names[i]));
		}
	}

	private static void linkDimensions() {
		if (dims.length == 0) return;
		PortalUtils.addDimensionMapping(PortalType.IN, 0, dims[0]);
		PortalUtils.addDimensionMapping(PortalType.OUT, dims[0], 0);
		for (int i = 1; i < dims.length; ++i) {
			PortalUtils.addDimensionMapping(PortalType.IN, dims[i-1], dims[i]);
			PortalUtils.addDimensionMapping(PortalType.OUT, dims[i], dims[i-1]);
		}
	}
	
	private static void registerForEvents() {
		MinecraftForge.EVENT_BUS.register(new SurrealWorldUpdater());
	}

	static double getMovementFactor(int dim) {
		for (int i = 0; i < dims.length; ++i) {
			if (dims[i] == dim) return Math.pow(8.0, i+1);
		}
		return 1.0;
	}

	static IDimensionSpecifier getSpecifier(int dim) {
		return dimTypes.get(dim);
	}
}
