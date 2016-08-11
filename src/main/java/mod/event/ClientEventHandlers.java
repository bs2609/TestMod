package mod.event;

import mod.block.SurrealBlockModel;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventHandlers {

	@SubscribeEvent
	public void onModelBakeEvent(ModelBakeEvent event) {
		Object object = event.getModelRegistry().getObject(SurrealBlockModel.modelResourceLocation);
		if (object != null) {
			SurrealBlockModel model = new SurrealBlockModel();
			event.getModelRegistry().putObject(SurrealBlockModel.modelResourceLocation, model);
		}
	}
}
