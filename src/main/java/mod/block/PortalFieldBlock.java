package mod.block;

import mod.portal.PortalUtils;
import mod.util.MiscUtils;
import mod.world.SpawnTeleporter;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Random;

@SuppressWarnings("deprecation")
public class PortalFieldBlock extends BasicBlock {

	public static final String NAME = "portalFieldBlock";

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
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (!worldIn.isRemote && entityIn instanceof EntityPlayerMP && PortalUtils.checkEntity(entityIn)) {
			EntityPlayerMP player = (EntityPlayerMP) entityIn;
			int dim = 0;
			WorldServer destination = MiscUtils.worldServerForDimension(dim);
			Teleporter teleporter = new SpawnTeleporter(destination);
			
			worldIn.getMinecraftServer().getPlayerList().transferPlayerToDimension(player, dim, teleporter);
		}
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}
	
	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return null;
	}
}
