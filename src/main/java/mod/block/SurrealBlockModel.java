package mod.block;

import mod.TestMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class SurrealBlockModel implements IBakedModel {

	public static final ModelResourceLocation modelResourceLocation = new ModelResourceLocation(TestMod.MOD_ID + ":" + SurrealBlock.NAME);
	
	public static class EventHandler {
		
		@SubscribeEvent
		public void onModelBakeEvent(ModelBakeEvent event) {
			Object object = event.getModelRegistry().getObject(modelResourceLocation);
			if (object != null) {
				event.getModelRegistry().putObject(modelResourceLocation, new SurrealBlockModel());
			}
		}
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
		IExtendedBlockState extendedState = (IExtendedBlockState) state;
		IBlockState appearance = extendedState.getValue(SurrealBlock.APPEARANCE);
		if (appearance != null && appearance.getBlock().canRenderInLayer(appearance, layer)) {
			Minecraft mc = Minecraft.getMinecraft();
			BlockRendererDispatcher blockRendererDispatcher = mc.getBlockRendererDispatcher();
			BlockModelShapes blockModelShapes = blockRendererDispatcher.getBlockModelShapes();
			IBakedModel copiedBlockModel = blockModelShapes.getModelForState(appearance);
			return copiedBlockModel.getQuads(appearance, side, rand);
		}
		return Collections.emptyList();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		String name = TestMod.MOD_ID + ":blocks/" + SurrealBlock.NAME;
		return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(name);
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.NONE;
	}
}
