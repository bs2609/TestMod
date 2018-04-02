package mod.block;

import mod.portal.PortalUtils;
import mod.world.SpawnTeleporter;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;

@SuppressWarnings("deprecation")
public class PortalFieldBlock extends UnobtainableBlock {

	public static final String NAME = "portal_field";

	PortalFieldBlock() {
		super(NAME, Material.PORTAL);
	}

	@SideOnly(Side.CLIENT)
	void initModel() {
		ModelLoader.setCustomStateMapper(this, block -> Collections.emptyMap());
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (!world.isRemote && entity instanceof EntityPlayerMP && PortalUtils.checkEntity(entity)) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			int dim = world.provider.getRespawnDimension(player);
			player.changeDimension(dim, new SpawnTeleporter());
		}
	}
}
