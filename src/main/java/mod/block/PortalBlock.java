package mod.block;

import mod.world.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PortalBlock extends BasicBlock {

	public static final String NAME = "portal_block";
	
	private static class DimensionPos {
		
		final int dimension;
		final BlockPos position;
		
		DimensionPos(int dim, BlockPos pos) {
			dimension = dim;
			position = pos.toImmutable();
		}
	}
	
	private static final Map<UUID, DimensionPos> cachedPositions = new HashMap<UUID, DimensionPos>();

	PortalBlock() {
		super(NAME, Material.ROCK, CreativeTabs.BUILDING_BLOCKS);
		setHardness(1.5f);
		setResistance(5.0f);
	}

	@SideOnly(Side.CLIENT)
	void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && playerIn instanceof EntityPlayerMP && hand == EnumHand.MAIN_HAND) {
			EntityPlayerMP player = (EntityPlayerMP) playerIn;
			int dimension = ModDimensions.DIM_GLITCHED;
			ITeleporter teleporter;
			
			if (dimension == player.dimension) {
				DimensionPos dimPos = cachedPositions.get(player.getUniqueID());
				if (dimPos != null) {
					dimension = dimPos.dimension;
					teleporter = new BlockPosTeleporter(dimPos.position);
					
				} else {
					dimension = world.provider.getRespawnDimension(player);
					teleporter = new SpawnTeleporter();
				}
				
			} else {
				cachedPositions.put(player.getUniqueID(), new DimensionPos(player.dimension, new BlockPos(player)));
				teleporter = new PortalBlockTeleporter();
			}
			
			player.changeDimension(dimension, teleporter);
		}
		return true;
	}
}
