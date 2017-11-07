package mod.block;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

	public static SurrealBlock surrealBlock;
	public static SurrealVoidBlock surrealVoid;
	public static PortalBlock portalBlock;
	public static PortalFieldBlock portalField;
	public static PortalFrameBlock portalFrame;
	public static PortalInteriorBlock portalInterior;

	public static void init() {
		surrealBlock = new SurrealBlock();
		surrealVoid = new SurrealVoidBlock();
		portalBlock = new PortalBlock();
		portalField = new PortalFieldBlock();
		portalFrame = new PortalFrameBlock();
		portalInterior = new PortalInteriorBlock();
	}

	@SideOnly(Side.CLIENT)
	public static void initModels() {
		surrealBlock.initModel();
		surrealVoid.initModel();
		portalBlock.initModel();
		portalFrame.initModel();
	}

	@SideOnly(Side.CLIENT)
	public static void registerHandlers() {
		surrealBlock.registerColourHandler();
	}
}
