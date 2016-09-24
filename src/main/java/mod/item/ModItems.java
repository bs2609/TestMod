package mod.item;

public class ModItems {
	
	public static PortalCompass portalCompass;
	
	public static void init() {
		portalCompass = new PortalCompass();
	}
	
	public static void initModels() {
		portalCompass.initModel();
	}
}
