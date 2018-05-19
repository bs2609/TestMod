package mod.model;

import mod.TestMod;
import mod.block.SurrealBlock;
import mod.util.MiscUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class SurrealBlockModel implements IBakedModel {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(TestMod.MOD_ID + ":" + SurrealBlock.NAME);
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(TestMod.MOD_ID, "blocks/" + SurrealBlock.NAME);
	
	private final TextureAtlasSprite particleTexture = ModelLoader.defaultTextureGetter().apply(TEXTURE_LOCATION);
	private final ModelTransformer transformer = new ModelInverter();
	
	@Mod.EventBusSubscriber(value = Side.CLIENT, modid = TestMod.MOD_ID)
	public static class EventHandler {
		
		@SubscribeEvent
		public static void onModelBakeEvent(ModelBakeEvent event) {
			IBakedModel model = event.getModelRegistry().getObject(MODEL_LOCATION);
			if (model != null) {
				event.getModelRegistry().putObject(MODEL_LOCATION, new SurrealBlockModel());
			}
		}
	}
	
	private static IBakedModel getModelForState(IBlockState state) {
		return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(MiscUtils.getClean(state));
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		if (state instanceof IExtendedBlockState) {
			IExtendedBlockState extendedState = (IExtendedBlockState) state;
			IBlockState appearance = extendedState.getValue(SurrealBlock.APPEARANCE);
			BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
			if (appearance != null && appearance.getRenderType() == EnumBlockRenderType.MODEL
					&& (layer == null || appearance.getBlock().canRenderInLayer(appearance, layer))) {
				IBakedModel copiedModel = getModelForState(appearance);
				return transformer.transformQuads(copiedModel.getQuads(appearance, MiscUtils.getReflected(side, EnumFacing.Axis.Y), rand));
			}
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
		return particleTexture;
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
