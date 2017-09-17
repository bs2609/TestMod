package mod.block;

import mod.portal.PortalUtils;
import mod.util.MiscUtils;
import mod.world.SpawnTeleporter;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

@SuppressWarnings("deprecation")
public class PortalFieldBlock extends UnobtainableBlock {

	public static final String NAME = "portal_field";

	PortalFieldBlock() {
		super(NAME, Material.PORTAL);
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
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (!world.isRemote && entity instanceof EntityPlayerMP && PortalUtils.checkEntity(entity)) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			int dim = 0;
			WorldServer destination = MiscUtils.getWorld(dim);
			Teleporter teleporter = new SpawnTeleporter(destination);
			
			player.mcServer.getPlayerList().transferPlayerToDimension(player, dim, teleporter);
		}
	}
}
