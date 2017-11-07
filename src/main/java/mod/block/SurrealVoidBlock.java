package mod.block;

import mod.util.WorldViewer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.Map;

@SuppressWarnings("deprecation")
public class SurrealVoidBlock extends UnobtainableBlock {
	
	public static final String NAME = "surreal_void";
	
	SurrealVoidBlock() {
		super(NAME, Material.AIR);
	}
	
	@SideOnly(Side.CLIENT)
	void initModel() {
		IStateMapper empty = new IStateMapper() {
			@Override
			public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block) {
				return Collections.emptyMap();
			}
		};
		ModelLoader.setCustomStateMapper(this, empty);
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
		return NULL_AABB;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
		return false;
	}
	
	@Override
	public boolean isReplaceable(IBlockAccess access, BlockPos pos) {
		return true;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos) {
		int blockLight = source.getCombinedLight(pos, 0) >> 4 & 15;
		return WorldViewer.instance.getBlockAccess(source).getCombinedLight(getInverted(pos), blockLight);
	}
	
	private static BlockPos getInverted(BlockPos pos) {
		return new BlockPos(pos.getX(), 255-pos.getY(), pos.getZ());
	}
}
