package mod.block;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

	public static SurrealBlock surrealBlock;
	public static SurrealVoidBlock surrealVoidBlock;
	public static PortalBlock portalBlock;
	public static PortalFieldBlock portalFieldBlock;
	public static PortalFrameBlock portalFrameBlock;
	public static PortalInteriorBlock portalInteriorBlock;

	public static void init() {
		surrealBlock = new SurrealBlock();
		surrealVoidBlock = new SurrealVoidBlock();
		portalBlock = new PortalBlock();
		portalFieldBlock = new PortalFieldBlock();
		portalFrameBlock = new PortalFrameBlock();
		portalInteriorBlock = new PortalInteriorBlock();
	}

	@SideOnly(Side.CLIENT)
	public static void initModels() {
		surrealBlock.initModel();
		surrealVoidBlock.initModel();
		portalBlock.initModel();
		portalFrameBlock.initModel();
	}

	@SideOnly(Side.CLIENT)
	public static void registerHandlers() {
		surrealBlock.registerColourHandler();
	}
}
