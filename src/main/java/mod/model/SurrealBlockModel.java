package mod.model;

import mod.TestMod;
import mod.block.SurrealBlock;
import mod.util.MiscUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumBlockRenderType;
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
	private static final String textureName = TestMod.MOD_ID + ":blocks/" + SurrealBlock.NAME;
	
	private static final ModelTransformer transformer = new ModelInverter();
	
	public static class EventHandler {
		
		@SubscribeEvent
		public void onModelBakeEvent(ModelBakeEvent event) {
			IBakedModel model = event.getModelRegistry().getObject(modelResourceLocation);
			if (model != null) {
				event.getModelRegistry().putObject(modelResourceLocation, new SurrealBlockModel());
			}
		}
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		IExtendedBlockState extendedState = (IExtendedBlockState) state;
		IBlockState appearance = extendedState.getValue(SurrealBlock.APPEARANCE);
		if (appearance != null && appearance.getRenderType() == EnumBlockRenderType.MODEL
				&& appearance.getBlock().canRenderInLayer(appearance, MinecraftForgeClient.getRenderLayer())) {
			IBakedModel copiedModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(appearance);
			return transformer.transformQuads(copiedModel.getQuads(appearance, MiscUtils.getReflected(side, EnumFacing.Axis.Y), rand));
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
		return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(textureName);
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
