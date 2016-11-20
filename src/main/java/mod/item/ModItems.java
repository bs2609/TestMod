package mod.item;

public class ModItems {
	
	public static PortalBuilder portalBuilder;
	public static PortalCompass portalCompass;
	
	public static void init() {
		portalBuilder = new PortalBuilder();
		portalCompass = new PortalCompass();
	}
	
	public static void initModels() {
		portalBuilder.initModel();
		portalCompass.initModel();
	}
}
